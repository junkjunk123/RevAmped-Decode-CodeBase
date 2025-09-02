package org.firstinspires.ftc.teamcode.UG.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.UG.Constants;

import revAmped.components.Hardware.HwServo2;

public class GateServo extends HwServo2<GateServo.ArmState> {
    public GateServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ArmState.CLOSE, ServoBrand.AXON_MAX);
    }

    public GateServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ArmState.CLOSE, ServoBrand.AXON_MAX);
    }

    public enum ArmState implements HwServo2.ServoState {
        OPEN(Constants.GATE_OPEN),
        CLOSE(Constants.GATE_CLOSE);

        float position;

        ArmState(float position) {
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
