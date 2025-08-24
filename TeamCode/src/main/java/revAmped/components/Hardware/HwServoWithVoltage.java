
package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

public abstract class HwServoWithVoltage extends HwServo2 {
    private final HwAnalogEncoder voltageSensor;
    private Double previousVoltage = null;

    protected interface ServoVoltageState {
        boolean isGreater();
        default boolean reached(double voltage) {
            return (getVoltage() >= voltage && isGreater()) || (getVoltage() <= voltage && !isGreater());
        }
        ServoState[] getState();
        double getVoltage();
    }

    protected HwServoWithVoltage(HardwareMap hardwareMap, String id, boolean setPosition, ServoState initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, servoBrand);
        this.voltageSensor = voltage;
    }

    protected HwServoWithVoltage(HardwareMap hardwareMap, String id, float setPosition, ServoState initialState, HwAnalogEncoder voltage, ServoBrand servoBrand) throws IllegalArgumentException {
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

    protected boolean reached(ServoVoltageState servoVoltageState) {
        boolean correctState = false;
        boolean voltageUpdated = false;

        for (ServoState state : servoVoltageState.getState()) {
            if (state == super.getCurrentState()) {
                correctState = true;
                break;
            }
        }

        if (!correctState) {
            return false;
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
            servo.setPosition(position);
            this.position = position;
        }
    }

    @Override
    public void update() {
        voltageSensor.update();
    }
}