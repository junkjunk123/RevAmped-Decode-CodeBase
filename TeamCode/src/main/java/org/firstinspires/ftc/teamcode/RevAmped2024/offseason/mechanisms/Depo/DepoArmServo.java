package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class DepoArmServo extends HwServo2<DepoArmServo.ArmState> {
    public DepoArmServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ArmState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public DepoArmServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ArmState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public enum ArmState implements HwServo2.ServoState {
        TRANSFER(Constants.DEPO_ARM_TRANSFER),
        BASKET_DEPO(Constants.DEPO_ARM_BASKET_DEPO),
        SPEC_INTAKE(Constants.DEPO_ARM_SPEC_INTAKE),
        SPEC_DEPO(Constants.DEPO_ARM_SPEC_DEPO);

        float position;

        ArmState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public static float TRANSFER = 0f;
    public static float BASKET_DEPO = 0f;
    public static float SPEC_INTAKE = 0f;
    public static float SPEC_DEPO = 0f;
}
