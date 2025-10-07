package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.math.LinearFunction;

public class HwUltrasonicSensor extends DistanceSensor implements HwDevice {
    protected AnalogInput distanceSensor;

    public HwUltrasonicSensor(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    public HwUltrasonicSensor(HardwareMap hardwareMap, String id, LinearFunction sensorConversion) {
        super(hardwareMap, id, sensorConversion);
    }

    @Override
    public double updateSensorRead() {
        return distanceSensor.getVoltage();
    }

    @Override
    protected void initialize(HardwareMap hardwareMap) {
        distanceSensor = hardwareMap.get(AnalogInput.class, getId());
    }
}