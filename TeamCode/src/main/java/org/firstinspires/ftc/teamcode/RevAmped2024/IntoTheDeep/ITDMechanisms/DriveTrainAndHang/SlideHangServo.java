package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServo2;

public class SlideHangServo extends HwServo2 {
    public enum SlideHangState implements ServoState{
        ACTIVE(ITDConstants.SLIDEHANGSERVO_ACTIVE),
        INACTIVE(ITDConstants.SLIDEHANGSERVO_INACTIVE);

        final float position;

        SlideHangState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public SlideHangServo(HardwareMap hardwareMap, String id, boolean setPosition, SlideHangState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MICRO);
    }

    public SlideHangServo(HardwareMap hardwareMap, String id, float setPosition, SlideHangState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MICRO);
    }

    public SlideHangState getCurrentState() {
        return (SlideHangState) super.getCurrentState();
    }

    public SlideHangState getInitialState() {
        return (SlideHangState) super.getInitialState();
    }

    public void setCurrentState(SlideHangState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(SlideHangState currentState, float pos) {
        super.setCurrentState(currentState, pos);
    }

    public boolean reached(SlideHangState state) {
        return super.reached(state);
    }

    public boolean reached(SlideHangState[] states) {
        return super.reached(states);
    }

    @NonNull
    @Override
    public String toString() {
        return "SlideHangServo";
    }
}
