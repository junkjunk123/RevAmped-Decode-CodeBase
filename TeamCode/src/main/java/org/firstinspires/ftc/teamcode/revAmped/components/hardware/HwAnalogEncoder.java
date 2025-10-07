package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import static revAmped.components.misc.Globals.testing;

import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.AnalogInput;

public class HwAnalogEncoder extends CachedSensor.CachedNumericalSensor implements HwDevice, HwDevice.Sensor<Double> {
    private final AnalogInput voltageSensor;
    private Double voltage;

    public HwAnalogEncoder(HardwareMap hardwareMap, String id) {
        super(id);
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
    protected Double getRaw() {
        return voltageSensor.getVoltage();
    }

    public double getVoltage() {
        return getReading();
    }

    public AnalogInput getVoltageSensor() {
        return voltageSensor;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.ANALOG;
    }
}
