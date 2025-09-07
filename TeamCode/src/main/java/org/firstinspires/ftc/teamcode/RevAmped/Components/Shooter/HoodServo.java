package org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwServo2;

public class HoodServo extends HwServo2<HoodServo.HoodState> {
    public HoodServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", setPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public HoodServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", initialPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public enum HoodState implements ServoState {
        REST(HOOD_REST),
        TRACKING(HOOD_REST);

        final float pos;

        HoodState(float pos) {
            this.pos = pos;
        }

        @Override
        public float getPosition() {
            return pos;
        }
    }

    public static float HOOD_REST;
    public static float HOOD_MAX_POS;
    public static float HOOD_MIN_POS;
    public static float HOOD_MAX_RAD;
    public static float HOOD_MIN_RAD;
}
