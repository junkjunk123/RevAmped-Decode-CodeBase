package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class HwAnalogEncoder implements HwDevice {
    private final AnalogInput voltageSensor;
    private Double voltage;
    private final String id;

    public HwAnalogEncoder(HardwareMap hardwareMap, String id) {
        this.id = id;
        this.voltageSensor = hardwareMap.get(AnalogInput.class,id);
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
            voltage = voltageSensor.getVoltage();
        }
        return voltage;
    }

    public AnalogInput getVoltageSensor() {
        return voltageSensor;
    }
}
