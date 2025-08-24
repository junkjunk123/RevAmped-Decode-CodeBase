package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServoWithVoltage;

public class ConceptVoltageServo extends HwServoWithVoltage<ConceptVoltageServo.State, ConceptVoltageServo.VoltageState> {
    public enum VoltageState implements ServoVoltageState {
        VOLTAGE_STATE1(new VoltageStatePair[]{new VoltageStatePair(State.ONE, State.TWO, 300)}, 1.0, true),
        VOLTAGE_STATE2(new VoltageStatePair[]{new VoltageStatePair(State.ONE, State.THREE, 800),
                new VoltageStatePair(State.TWO, State.THREE, 400)}, 2.0, true);

        //The constructor for a VoltageStatePair is new VoltageStatePair(State previous, State next, double timeout).
        //This is the starting and ending state of the servo movement that the voltage state captures.
        //The timeout is the maximum amount of time we let the sensor run until we just conclude the sensor has bugged.
        //isGreater should be true if the servo has reached if the voltage reading is greater than the threshold.
        public final VoltageStatePair[] voltageStatePair;
        public final double voltage;
        public final boolean isGreater;

        VoltageState(VoltageStatePair[] voltageStatePair, double voltage, boolean isGreater) {
            this.voltageStatePair = voltageStatePair;
            this.voltage = voltage;
            this.isGreater = isGreater;
        }

        @Override
        public boolean isGreater() {
            return isGreater;
        }

        @Override
        public VoltageStatePair[] getStates() {
            return voltageStatePair;
        }

        @Override
        public double getVoltage() {
            return voltage;
        }
    }

    public enum State implements ServoState {
        ONE(5/255f),
        TWO(10/255f),
        THREE(15/255f);

        float pos;

        State(float pos) {
            this.pos = pos;
        }
        @Override
        public float getPosition() {
            return pos;
        }
    }

    public ConceptVoltageServo(HardwareMap hardwareMap, String id, boolean setPosition, State initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, voltage, servoBrand);
    }

    public ConceptVoltageServo(HardwareMap hardwareMap, String id, float setPosition, State initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, voltage, servoBrand);
    }
}
