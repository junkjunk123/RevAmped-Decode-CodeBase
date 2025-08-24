package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import java.util.Arrays;
import java.util.HashMap;

import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.HwServoWithVoltage;
import revAmped.components.Hardware.HwSwitch;

public class DepoClawServo extends HwServo2 {
    public enum ClawState implements HwServo2.ServoState {
        OPEN(ITDConstants.DEPO_CLAW_OPEN, true),
        CLOSED(ITDConstants.DEPO_CLAW_CLOSED, false),
        LOOSE_CLOSED(ITDConstants.DEPO_CLAW_LOOSE_CLOSED, false),
        SUPER_LOOSE_CLOSED(ITDConstants.DEPO_CLAW_SUPER_LOOSE, false),
        SPECIMEN_INTAKE_OPEN(ITDConstants.SPEC_OPEN, true);

        final boolean isOpen;
        final float position;

        ClawState (float position, boolean isOpen) {
            this.isOpen = isOpen;
            this.position = position;
        }

        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public DepoClawServo(HardwareMap hardwareMap, String id, boolean setPosition, ClawState clawState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, clawState, ServoBrand.AXON_MINI);
    }

    public DepoClawServo(HardwareMap hardwareMap, String id, float setPosition, ClawState clawState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, clawState, ServoBrand.AXON_MINI);
    }

    public void open() {
        setCurrentState(ClawState.OPEN);
    }

    public void close() {
        setCurrentState(ClawState.CLOSED);
    }

    public void looseClose() {
        setCurrentState(ClawState.LOOSE_CLOSED);
    }

    public void specimenIntake() {
        setCurrentState(ClawState.SPECIMEN_INTAKE_OPEN);
    }

    public void superLooseClose() {
        setCurrentState(ClawState.SUPER_LOOSE_CLOSED);
    }

    public void setCurrentState(ClawState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(ClawState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public ClawState getCurrentState() {
        return (ClawState) super.getCurrentState();
    }

    public boolean isOpen() {
        return getCurrentState().isOpen();
    }

    public boolean hasSample() {
        return super.reached() && !isOpen();
    }

    public boolean reached(ClawState clawState) {
        return super.reached(clawState);
    }

    public boolean reached(ClawState[] states) {
        return super.reached(states);
    }

    public boolean reachedOpenState() {
        return super.reached() && getCurrentState().isOpen();
    }

    public boolean reachedClosedState() {
        return super.reached() && !getCurrentState().isOpen();
    }

    public ClawState[] openStates() {
        return new ClawState[] {ClawState.OPEN, ClawState.SPECIMEN_INTAKE_OPEN};
    }

    public ClawState[] closedStates() {
        return new ClawState[] {ClawState.CLOSED, ClawState.LOOSE_CLOSED, ClawState.SUPER_LOOSE_CLOSED};
    }

    @NonNull
    @Override
    public String toString() {
        return "depoClaw";
    }
}
