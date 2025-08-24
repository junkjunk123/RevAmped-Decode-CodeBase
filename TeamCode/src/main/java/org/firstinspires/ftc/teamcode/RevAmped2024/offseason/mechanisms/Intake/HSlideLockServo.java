package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class HSlideLockServo extends HwServo2<HSlideLockServo.LockState> {
    public enum LockState implements ServoState {
        OPEN(Constants.HSLIDE_LOCK_UP),
        CLOSE(Constants.HSLIDE_LOCK_DOWN);

        final float position;

        LockState(float pos) {
            position = pos;
        }

        public float getPosition() {
            return position;
        }
    }

    public HSlideLockServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hlock", setPosition, LockState.CLOSE, ServoBrand.AXON_MICRO);
    }

    public HSlideLockServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hlock", initialPosition, LockState.CLOSE, ServoBrand.AXON_MICRO);
    }

    public void close() {
        setCurrentState(LockState.CLOSE);
    }

    public void open() {
        setCurrentState(LockState.OPEN);
    }
}
