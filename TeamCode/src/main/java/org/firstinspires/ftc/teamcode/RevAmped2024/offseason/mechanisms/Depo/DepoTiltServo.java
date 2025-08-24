package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class DepoTiltServo extends HwServo2<DepoTiltServo.TiltState> {
    public DepoTiltServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, TiltState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public DepoTiltServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, TiltState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public enum TiltState implements HwServo2.ServoState {
        TRANSFER(Constants.DEPO_ARM_TRANSFER),
        BASKET_DEPO(Constants.DEPO_ARM_BASKET_DEPO),
        SPEC_INTAKE(Constants.DEPO_ARM_SPEC_INTAKE),
        SPEC_DEPO(Constants.DEPO_ARM_SPEC_DEPO);

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
