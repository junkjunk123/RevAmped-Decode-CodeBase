package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.revAmped.components.math.LinearFunction;

public class Hw2mDistanceSensor extends org.firstinspires.ftc.teamcode.revAmped.components.hardware.DistanceSensor implements HwDevice {
    protected DistanceSensor distanceSensor;

    protected Hw2mDistanceSensor(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    protected Hw2mDistanceSensor(HardwareMap hardwareMap, String id, LinearFunction sensorConversion) {
        super(hardwareMap, id, sensorConversion);
    }

    @Override
    public double updateSensorRead() {
        return distanceSensor.getDistance(DistanceUnit.INCH);
    }

    @Override
    protected void initialize(HardwareMap hardwareMap) {
        distanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, getId());
    }
}