package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo2;

public class DepoTurretServo extends HwServo2 {
    public DepoTurretServo(HardwareMap hardwareMap, String id, boolean setPosition, TurretState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MAX);
    }

    public DepoTurretServo(HardwareMap hardwareMap, String id, float setPosition, TurretState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MAX);
    }

    public enum TurretState implements ServoState {
        NEUTRAL(ITDConstants.DEPO_TURRET_NEUTRAL),
        GYRO_RUNG(-1f),
        GYRO_SPECIMEN_INTAKE(-1f),
        AUTO_SPECIMEN_INTAKE(ITDConstants.DEPO_TURRET_AUTO_SPECIMEN_INTAKE),
        AUTO_RUNG(ITDConstants.DEPO_TURRET_AUTO_RUNG),
        AUTO_BASKET(ITDConstants.DEPO_TURRET_AUTO_BASKET),
        GYRO_BASKET(-1f);

        final float position;

        TurretState(float position1) {
            this.position = position1;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public void setCurrentState(TurretState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(TurretState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    @Override
    public TurretState getCurrentState() {
        return (TurretState) super.getCurrentState();
    }

    public float timeToNeutral() {
        return (getPosition() - ITDConstants.DEPO_TURRET_SAFE_POSITION)*255/15;
    }

    public boolean reached(TurretState[] servoState) {
        return super.reached(servoState);
    }

    public boolean reached(TurretState state) {
        return super.reached(state);
    }

    @NonNull
    @Override
    public String toString() {
        return "depoTurret";
    }
}
