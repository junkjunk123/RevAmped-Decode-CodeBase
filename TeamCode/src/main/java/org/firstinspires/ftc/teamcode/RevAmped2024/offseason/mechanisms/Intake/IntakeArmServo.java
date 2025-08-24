package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeArmServo extends HwServo2<IntakeArmServo.ArmState> {
    public IntakeArmServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ArmState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public IntakeArmServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ArmState.TRANSFER, ServoBrand.AXON_MAX);
    }

    public enum ArmState implements HwServo2.ServoState {
        INTAKE(Constants.INTAKE_ARM_INTAKE),
        TRANSFER(Constants.INTAKE_ARM_TRANSFER),
        DROP_TO_HUMAN_PLAYER(Constants.INTAKE_ARM_DROP_TO_HUMAN_PLAYER),
        NEUTRAL_HIGHER(Constants.INTAKE_ARM_MIDDLE_HIGHER),
        NEUTRAL_LOWER(Constants.INTAKE_ARM_MIDDLE_LOWER);

        float position;

        ArmState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }
}
