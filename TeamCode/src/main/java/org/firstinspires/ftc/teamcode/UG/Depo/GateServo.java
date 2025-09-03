package org.firstinspires.ftc.teamcode.UG.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwServo2;

public class GateServo extends HwServo2<GateServo.GateState> {
    public GateServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, GateState.CLOSE, ServoBrand.AXON_MAX);
    }

    public GateServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, GateState.CLOSE, ServoBrand.AXON_MAX);
    }

    public enum GateState implements HwServo2.ServoState {
        OPEN(GateServo.OPEN),
        CLOSE(GateServo.CLOSE);

        float position;

        GateState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public static float OPEN = 0f;
    public static float CLOSE = 0f;
}
