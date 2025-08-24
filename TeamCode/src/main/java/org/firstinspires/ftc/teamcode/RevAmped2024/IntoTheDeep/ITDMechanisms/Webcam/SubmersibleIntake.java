package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Webcam;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.Vector;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.util.Range;

public class SubmersibleIntake {
    private final DelayManager delays;
    private final Follower follower;
    private Vector detectionVector;
    private final RobotRevampedITD robot;
    private boolean turnLeft = false;
    private boolean avoidTurningIntoOpposingAlliance = false;
    private boolean intake = false;
    private boolean extendHslide = false;
    private boolean firstTime2 = false;
    private boolean transfer = false;
    private boolean stop = false;
    private boolean startIntakeRegardless = false;
    private final ElapsedTime intakeTimer = new ElapsedTime();
    private final ElapsedTime startTimer = new ElapsedTime();
    private boolean blindIntake = false;
    private boolean restart = false;
    private boolean firstTime3 = false;

    public SubmersibleIntake(DelayManager delays, Follower follower, RobotRevampedITD robot) {
        this.delays = delays;
        this.follower = follower;
        detectionVector = new Vector();
        this.robot = robot;
    }

    public void start(ITDWebcam.DetectionTarget detectionTarget, boolean avoidTurningIntoOpposingAlliance, boolean turnLeft) {
        blindIntake = false;
        firstTime3 = false;
        restart = false;
        detectionVector = new Vector();
        /*
        delays.add(new Delay(200, () -> {
            this.avoidTurningIntoOpposingAlliance = avoidTurningIntoOpposingAlliance;
            this.turnLeft = turnLeft;
            assert robot.limelight != null;
            robot.limelight.start(detectionTarget);
            startTimer.reset();
            startIntakeRegardless = true;
            if (avoidTurningIntoOpposingAlliance) {
                if (turnLeft) {
                    delays.add(new Delay(() -> robot.limelight.getDetectionVector().getMagnitude() >= 5 && getNormalizedAngle(robot.limelight.getDetectionVector()) <= 0, () -> {
                        detectionVector = robot.limelight.getDetectionVector();
                        robot.limelight.pause();
                        delays.add(new Delay(200, this::intakeSample));
                    }));
                } else {
                    delays.add(new Delay(() -> robot.limelight.getDetectionVector().getMagnitude() >= 5 && getNormalizedAngle(robot.limelight.getDetectionVector()) >= 0, () -> {
                        detectionVector = robot.limelight.getDetectionVector();
                        robot.limelight.pause();
                        delays.add(new Delay(200, this::intakeSample));
                    }));
                }
            } else {
                delays.add(new Delay(() -> robot.limelight.getDetectionVector().getMagnitude() >= 5, () -> {
                    detectionVector = robot.limelight.getDetectionVector();
                    robot.limelight.pause();
                    delays.add(new Delay(200, this::intakeSample));
                }));
            }
            transfer = false;
            stop = false;
        }));

         */
    }

    public void start(ITDWebcam.DetectionTarget detectionTarget) {
        start(detectionTarget, false, false);
    }


    private void intakeSample() {
        if (ITDConstants.TICKS_PER_INCH > 0) {
            robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
            startIntakeRegardless = false;
            delays.add(new Delay(400, () -> {
                robot.hslides.runToInches(detectionVector.getMagnitude() * Math.cos(getNormalizedAngle(detectionVector)) - 3);
                robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                delays.add(new Delay(400, () -> {
                    robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
                    robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
                    robot.intake.setPower(1f);
                    intakeTimer.reset();
                    firstTime2 = true;
                    intake = true;
                    delays.add(new Delay(700, () -> extendHslide = true));
                }));
            }));
        }
    }

    public void update() {
        if (!stop) {
            if (!intake) {
                /*
                assert robot.limelight != null;
                if (!robot.limelight.periodCompleted()) {
                    if (startTimer.milliseconds() > 1500 && startIntakeRegardless) {
                        blindIntake();
                        startIntakeRegardless = false;
                    } else {
                        robot.limelight.update(avoidTurningIntoOpposingAlliance, turnLeft);
                    }
                }

                 */
            }

            if (intake) {
                if (extendHslide) {
                    extendFurther();
                    extendHslide = false;
                    delays.add(new Delay(200, () -> extendHslide = true));
                    robot.intake.setPower(1f);
                }

                if (robot.hslides.getTargetPosition() == ITDConstants.HSLIDES_MAX && firstTime2 && !transfer) {
                    firstTime2 = false;
                    delays.add(new Delay(400, () -> {
                        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
                        robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
                        delays.add(new Delay(400, () -> {
                            if (!stop) {
                                robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
                                robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
                                intakeTimer.reset();
                                firstTime2 = true;
                            }
                        }));
                        firstTime3 = false;
                    }));
                }
            }
        }
    }

    private void transfer() {
        extendHslide = false;
        intake = false;
        transfer = true;
        firstTime2 = false;
        firstTime3 = false;
    }

    public void blindIntake() {
        blindIntake(400);
    }

    public void blindIntake(double flipperWait) {
        resume();
        restart = false;
        firstTime3 = true;
        blindIntake = true;
        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
        robot.hslides.runToPosition(ITDConstants.HSLIDES_MAX-200);
        robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
        delays.add(new Delay(flipperWait, () -> {
            robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES);
            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES);
            robot.intake.setPower(1f);
        }));
        extendHslide = true;
        intake = true;
        firstTime2 = true;
        intakeTimer.reset();
        delays.add(new Delay(200, () -> {
            robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
            intakeTimer.reset();
        }));
    }

    public void stop() {
        //assert robot.limelight != null;
        //robot.limelight.pause();
        stop = true;
        reset();
    }

    private void reset() {
        blindIntake = false;
        detectionVector = new Vector();
        intakeTimer.reset();
        startTimer.reset();
        intake = false;
        extendHslide = false;
        firstTime2 = true;
        transfer = false;
        startIntakeRegardless = false;
    }

    public void resume() {
        stop = false;
    }

    private void extendFurther() {
        int currentPos = robot.hslides.getCurrentPosition();
        currentPos = Range.clip(currentPos + 50, ITDConstants.HSLIDES_MAX - 200, ITDConstants.HSLIDES_MAX);
        robot.hslides.runToPosition(currentPos);
    }

    public Vector getDetectionVector() {
        return detectionVector;
    }

    public boolean isStartIntakeRegardless() {
        return startIntakeRegardless;
    }

    public boolean isBlindIntake() {
        return blindIntake;
    }

    public void returnHslides() {
        robot.hslides.runToPosition(250);
    }

    public void kill() {
        stop();
        //robot.limelight.webcam.close();
    }

    private double getNormalizedAngle(Vector vector) {
        return Math.atan2(Math.sin(vector.getTheta()), Math.cos(vector.getTheta()));
    }

    public double getNormalizedAngleDetectionVector() {
        return getNormalizedAngle(detectionVector);
    }
}
