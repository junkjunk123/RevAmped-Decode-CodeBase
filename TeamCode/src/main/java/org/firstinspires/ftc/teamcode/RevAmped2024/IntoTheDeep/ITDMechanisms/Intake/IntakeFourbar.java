package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class IntakeFourbar {
    private final IntakeFourbarArmServo fourbarArm;
    private final IntakeFourbarTiltServo fourbarTilt;
    private final TrapdoorServo trapdoor;
    private final DelayManager delays;

    public enum FourbarState {
        INTAKE,
        TRANSFER,
        EDGE_SAMPLES,
        MIDDLE,
    }
    private FourbarState currentState = FourbarState.TRANSFER;

    public IntakeFourbar(IntakeFourbarArmServo fourbarArm, IntakeFourbarTiltServo fourbarTilt, TrapdoorServo trapdoor, DelayManager delays) {
        this.fourbarArm = fourbarArm;
        this.fourbarTilt = fourbarTilt;
        this.trapdoor = trapdoor;
        this.delays = delays;
    }

    public IntakeFourbar(RobotRevampedITD robot, DelayManager delays) {
        this(robot.intakeArm, robot.intakeTilt, robot.intakeTrapdoor, delays);
    }

    private IntakeFourbarArmServo.IntakeArmState fourbarStateToArmState(FourbarState fourbarState) {
        if (fourbarState == FourbarState.INTAKE) {
            return IntakeFourbarArmServo.IntakeArmState.INTAKE;
        } else if (fourbarState == FourbarState.TRANSFER) {
            return IntakeFourbarArmServo.IntakeArmState.TRANSFER;
        } else if (fourbarState == FourbarState.EDGE_SAMPLES) {
            return IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES;
        } else {
            return IntakeFourbarArmServo.IntakeArmState.MIDDLE;
        }
    }

    public FourbarState getCurrentState() {
        return currentState;
    }

    public IntakeFourbarArmServo.IntakeArmState fourbarStateToArmState() {
        return fourbarStateToArmState(getCurrentState());
    }

    public IntakeFourbarTiltServo.IntakeTiltState fourbarStateToTiltState(FourbarState fourbarState) {
        if (fourbarState == FourbarState.INTAKE) {
            return IntakeFourbarTiltServo.IntakeTiltState.INTAKE;
        } else if (fourbarState == FourbarState.TRANSFER) {
            return IntakeFourbarTiltServo.IntakeTiltState.TRANSFER;
        } else if (fourbarState == FourbarState.EDGE_SAMPLES) {
            return IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES;
        } else {
            return IntakeFourbarTiltServo.IntakeTiltState.MIDDLE;
        }
    }

    public IntakeFourbarTiltServo.IntakeTiltState fourbarStateToTiltState() {
        return fourbarStateToTiltState(getCurrentState());
    }

    public void intake() {
        currentState = FourbarState.INTAKE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
        trapdoor.close();
    }

    public void intakePreloadAuto() {
        currentState = FourbarState.INTAKE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
        fourbarTilt.autoPreloadIntake();
        trapdoor.close();
    }

    public void intakeAuto() {
        currentState = FourbarState.INTAKE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE, ITDConstants.INTAKE_TILT_AUTO_PRELOAD);
        trapdoor.close();
    }

    public void intakeSubAuto() {
        currentState = FourbarState.INTAKE;
        fourbarArm.intakeSubAuto();
        fourbarTilt.autoSubIntake();
        trapdoor.close();
    }

    public void transfer() {
        currentState = FourbarState.TRANSFER;

        if (fourbarArm.getCurrentState() != IntakeFourbarArmServo.IntakeArmState.MIDDLE && fourbarArm.getCurrentState() != IntakeFourbarArmServo.IntakeArmState.TRANSFER) {
            fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            delays.add(new Delay(200, () -> fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER)));
        }
        trapdoor.close();
    }

    public void transfer(double tiltDelay) {
        currentState = FourbarState.TRANSFER;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        delays.add(new Delay(tiltDelay, () -> fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER)));
        trapdoor.close();
    }

    public void edgeSamples() {
        currentState = FourbarState.EDGE_SAMPLES;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES);
        trapdoor.close();
    }

    public void edgeSampleClearing() {
        currentState = FourbarState.EDGE_SAMPLES;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES, ITDConstants.INTAKE_ARM_CLEARING_AUTO);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES, ITDConstants.INTAKE_TILT_CLEARING_AUTO);
    }

    public void middlePosition() {
        currentState = FourbarState.MIDDLE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.MIDDLE);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.MIDDLE);
        trapdoor.close();
    }

    public void middlePositionAuto() {
        currentState = FourbarState.MIDDLE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.MIDDLE);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.MIDDLE);
    }

    public void limelightDetection() {
        currentState = FourbarState.TRANSFER;
        trapdoor.close();
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER, ITDConstants.INTAKE_ARM_LIMELIGHT_DETECTION);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER, ITDConstants.INTAKE_TILT_LIMELIGHT_DETECTION);
    }

    public void setCurrentState(FourbarState fourbarState) {
        if (fourbarState == FourbarState.INTAKE) {
            intake();
        } else if (fourbarState == FourbarState.TRANSFER) {
            transfer();
        } else if (fourbarState == FourbarState.EDGE_SAMPLES) {
            edgeSamples();
        } else if (fourbarState == FourbarState.MIDDLE) {
            middlePosition();
        }
    }

    public void teleopClearing() {
        currentState = FourbarState.MIDDLE;
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE, ITDConstants.INTAKE_ARM_SUB_CLEARING);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
        trapdoor.close();
    }

    public void snipe() {
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES, ITDConstants.INTAKE_ARM_SNIPE);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.EDGE_SAMPLES, ITDConstants.INTAKE_TILT_SNIPE);
    }

    public void shootSample() {
        currentState = FourbarState.TRANSFER;
        trapdoor.close();
        fourbarArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        fourbarTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER, ITDConstants.INTAKE_TILT_SPEC_PASSTHROUGH);
    }
}
