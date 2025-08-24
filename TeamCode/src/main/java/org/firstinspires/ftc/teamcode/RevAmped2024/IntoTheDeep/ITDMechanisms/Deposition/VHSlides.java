package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServoWithVoltage;

public class VHSlides extends HwServoWithVoltage {
    public enum MGNState implements ServoState {
        NEUTRAL(ITDConstants.VH_MGN_NEUTRAL),
        TRANSFER(ITDConstants.VH_MGN_TRANSFER),
        RUNG_DEPO(ITDConstants.VH_MGN_SPECIMEN_DEPO),
        SPECIMEN_INTAKE(ITDConstants.VH_MGN_SPECIMEN_INTAKE),
        BASKET_DEPO(ITDConstants.VH_MGN_HIGH_BASKET),
        GIVE_TO_HUMAN_PLAYER(ITDConstants.VH_MGN_SPECIMEN_INTAKE);

        final float position;

        MGNState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public enum MGNVoltageState implements ServoVoltageState {
        AT_SPEC_DEPO_FOR_TURRET(ITDConstants.VOLTAGE_MGNS_AT_SPEC_DEPO, false, new MGNState[] {MGNState.RUNG_DEPO}),
        AT_SPEC_INTAKE_FOR_TURRET(ITDConstants.VOLTAGE_MGNS_AT_SPEC_INTAKE_FOR_TURRET, true, new MGNState[] {MGNState.SPECIMEN_INTAKE}),
        AT_SPEC_INTAKE_FOR_VSLIDES(ITDConstants.VOLTAGE_MGNS_AT_SPEC_INTAKE_FOR_VSLIDES, true, new MGNState[] {MGNState.SPECIMEN_INTAKE}),
        AT_BASKET_DEPO(ITDConstants.VOLTAGE_MGNS_BASKET_DEPO, false, new MGNState[] {MGNState.BASKET_DEPO});

        final float voltage;
        final boolean greater;
        final MGNState[] states;

        MGNVoltageState(float voltage, boolean greater, MGNState[] states) {
            this.voltage = voltage;
            this.greater = greater;
            this.states = states;
        }


        @Override
        public boolean isGreater() {
            return greater;
        }

        @Override
        public MGNState[] getState() {
            return states;
        }

        @Override
        public double getVoltage() {
            return voltage;
        }
    }

    public VHSlides(HardwareMap hardwareMap, String id, boolean setPosition, MGNState initialState, HwAnalogEncoder mgnVoltage) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, mgnVoltage, ServoBrand.AXON_MINI);
    }

    public VHSlides(HardwareMap hardwareMap, String id, float setPosition, MGNState initialState, HwAnalogEncoder mgnVoltage) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, mgnVoltage, ServoBrand.AXON_MINI);
    }

    public MGNState getCurrentState() {
        return (MGNState) super.getCurrentState();
    }

    public void setCurrentState(MGNState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(MGNState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    /**
     * @param change examples: -2/255f or 2/255f
     */
    public void MGNTransferFinetune(float change) {
        setCurrentState(MGNState.TRANSFER, ITDConstants.VH_MGN_TRANSFER + change);
    }

    public void specDepoDownFinal() {
        setCurrentState(MGNState.RUNG_DEPO, ITDConstants.VHSLIDE_FINAL_RUNG_DEPO);
    }

    public boolean reached(MGNVoltageState state) {
        return super.reached(state);
    }

    public boolean atSpecDepoForTurret() {
        return reached(MGNVoltageState.AT_SPEC_DEPO_FOR_TURRET);
    }

    public boolean atSpecIntakeForTurret() {
        return reached(MGNVoltageState.AT_SPEC_INTAKE_FOR_TURRET);
    }

    public boolean atSpecIntakeForVslides() {
        return reached(MGNVoltageState.AT_SPEC_INTAKE_FOR_VSLIDES);
    }

    @NonNull
    @Override
    public String toString() {
        return "VHSlides";
    }
}
