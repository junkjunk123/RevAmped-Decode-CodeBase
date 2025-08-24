package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import java.util.Arrays;
import java.util.HashMap;

import kotlin.Suppress;
import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServoWithVoltage;

public class IntakeFourbarArmServo extends HwServoWithVoltage {
    public enum IntakeArmState implements ServoState{
        INTAKE(ITDConstants.INTAKE_ARM_COLLECT),
        TRANSFER(ITDConstants.INTAKE_ARM_UP),
        EDGE_SAMPLES(ITDConstants.INTAKE_ARM_EDGE_SAMPLES),
        MIDDLE(ITDConstants.INTAKE_ARM_MIDDLE);

        private final float pos;

        IntakeArmState(float pos) {
            this.pos = pos;
        }

        @Override
        public float getPosition() {
            return pos;
        }
    }

    public enum IntakeVoltageState implements ServoVoltageState {
        TRANSFER(ITDConstants.VOLTAGE_INTAKE_ARM_TRANSFER, true, new IntakeArmState[] {IntakeArmState.TRANSFER}),
        PAST_MIDDLE_UP(ITDConstants.VOLTAGE_INTAKE_ARM_MIDDLE, true, new IntakeArmState[] {IntakeArmState.TRANSFER, IntakeArmState.MIDDLE});

        private final double voltage;
        private final boolean greaterThan;
        private final IntakeArmState[] states;

        IntakeVoltageState(double voltage1, boolean greaterThan1, IntakeArmState[] states1) {
            this.voltage = voltage1;
            this.greaterThan = greaterThan1;
            this.states = states1;
        }

        public double getVoltage() {
            return voltage;
        }

        public boolean isGreater() {
            return greaterThan;
        }

        @Override
        public IntakeArmState[] getState() {
            return states;
        }
    }

    public IntakeFourbarArmServo(HardwareMap hardwareMap, String id, boolean setPosition, IntakeArmState state, HwAnalogEncoder analogEncoder) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, state, analogEncoder, ServoBrand.AXON_MINI);
    }

    public IntakeFourbarArmServo(HardwareMap hardwareMap, String id, float setPosition, IntakeArmState state, HwAnalogEncoder analogEncoder) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, state, analogEncoder, ServoBrand.AXON_MINI);
    }

    public void setCurrentState(IntakeArmState state) {
        super.setCurrentState(state);
    }

    public void setCurrentState(IntakeArmState currentState, float position) {
        super.setCurrentState(currentState, position);
    }

    public IntakeArmState getCurrentState() {
        return (IntakeArmState) super.getCurrentState();
    }

    public boolean reached(IntakeVoltageState servoVoltageState) {
        return super.reached(servoVoltageState);
    }

    public void intakeSubAuto() {
        setCurrentState(IntakeArmState.INTAKE, ITDConstants.INTAKE_ARM_AUTO);
    }

    @NonNull
    @Override
    public String toString() {
        return "IntakeArm";
    }
}
