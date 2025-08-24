package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeTurretServo extends HwServo2<IntakeTurretServo.TurretState> {
    public IntakeTurretServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, TurretState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public IntakeTurretServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, TurretState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public enum TurretState implements HwServo2.ServoState {
        INTAKE(Constants.INTAKE_TURRET_NEUTRAL),
        TRANSFER(Constants.INTAKE_TURRET_NEUTRAL),
        DROP_TO_HUMAN_PLAYER(Constants.INTAKE_TURRET_DROP_TO_HUMAN_PLAYER);

        float position;

        TurretState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public void intake(double offsetRad) {
        setCurrentState(TurretState.INTAKE, HwServo2.getPositionFromRad(offsetRad, -Math.PI/2, Math.PI/2, Constants.INTAKE_TURRET_EXTREME_RIGHT, Constants.INTAKE_TURRET_EXTREME_LEFT));
    }
}
