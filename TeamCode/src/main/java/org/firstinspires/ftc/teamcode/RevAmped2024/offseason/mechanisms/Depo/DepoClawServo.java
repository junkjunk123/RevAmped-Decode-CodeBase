package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class DepoClawServo extends HwServo2<DepoClawServo.ClawState> {
    public DepoClawServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ClawState.OPEN, ServoBrand.GOBILDA_SUPERSPEED);
    }

    public DepoClawServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ClawState.OPEN, ServoBrand.GOBILDA_SUPERSPEED);
    }

    public enum ClawState implements HwServo2.ServoState {
        CLOSED(Constants.DEPO_CLAW_CLOSE),
        OPEN(Constants.DEPO_CLAW_OPEN);

        float position;

        ClawState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public void open() {
        setCurrentState(ClawState.OPEN);
    }

    public void close() {
        setCurrentState(ClawState.CLOSED);
    }

    public void toggle() {
        if (getCurrentState() == ClawState.CLOSED) setCurrentState(ClawState.OPEN);
        else setCurrentState(ClawState.CLOSED);
    }
}
