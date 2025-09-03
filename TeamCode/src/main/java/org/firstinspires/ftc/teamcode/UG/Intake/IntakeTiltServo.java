package org.firstinspires.ftc.teamcode.UG.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.UG.UltimateGoalConstants;

import revAmped.components.Hardware.HwServo2;

public class IntakeTiltServo extends HwServo2<IntakeTiltServo.TiltState> {
    public IntakeTiltServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, TiltState.UP, ServoBrand.AXON_MAX);
    }

    public IntakeTiltServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, TiltState.UP, ServoBrand.AXON_MAX);
    }

    public enum TiltState implements HwServo2.ServoState {
        UP(IntakeTiltServo.UP),
        DOWN(IntakeTiltServo.DOWN);

        float position;

        TiltState(float position) {
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
