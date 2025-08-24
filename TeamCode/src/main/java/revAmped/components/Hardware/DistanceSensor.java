package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.MathUtil.LinearFunction;

public abstract class DistanceSensor implements HwDevice {
    protected Double distance = 0.0;
    private LinearFunction sensorConversion;
    private final String id;

    protected DistanceSensor(HardwareMap hardwareMap, String id) {
        this.id = id;
        initialize(hardwareMap);
    }

    protected DistanceSensor(HardwareMap hardwareMap, String id, LinearFunction sensorConversion) {
        this.id = id;
        this.sensorConversion = sensorConversion;
        initialize(hardwareMap);
    }

    public abstract double updateSensorRead();
    protected abstract void initialize(HardwareMap hardwareMap);

    public Double getDistance() {
        if (distance == null) distance = sensorConversion.evaluateAtInput(updateSensorRead());
        return distance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        distance = null;
    }

    public void setSensorConversion(LinearFunction sensorConversion) {
        this.sensorConversion = sensorConversion;
    }
}
