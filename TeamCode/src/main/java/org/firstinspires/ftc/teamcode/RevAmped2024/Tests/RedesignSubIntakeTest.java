package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ITDWebcamNew;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import revAmped.components.CommandScheduler.Delay;
@Disabled
@Autonomous(name = "WebcamIntakeTest",group = "Test")
public class RedesignSubIntakeTest extends ITDRevAmpedOpMode {
    private ITDWebcamNew limelight;
    private RobotRevampedITD robot;
    private Follower follower;
    private PathChain initialDriveForward;
    private final Pose startPose = new Pose(-23.5, 0, Math.toRadians(0));
    private int pathState = 0;
    private ITDWebcamNew.DetectionTarget detectionTarget = ITDWebcamNew.DetectionTarget.Yellow;
    private ITDRevAmpedGamepad gamepad_1;
    private boolean specimen = false;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.AUTO_TEST;
    }

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this);
        limelight = new ITDWebcamNew(hardwareMap, "limelight");
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1);
        follower.setStartingPose(new Pose());
    }

    @Override
    public void RevAmpedInitLoop() {
        gamepad_1.update();
        telemetry.update();

        if (gamepad_1.a.isPress()) {
            detectionTarget = ITDWebcamNew.DetectionTarget.Yellow;
        }

        if (gamepad_1.b.isPress()) {
            detectionTarget = ITDWebcamNew.DetectionTarget.Red;
        }

        if (gamepad_1.x.isPress()) {
            detectionTarget = ITDWebcamNew.DetectionTarget.Blue;
        }

        if (gamepad_1.y.isPress()) {
            specimen = !specimen;
        }

        telemetry.addData("detectionTarget",detectionTarget.name());
        telemetry.addData("specimen",specimen);
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("active",limelight.isActive());
        telemetry.addData("hslidesTarget",robot.hslides.getTargetPosition());
        telemetry.addData("hslidesPos",robot.hslides.getCurrentPosition());
        switch (pathState) {
            case 0:
                if (limelight.isActive()) {
                    Pose currentPose = follower.getPose();
                    limelight.updateTargetSample(follower.getPose().getX());
                    Point correctivePoint = limelight.getTargetSample();

                    if (correctivePoint != null) {
                        telemetry.addData("sample","found");
                        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.MIDDLE);
                        robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES);
                        robot.hslides.runToInches(correctivePoint.getY() - 1);
                        pathState++;
                        limelight.pause();

                        PathChain goToSample;

                        if (specimen) {
                            goToSample = follower.pathBuilder()
                                    .addPath(new BezierPoint(new Point(currentPose.getX(), currentPose.getY() + correctivePoint.getX())))
                                    .setConstantHeadingInterpolation(currentPose.getHeading())
                                    .build();
                        } else {
                            goToSample = follower.pathBuilder()
                                    .addPath(new BezierPoint(new Point(currentPose.getX() + correctivePoint.getX(), correctivePoint.getX())))
                                    .setConstantHeadingInterpolation(currentPose.getHeading())
                                    .build();
                        }

                        follower.followPath(goToSample);
                    }
                }

                break;
            case 1:
                if (robot.hslides.getCurrentPosition() - robot.hslides.getTargetPosition() < 5 && follower.getTranslationalError().getMagnitude() < 1) {
                    pathState++;
                    robot.intake.setPower(1f);
                    robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES);
                    scheduler.add(new Delay(() -> robot.colorSensor.getColor() == limelight.targetColorToDetectionColor(), () -> {
                        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
                        robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
                        robot.intakeTrapdoor.close();
                        robot.hslides.retract();
                        scheduler.add(new Delay(500, () -> robot.intake.setPower(-0.4f)));
                        stop();
                    }));
                }

                break;
        }
    }

    @Override
    public void update() {
        robot.update();
        limelight.update();
        follower.update();
        telemetry.update();
    }

    public void RevAmpedStart() {
        limelight.start(ITDWebcamNew.DetectionTarget.Yellow);
        telemetry.addData("llState", "started");
    }
}
