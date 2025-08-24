package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo2;

public class IntakeFourbarTiltServo extends HwServo2 {
    public enum IntakeTiltState implements ServoState {
        INTAKE(ITDConstants.INTAKE_TILT_COLLECT),
        TRANSFER(ITDConstants.INTAKE_TILT_UP),
        EDGE_SAMPLES(ITDConstants.INTAKE_TILT_EDGE_SAMPLES),
        MIDDLE(ITDConstants.INTAKE_TILT_MIDDLE);

        final float position;

        IntakeTiltState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public IntakeFourbarTiltServo(HardwareMap hardwareMap, String id, boolean setPosition, IntakeTiltState tiltState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, tiltState, ServoBrand.AXON_MINI);
    }

    public IntakeFourbarTiltServo(HardwareMap hardwareMap, String id, float setPosition, IntakeTiltState tiltState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, tiltState, ServoBrand.AXON_MINI);
    }

    @Override
    public IntakeTiltState getInitialState() {
        return (IntakeTiltState) super.getInitialState();
    }

    public void setCurrentState(IntakeTiltState state) {
        super.setCurrentState(state);
    }

    public void setCurrentState(IntakeTiltState state, float position) {
        super.setCurrentState(state, position);
    }

    @Override
    public IntakeTiltState getCurrentState() {
        return (IntakeTiltState) super.getCurrentState();
    }

    public void autoPreloadIntake() {
        setCurrentState(IntakeTiltState.INTAKE, ITDConstants.INTAKE_TILT_AUTO_PRELOAD);
    }

    public void autoSubIntake() {
        setCurrentState(IntakeTiltState.INTAKE, ITDConstants.INTAKE_TILT_AUTO);
    }

    public boolean reached(IntakeTiltState state) {
        return super.reached(state);
    }

    public boolean reached(IntakeTiltState[] states) {
        return super.reached(states);
    }

    @NonNull
    @Override
    public String toString() {
        return "IntakeTilt";
    }
}
