package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SpecAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.GyroHandler;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.SpecimenMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.MathUtil.RGBValue;

@TeleOp(name = "SpecTeleOp", group = "Game")
public class SpecTeleOp extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private ITDRevAmpedGamepad gamepad_1;
    private ITDRevAmpedGamepad gamepad_2;
    private Follower follower;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    private IntakeMethods intake;
    private SpecimenMethods specimenMethods;
    private boolean autoDriving = false;
    private Pose specIntakePose = new Pose(39.75,65,Math.toRadians(255));
    private Pose specDepositPose = new Pose(26.5,28,Math.toRadians(305));
    private PathChain specIntakePath;
    private PathChain specDepositPath;
    private boolean colorSensor = true;

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.None, SpecAutoPaths.startPose);
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1, 0.3f);
        gamepad1.reset();
        gamepad_2 = new ITDRevAmpedGamepad(gamepad2, 0.3f);
        gamepad2.reset();
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(SpecAutoPaths.startPose);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        depositionArm = new DepositionArm(robot, scheduler.delays);
        specIntakePath = follower.pathBuilder()
                .addPath(new Path(new BezierLine(specDepositPose, specIntakePose)))
                .setLinearHeadingInterpolation(specDepositPose.getHeading(), specIntakePose.getHeading(), 0.8)
                .setGlobalDeceleration()
                .build();
        specDepositPath = follower.pathBuilder()
                .addPath(new Path(new BezierLine(specIntakePose, specDepositPose)))
                .setLinearHeadingInterpolation(specIntakePose.getHeading(), specDepositPose.getHeading(), 0.8)
                .setGlobalDeceleration()
                .build();
        intake = new IntakeMethods(depositionArm, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar, telemetry);
        specimenMethods = new SpecimenMethods(depositionArm, new GyroHandler(robot), robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar);
        robot.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void RevAmpedLoop() {
        if (!autoDriving) {
            robot.drive.arcadeDrive(gamepad_1, false);
        }

        if (gamepad_1.dpad_left.isPress()) {
            autoDriving = true;
            if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                follower.followPath(specDepositPath);
            } else {
                if (robot.depoArm.getCurrentState() != DepoArmServo.ArmState.SPECIMEN_INTAKE) specIntake();
                follower.followPath(specIntakePath);
            }
        }

        if (gamepad_1.a.isPress()) {
            autoDriving = false;
            follower.breakFollowing();
            robot.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                upClip();
                robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_INTAKE);
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE) {
                robot.depoClaw.close();
                robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP);
                specDepo();
            }
        }

        if (autoDriving && (gamepad_1.y.isPress() || !follower.isBusy())) {
            autoDriving = false;
            follower.breakFollowing();
            robot.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            toggleSpecStates();
        }

        if (gamepad_1.left_bumper.isPress()) {
            robot.vslides.retract();
            if (robot.getCycleState().isBasketDepo()) {
                robot.depoWrist.setCurrentState(DepoWristServo.WristState.LEFT_BASKET);
            }

            intake.fullExtend();
        }

        if (gamepad_1.x.isPress()) {
            robot.setCycleState(RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER);
            robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, this::specIntake, true);
        }

        if (gamepad_1.right_bumper.isPress()) {
            robot.vslides.retract();
            if (!robot.getCycleState().isBasketDepo()) {
                if (robot.hslides.getExtentionState() != hSlideMotor.ExtensionState.HALF || robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN) {
                    intake.extend(ITDConstants.HSLIDES_MAX / 2);
                } else {
                    robot.hslides.runToPosition(hSlideMotor.MAX_ETENSION);
                }
            } else {
                robot.depoWrist.setCurrentState(DepoWristServo.WristState.RIGHT_BASKET);
            }
        }

        //Intake Goes Down
        if (gamepad_1.b.isPress()) {
            robot.vslides.retract();
            if (!robot.getCycleState().isBasketDepo()) {
                if (intakeFourbar.getCurrentState() != IntakeFourbar.FourbarState.INTAKE) {
                    intake.intakeDown();
                } else {
                    intakeFourbar.teleopClearing();
                    robot.intake.setPower(-0.7f);
                }
            }
        }

        if (gamepad_1.right_trigger_button.isPress() && !robot.getCycleState().isBasketDepo()) {
            intake.edgeSampleIntake();
        }

        //Change alliance colors (for intake)
        if (gamepad_1.dpad_down.isPress()) {
            AutoTeleopCommunication.handleAllianceColor(robot, gamepad_1, gamepad_2);
        }
    }

    @Override
    public void update() {
        robot.update();
        follower.update();
        gamepad_1.update();
        telemetry.addData("alliance",robot.getAllianceColor().name());

        if (colorSensor && robot.intake.getPower() > 0f && robot.getCycleState() == RobotRevampedITD.CycleState.SAMPLE_INTAKE && (intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.INTAKE || intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.EDGE_SAMPLES)) {
            ITDColor.DetectionColor sampleColor = robot.colorSensor.getColor();
            double current = robot.intake.getCurrentAmps();

            if (robot.getAllianceColor() != RobotRevampedITD.AllianceColor.None && sampleColor == robot.allianceColorToDetectionColor() && sampleColor != ITDColor.DetectionColor.None) {
                robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
                colorSensor = false;
                if (gamepad_1 != null && gamepad_2 != null) {
                    gamepad_1.sendGamepadSignals(new RGBValue(1, 1, 1));
                    gamepad_2.sendGamepadSignals(new RGBValue(1, 1, 1));
                }

                if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.IN) {
                    scheduler.add(new Delay(300, () -> robot.intake.setPower(-1f)));
                    transfer();
                }
            } else if ((robot.getAllianceColor() != RobotRevampedITD.AllianceColor.None && sampleColor != ITDColor.DetectionColor.None) || current > 5) {
                colorSensor = false;
                intakeFourbar.middlePosition();
                scheduler.add(new Delay(200, () -> robot.intakeTrapdoor.spitSample(robot.intake, () -> colorSensor = true)));
            }
        }

        telemetry.update();
    }

    public void transfer() {
        colorSensor = true;
        robot.vslides.runToPosition(vSlideMotor.LOW_BASKET + 100);
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        intakeFourbar.middlePosition();
        robot.hslides.retract(() -> {robot.intake.stop(); intakeFourbar.shootSample();});
    }

    public void specIntake() {

    }

    public void specDepo() {

    }

    public void specIntakeFromSpecDepo() {

    }

    public void upClip() {

    }

    public void toggleSpecStates() {
        if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE) robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP);
        else if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_INTAKE);
    }

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }
}
