package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.localization.localizers.PinpointLocalizer;
import com.pedropathing.pathgen.Vector;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SampleAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.MathUtil.LinearFunction;
import revAmped.util.Range;

@TeleOp(name = "ConceptDepoInverseKinematics", group = "Test")
public class ConceptDepositionInverseKinematics extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private ITDRevAmpedGamepad gamepad_1;
    private PoseUpdater poseUpdater;
    private final DepoWristServo.WristState wristState = DepoWristServo.WristState.BASKET_INITIAL;
    private final Position basketPose = new Position(DistanceUnit.INCH, 0,0,0, 0);
    private final double armLength = 1;
    private final double tiltToSampleCenterLength = 1;
    private final LinearFunction VSLIDE_INCH_TO_TICKS = new LinearFunction(1,0);

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this);
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1);
        poseUpdater = new PoseUpdater(hardwareMap, new PinpointLocalizer(hardwareMap, SampleAutoPaths.startPose));
        poseUpdater.setStartingPose(SampleAutoPaths.startPose);
    }

    @Override
    public void RevAmpedLoop() {
        poseUpdater.update();
        gamepad_1.update();
        robot.update();
        robot.drive.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f, gamepad_1.y.isToggled());

        if (gamepad_1.dpad_up.isPress()) {
            robot.intake.setPower(0.5f);
            robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
            Pose currentPose = poseUpdater.getPose();
            double[] solution = inverseKinematics(currentPose);
            robot.depoClaw.close();
            robot.vslides.runToPosition((int) solution[0]);
            scheduler.add(new Delay(() -> robot.vslides.getCurrentPosition() > ITDConstants.VSLIDE_MIN, () -> {
                scheduler.add(new Delay(600, robot.intake::stop));
                robot.depoWrist.setCurrentState(wristState);
                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET, (float) solution[1]);
                robot.depoTilt.setCurrentState(DepoTiltServo.TiltState.HIGH_BASKET, (float) solution[3]);
                scheduler.add(new Delay(robot.depoArm::atBasketDepoForTurret,
                        () -> robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.AUTO_BASKET, (float) solution[4])));
            }));
        }

        if (gamepad_1.a.isPress() && robot.getCycleState() == RobotRevampedITD.CycleState.HIGH_BASKET_DEPO) {
            robot.depoClaw.open();
        }
    }

    @Override
    public void update() {
        robot.update();
    }

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }

    private double angleToServoAngle(double angleRad, double servoRange, double servoNeutralOffset, double min, double max) {
        return Range.clip(angleRad / servoRange + servoNeutralOffset, min, max);
    }

    private double[] inverseKinematics(Pose currentPose) {
        double[] servoAngles = new double[5];
        double deltaX = basketPose.x - currentPose.getX();
        double deltaY = basketPose.y - currentPose.getY();
        double deltaZ = basketPose.z;
        servoAngles[2] = wristState.getPosition();
        Vector globalTurretCorrection = new Vector();
        globalTurretCorrection.setOrthogonalComponents(deltaX, deltaY);
        double robotTurretAngle = globalTurretCorrection.getTheta() - currentPose.getHeading();
        servoAngles[4] = angleToServoAngle(robotTurretAngle, Math.toRadians(355), ITDConstants.DEPO_TURRET_NEUTRAL, ITDConstants.DEPO_TURRET_BASKET_RIGHT, ITDConstants.DEPO_TURRET_BASKET_LEFT);
        double cosArmAngle = deltaY / armLength / Math.sin(robotTurretAngle);
        double armAngle = Math.acos(cosArmAngle);
        servoAngles[1] = angleToServoAngle(armAngle, Math.toRadians(355), ITDConstants.DEPO_ARM_BASKET_AUTO, ITDConstants.DEPO_ARM_NEUTRAL, ITDConstants.DEPO_ARM_HIGH_BASKET);
        double tiltPos = Math.asin((deltaX - deltaY / Math.tan(robotTurretAngle))/tiltToSampleCenterLength);
        servoAngles[3] = angleToServoAngle(tiltPos, Math.toRadians(355), ITDConstants.DEPO_TILT_TRANSFER, ITDConstants.DEPO_TILT_HIGH_BASKET, ITDConstants.DEPO_TILT_AUTO_START);
        servoAngles[0] = VSLIDE_INCH_TO_TICKS.evaluateAtInput(deltaZ - armLength * Math.sin(armAngle) + tiltToSampleCenterLength * Math.sin(tiltPos));
        return servoAngles;
    }
}
