package revAmped.components.Hardware;

import static revAmped.components.Misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class HwVoltageSensor implements HwDevice, HwDevice.Sensor<Float> {
    private final VoltageSensor voltageSensor;
    private Float voltage;

    public HwVoltageSensor(HardwareMap hardwareMap) {
        if (!testing) {
            try {
                this.voltageSensor = hardwareMap.voltageSensor.iterator().next();
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        }

        else voltageSensor = null;
        voltage = (float) 0;
    }

    @Override
    public String getId() {
        return "";
    }

    public void update() {
        voltage = null;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.ANALOG;
    }

    public float getVoltage() {
        if (voltage == null ) {
            if (!testing) voltage = (float) voltageSensor.getVoltage();
            else return 1;
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

    @NonNull
    @Override
    public String toString() {
        return getId();
    }

    @Override
    public Float getReading() {
        return getVoltage();
    }
}
