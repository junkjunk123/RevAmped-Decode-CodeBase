package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo2;

public class DepoWristServo extends HwServo2 {
    public DepoWristServo(HardwareMap hardwareMap, String id, boolean setPosition, WristState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MINI);
    }

    public enum WristState implements ServoState{
        LEFT_BASKET(ITDConstants.DEPO_WRIST_LEFT_DIAGONAL_BASKET),
        LEFT_RUNG(ITDConstants.DEPO_TURRET_RUNG_LEFT),
        RIGHT_BASKET(ITDConstants.DEPO_WRIST_RIGHT_DIAGONAL_BASKET),
        RIGHT_RUNG(ITDConstants.DEPO_TURRET_RUNG_RIGHT),
        HIGH_RUNG(ITDConstants.DEPO_WRIST_RUNG),
        BASKET(ITDConstants.DEPO_WRIST_BASKET),
        BASKET_INITIAL(ITDConstants.DEPO_WRIST_BASKET_DEPO_INITIAL),
        TRANSFER(ITDConstants.DEPO_WRIST_TRANSFER),
        GYRO_BASKET(ITDConstants.DEPO_WRIST_BASKET),
        GYRO_RUNG_DOWN(ITDConstants.DEPO_WRIST_RUNG),
        GYRO_RUNG_UP(ITDConstants.WRIST_SPECIMEN_INTAKE),
        DOWN_CLIP(ITDConstants.WRIST_DOWN_CLIP_POSITION),
        GYRO_SPECIMEN_INTAKE(ITDConstants.WRIST_SPECIMEN_INTAKE),
        SPEC_INTAKE(ITDConstants.WRIST_SPECIMEN_INTAKE);

        final float position;

        WristState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public WristState getCurrentState() {
        return (WristState) super.getCurrentState();
    }

    public void setCurrentState(WristState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(WristState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public boolean reached(WristState servoState) {
        return super.reached(servoState);
    }

    public boolean reached(WristState[] servoStates) {
        return super.reached(servoStates);
    }

    @NonNull
    @Override
    public String toString() {
        return "depoWrist";
    }
}
