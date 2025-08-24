package revAmped.components.Hardware;

import static revAmped.components.Misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class HwAnalogEncoder implements HwDevice, HwDevice.Sensor<Double> {
    private final AnalogInput voltageSensor;
    private Double voltage;
    private final String id;

    public HwAnalogEncoder(HardwareMap hardwareMap, String id) {
        this.id = id;
        if (!testing) {
            try {
                this.voltageSensor = hardwareMap.get(AnalogInput.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        }
        else voltageSensor = null;
        voltage = (double) 0;
    }

    @Override
    public String getId() {
        return id;
    }

    public void update() {
        voltage = null;
    }

    public double getVoltage() {
        if (voltage == null) {
            if (!testing) voltage = voltageSensor.getVoltage();
            else return 1.0;
        }

        return voltage;
    }

    public AnalogInput getVoltageSensor() {
        return voltageSensor;
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }

    @Override
    public Double getReading() {
        return getVoltage();
    }
}
