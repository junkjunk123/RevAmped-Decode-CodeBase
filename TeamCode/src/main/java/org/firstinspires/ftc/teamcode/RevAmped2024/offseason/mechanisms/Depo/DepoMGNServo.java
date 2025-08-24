package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class DepoMGNServo extends HwServo2<DepoMGNServo.MGNState> {
    public DepoMGNServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, MGNState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public DepoMGNServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, MGNState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public enum MGNState implements HwServo2.ServoState {
        TRANSFER(Constants.DEPO_MGN_TRANSFER),
        BASKET_DEPO(Constants.DEPO_MGN_BASKET_DEPO),
        SPEC_INTAKE(Constants.DEPO_MGN_SPEC_INTAKE),
        SPEC_DEPO(Constants.DEPO_MGN_SPEC_DEPO);

        float position;

        MGNState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }
}
