package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeTiltServo extends HwServo2<IntakeTiltServo.TiltState> {
    public IntakeTiltServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, TiltState.TRANSFER, ServoBrand.AXON_MINI);
    }

    public IntakeTiltServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, TiltState.TRANSFER, ServoBrand.AXON_MINI);
    }

    public enum TiltState implements HwServo2.ServoState {
        INTAKE(Constants.INTAKE_TILT_INTAKE),
        TRANSFER(Constants.INTAKE_TILT_TRANSFER),
        DROP_TO_HUMAN_PLAYER(Constants.INTAKE_TILT_DROP_TO_HUMAN_PLAYER),
        NEUTRAL(Constants.INTAKE_TILT_NEUTRAL);

        float position;

        TiltState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }
}
