package org.firstinspires.ftc.teamcode.revAmped.components.hardware;
import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.testing;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.Arrays;

import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;

public abstract class HwServoWithVoltage<T extends Enum<T> & HwServo2.ServoState, S extends Enum<S> & HwServoWithVoltage.ServoVoltageState> extends HwServo2<T> {
    private final HwAnalogEncoder voltageSensor;
    private Double previousVoltage = null;
    public record VoltageStatePair(ServoState previous, ServoState next, double timeout) {}
    private long turnStartTime = 0;

    public interface ServoVoltageState {
        /**
         * @return if the analog encoder must read a value greater than the specified voltage to know that it has arrived -- this essentially says the direction the servo is traveling to the target
         */
        boolean reached(double voltage);
        VoltageStatePair[] getStates();
    }

    protected HwServoWithVoltage(HardwareMap hardwareMap, String id, boolean setPosition, T initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, servoBrand);
        this.voltageSensor = voltage;
    }

    protected HwServoWithVoltage(HardwareMap hardwareMap, String id, float setPosition, T initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, servoBrand);
        this.voltageSensor = voltage;
    }

    public double getVoltage() {
        return voltageSensor.getVoltage();
    }

    public double getVoltage(boolean update) {
        double voltage = getVoltage();
        if (update) previousVoltage = voltage;
        return voltage;
    }

    public boolean reached(S servoVoltageState) {
        boolean voltageUpdated = false;

        ArrayList<VoltageStatePair> validStates = new ArrayList<>(Arrays.asList(servoVoltageState.getStates()));
        validStates.removeIf(s -> s.next() != getCurrentState());

        if (validStates.isEmpty()) return false;
        else {
            validStates.removeIf(s -> s.previous() != getPreviousState());
            for (VoltageStatePair state : validStates) {
                if (MathUtil.millisTime() - turnStartTime < state.timeout()) return true;
            }
        }

        if (previousVoltage == null) {
            previousVoltage = getVoltage();
            voltageUpdated = true;
        }

        boolean voltageSufficient = servoVoltageState.reached(previousVoltage);

        if (voltageSufficient) {
            return true;
        }

        if (!voltageUpdated) {
            previousVoltage = getVoltage();
            return servoVoltageState.reached(previousVoltage);
        }

        return false;
    }

    @Override
    public void setPosition(float position) {
        if (Math.abs(this.position - position) >= 1/255f) {
            previousVoltage = null;
            if (!testing) servo.setPosition(position);
            this.position = position;
            turnStartTime = MathUtil.millisTime();
        }
    }

    @Override
    public void update() {
        voltageSensor.update();
    }
}