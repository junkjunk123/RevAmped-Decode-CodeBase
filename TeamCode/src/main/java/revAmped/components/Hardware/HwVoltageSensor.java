package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class HwVoltageSensor implements HwDevice {
    private final VoltageSensor voltageSensor;
    private Float voltage;

    public HwVoltageSensor(HardwareMap hardwareMap) {
        this.voltageSensor = hardwareMap.voltageSensor.iterator().next();
        voltage = (float) 0;
    }

    @Override
    public String getId() {
        return "";
    }

    public void update() {
        voltage = null;
    }

    public float getVoltage() {
        if (voltage == null) {
            voltage = (float) voltageSensor.getVoltage();
        }

        return voltage;
    }

    public VoltageSensor getVoltageSensor() {
        return voltageSensor;
    }

    public float getVoltageNormalized(float nominalVoltage) {
        return Math.min(nominalVoltage / getVoltage(), 1f);
    }

    public float getVoltageNormalized() {
        return getVoltageNormalized(12.0f);
    }
}
