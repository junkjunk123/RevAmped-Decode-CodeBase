package revAmped.components.Hardware;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import revAmped.components.MathUtil.LinearFunction;

public class Hw2mDistanceSensor extends revAmped.components.Hardware.DistanceSensor implements HwDevice {
    protected DistanceSensor distanceSensor;

    protected Hw2mDistanceSensor(HardwareMap hardwareMap, String id, DistanceSensor distanceSensor) {
        super(hardwareMap, id);
        this.distanceSensor = distanceSensor;
    }

    protected Hw2mDistanceSensor(HardwareMap hardwareMap, String id, LinearFunction sensorConversion, DistanceSensor distanceSensor) {
        super(hardwareMap, id, sensorConversion);
        this.distanceSensor = distanceSensor;
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