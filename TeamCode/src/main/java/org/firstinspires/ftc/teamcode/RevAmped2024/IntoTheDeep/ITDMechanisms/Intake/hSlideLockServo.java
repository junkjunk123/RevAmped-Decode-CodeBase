
package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import android.telephony.ServiceState;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.Hardware.HwServo2;

public class hSlideLockServo extends HwServo2 {
    public enum LockState implements ServoState {
        DOWN(ITDConstants.HSLIDE_LOCK_DOWN),
        UP(ITDConstants.HSLIDE_LOCK_UP),
        DOWN_SLIDES_OUT(ITDConstants.HSLIDE_LOCK_DOWN);

        final float position;

        LockState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public hSlideLockServo(HardwareMap hardwareMap, String id, boolean setPosition, LockState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MICRO);
    }
    public hSlideLockServo(HardwareMap hardwareMap, String id, float setPosition, LockState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MICRO);
    }

    public void setCurrentState(LockState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public void setCurrentState(LockState currentState) {
        super.setCurrentState(currentState);
    }

    public void open() {
        setCurrentState(LockState.UP);
    }

    public void close() {
        setCurrentState(LockState.DOWN);
    }

    public void downHslidesOut() {
        setCurrentState(LockState.DOWN_SLIDES_OUT);
    }

    public LockState getCurrentState() {
        return (hSlideLockServo.LockState) super.getCurrentState();
    }

    public boolean reached(LockState state) {
        return super.reached(state);
    }

    public boolean reached(LockState[] states) {
        return super.reached(states);
    }

    @NonNull
    @Override
    public String toString() {
        return "HslideLock";
    }

    @Override
    public LockState getInitialState() {
        return (LockState) super.getInitialState();
    }
}