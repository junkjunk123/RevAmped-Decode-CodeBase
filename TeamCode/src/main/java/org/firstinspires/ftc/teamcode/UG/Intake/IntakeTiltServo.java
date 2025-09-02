package org.firstinspires.ftc.teamcode.UG.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.UG.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeTiltServo extends HwServo2<IntakeTiltServo.ArmState> {
    public IntakeTiltServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ArmState.UP, ServoBrand.AXON_MAX);
    }

    public IntakeTiltServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ArmState.UP, ServoBrand.AXON_MAX);
    }

    public enum ArmState implements HwServo2.ServoState {
        UP(Constants.INTAKE_UP),
        DOWN(Constants.INTAKE_DOWN);

        float position;

        ArmState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public static float UP = 0f;
    public static float DOWN = 0f;
}
