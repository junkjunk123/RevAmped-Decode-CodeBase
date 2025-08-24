package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo;

public class DriveTrainHangServo extends HwServo {
    public enum DTHangState {
        LATCHED,
        PULLING,
        UNLATCHED
    }
    private DTHangState currentState = DTHangState.LATCHED;

    public DriveTrainHangServo(HardwareMap hardwareMap, String id) throws IllegalArgumentException {
        super(hardwareMap, id);
    }

    public DTHangState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DTHangState currentState) {
        this.currentState = currentState;
        float set = DTHangStateToPosition(currentState);

        if (set != -1f) {
            setPosition(set);
        }
    }

    private float DTHangStateToPosition(DTHangState state) {
        if (state == DTHangState.LATCHED) {
            return ITDConstants.DTHANGSERVO_CLOSED;
        } else if (state == DTHangState.PULLING) {
            return ITDConstants.DTHANGSERVO_FULLY_CLOSED;
        } else if (state == DTHangState.UNLATCHED) {
            return ITDConstants.DTHANGSERVO_UNLATCHED;
        } else {
            return -1f;
        }
    }

    public void setCurrentState(DTHangState currentState, float pos) {
        this.currentState = currentState;
        setPosition(pos);
    }

    @NonNull
    @Override
    public String toString() {
        return "HangServo";
    }
}
