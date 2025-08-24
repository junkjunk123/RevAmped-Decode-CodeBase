
package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_BASKET_AUTO;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_HIGH_BASKET;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_HIGH_RUNG;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_NEUTRAL;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_SPECIMEN_INTAKE;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.DEPO_ARM_TRANSFER;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.VOLTAGE_DEPO_ARM_AUTO_TURRET;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.VOLTAGE_DEPO_ARM_BASKET_DEPO_FOR_WRIST;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.VOLTAGE_DEPO_ARM_SPEC_INTAKE;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants.VOLTAGE_DEPO_ARM_TRANSFER;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.HwServoWithVoltage;

public class DepoArmServo extends HwServoWithVoltage {
    public DepoArmServo(HardwareMap hardwareMap, String id, boolean setPosition, ArmState initialState, HwAnalogEncoder voltage) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, voltage, ServoBrand.AXON_MAX);
    }

    public DepoArmServo(HardwareMap hardwareMap, String id, float setPosition, ArmState initialState, HwAnalogEncoder voltage) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, voltage, ServoBrand.AXON_MAX);
    }

    public enum ArmState implements HwServo2.ServoState {
        LOW_BASKET(DEPO_ARM_HIGH_BASKET),
        HIGH_BASKET(DEPO_ARM_HIGH_BASKET),
        HIGH_RUNG(DEPO_ARM_HIGH_RUNG),
        NEUTRAL(DEPO_ARM_NEUTRAL),
        TRANSFER(DEPO_ARM_TRANSFER),
        SPECIMEN_INTAKE(DEPO_ARM_SPECIMEN_INTAKE),
        GIVE_TO_HUMAN_PLAYER(DEPO_ARM_SPECIMEN_INTAKE),
        HIGH_BASKET_AUTO(DEPO_ARM_BASKET_AUTO);

        @Override
        public float getPosition() {
            return position;
        }

        private final float position;

        ArmState(float position) {
            this.position = position;
        }
    }

    public enum ArmVoltageState implements HwServoWithVoltage.ServoVoltageState {
        AT_BASKET_DEPO_FOR_WRIST(VOLTAGE_DEPO_ARM_BASKET_DEPO_FOR_WRIST, false, new ArmState[] {ArmState.HIGH_BASKET, ArmState.LOW_BASKET}),
        AT_SPEC_INTAKE_FOR_TURRET(VOLTAGE_DEPO_ARM_SPEC_INTAKE, false, new ArmState[] {ArmState.SPECIMEN_INTAKE}),
        AT_BASKET_DEPO_FOR_TURRET(VOLTAGE_DEPO_ARM_AUTO_TURRET, false, new ArmState[] {ArmState.HIGH_BASKET_AUTO}),
        TRANSFER(VOLTAGE_DEPO_ARM_TRANSFER, true, new ArmState[] {ArmState.TRANSFER, ArmState.NEUTRAL});

        private final double voltage;
        private final boolean greaterThan;
        private final ArmState[] states;

        ArmVoltageState(double voltage, boolean greaterThan, ArmState[] states) {
            this.voltage = voltage;
            this.greaterThan = greaterThan;
            this.states = states;
        }

        public double getVoltage() {
            return voltage;
        }

        public boolean isGreater() {
            return greaterThan;
        }

        @Override
        public ArmState[] getState() {
            return states;
        }
    }

    public void setCurrentState(ArmState currentState) {
        super.setCurrentState(currentState);
    }

    public void setCurrentState(ArmState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public ArmState getCurrentState() {
        return (ArmState) super.getCurrentState();
    }

    @Override
    public ArmState getInitialState() {
        return (ArmState) super.getInitialState();
    }

    public void specDepoDownInitial() {
        setCurrentState(ArmState.HIGH_RUNG, ITDConstants.DOWN_RUNG_ARM_INITIAL);
    }

    public void specDepoDownFinal() {
        setCurrentState(ArmState.HIGH_RUNG, ITDConstants.DOWN_RUNG_ARM_FINAL);
    }

    public void specDepoStraightClipFinal() {
        setCurrentState(ArmState.HIGH_RUNG, ITDConstants.DEPO_ARM_RUNG_FINAL);
    }

    public boolean isExteriorState(ArmState state) {
        return state == ArmState.LOW_BASKET || state == ArmState.HIGH_BASKET || state == ArmState.HIGH_RUNG || state == ArmState.SPECIMEN_INTAKE
                || state == ArmState.GIVE_TO_HUMAN_PLAYER;
    }

    public boolean isExteriorState() {
        return isExteriorState(getCurrentState());
    }

    public boolean reached(ArmVoltageState state) {
        return super.reached(state);
    }

    public boolean atBasketDepoForWrist() {
        return reached(ArmVoltageState.AT_BASKET_DEPO_FOR_WRIST);
    }

    public boolean atBasketDepoForTurret() {
        return reached(ArmVoltageState.AT_BASKET_DEPO_FOR_TURRET);
    }

    public boolean atSpecIntakeForTurret() {
        return reached(ArmVoltageState.AT_SPEC_INTAKE_FOR_TURRET);
    }

    public boolean reachedTimeBased(ArmState state) {
        return super.reached(state);
    }

    @NonNull
    @Override
    public String toString() {
        return "depoArm";
    }
}
