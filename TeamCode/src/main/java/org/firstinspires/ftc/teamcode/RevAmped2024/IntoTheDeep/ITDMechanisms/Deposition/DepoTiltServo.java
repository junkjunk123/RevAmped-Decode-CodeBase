
package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServo2;

public class DepoTiltServo extends HwServo2 {
    public enum TiltState implements ServoState {
        SPECIMEN_INTAKE(ITDConstants.DEPO_TILT_SPECIMEN_INTAKE),
        GIVE_TO_HUMAN_PLAYER(ITDConstants.DEPO_TILT_SPECIMEN_INTAKE),
        HIGH_BASKET(ITDConstants.DEPO_TILT_HIGH_BASKET),
        LOW_BASKET(ITDConstants.DEPO_TILT_HIGH_BASKET),
        BASKET_INITIAL(ITDConstants.DEPO_TILT_BASKET_AUTO_INITIAL),
        TELEOP_INITIAL(ITDConstants.DEPO_TILT_TELE_INITIAL),
        NEUTRAL(ITDConstants.DEPO_TILT_NEUTRAL),
        TRANSFER(ITDConstants.DEPO_TILT_TRANSFER),
        HIGH_RUNG(ITDConstants.DEPO_TILT_HIGH_RUNG);

        final float position;

        TiltState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public DepoTiltServo(HardwareMap hardwareMap, String id, boolean setPosition, TiltState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MAX);
    }

    public DepoTiltServo(HardwareMap hardwareMap, String id, float setPosition, TiltState initialState) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, ServoBrand.AXON_MAX);
    }

    public void setCurrentState(TiltState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public void setCurrentState(TiltState state) {
        super.setCurrentState(state);
    }

    public void specDepoDownInitial() {
        setCurrentState(TiltState.HIGH_RUNG, ITDConstants.DOWN_RUNG_TILT_INITIAL);
    }

    public void specDepoDownFinal() {
        setCurrentState(TiltState.HIGH_RUNG, ITDConstants.DOWN_RUNG_TILT_FINAL);
    }

    public void sampleDepoAutoDownFinal() {
        setCurrentState(TiltState.BASKET_INITIAL, ITDConstants.DEPO_TILT_BASKET_AUTO_FINAL);
    }
    public void sampleDepoAutoDownInitial() {
        setCurrentState(TiltState.BASKET_INITIAL);
    }

    public void prepForSpecDepo() {
        setCurrentState(TiltState.SPECIMEN_INTAKE, ITDConstants.DEPO_TILT_PREP_FOR_SPEC_DEPO);
    }

    public TiltState getCurrentState() {
        return (TiltState) super.getCurrentState();
    }

    @Override
    public TiltState getInitialState() {
        return (TiltState) super.getInitialState();
    }

    @NonNull
    @Override
    public String toString() {
        return "depoTilt";
    }

    public boolean reached(TiltState state) {
        return super.reached(state);
    }

    public boolean reached(TiltState[] states) {
        return super.reached(states);
    }
}
