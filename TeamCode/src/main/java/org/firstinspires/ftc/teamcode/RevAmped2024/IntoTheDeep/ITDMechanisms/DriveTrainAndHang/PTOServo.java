package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServo2;

public class PTOServo extends HwServo2 {
    public enum PTOState implements ServoState{
        ACTIVE(ITDConstants.PTO_ACTIVE),
        INACTIVE(ITDConstants.PTO_INACTIVE);

        final float position;

        PTOState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public PTOServo(HardwareMap hardwareMap, String id, boolean setPosition, PTOState state) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, state, ServoBrand.AXON_MAX);
    }

    public PTOServo(HardwareMap hardwareMap, String id, float setPosition, PTOState state) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, state, ServoBrand.AXON_MAX);
    }

    public PTOState getCurrentState() {
        return (PTOState) super.getCurrentState();
    }

    public void setCurrentState(PTOState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(PTOState currentState, float pos) {
        super.setCurrentState(currentState, pos);
    }

    public void activate() {
        setCurrentState(PTOState.ACTIVE);
    }

    public void stop() {
        setCurrentState(PTOState.INACTIVE);
    }

    public void toggle() {
        if (getCurrentState() == PTOState.ACTIVE) {
            setCurrentState(PTOState.INACTIVE);
        } else if (getCurrentState() == PTOState.INACTIVE) {
            setCurrentState(PTOState.ACTIVE);
        }
    }

    public boolean reached(PTOState state) {
        return super.reached(state);
    }

    public boolean reached(PTOState[] states) {
        return super.reached(states);
    }

    @NonNull
    @Override
    public String toString() {
        return "PTOServo";
    }
}
