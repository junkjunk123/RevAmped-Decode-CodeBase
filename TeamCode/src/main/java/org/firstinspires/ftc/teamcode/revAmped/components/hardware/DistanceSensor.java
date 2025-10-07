package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.testing;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Optional;

import org.firstinspires.ftc.teamcode.revAmped.components.math.LinearFunction;

/**
 * Abstract class representing a distance sensor.
 * It provides methods to initialize the sensor, update its readings,
 * and retrieve the distance value.
 */
public abstract class DistanceSensor extends CachedSensor.CachedNumericalSensor implements HwDevice, HwDevice.Sensor<Double> {
    private LinearFunction sensorConversion;

    /**
     * Constructor for DistanceSensor.
     * Initializes the sensor with the given hardware map and ID.
     *
     * @param hardwareMap the hardware map to use for initialization
     * @param id the unique identifier for this distance sensor
     */
    protected DistanceSensor(HardwareMap hardwareMap, String id) {
        super(id);
        if (!testing) {
            try {
                initialize(hardwareMap);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        }
    }

    /**
     * Constructor for DistanceSensor with a custom sensor conversion function.
     * Initializes the sensor with the given hardware map, ID, and conversion function.
     *
     * @param hardwareMap the hardware map to use for initialization
     * @param id the unique identifier for this distance sensor
     * @param sensorConversion the linear function to convert raw sensor readings to distance
     */
    protected DistanceSensor(HardwareMap hardwareMap, String id, LinearFunction sensorConversion) {
        super(id);
        this.sensorConversion = sensorConversion;
        if (!testing) {
            try {
                initialize(hardwareMap);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        }
    }

    /**
     * Initializes the distance sensor with the provided hardware map.
     * This method should be implemented by subclasses to set up the sensor.
     *
     * @param hardwareMap the hardware map containing the sensor configuration
     */
    protected abstract void initialize(HardwareMap hardwareMap);

    /**
     * Retrieves the distance measured by the sensor.
     * If the distance has not been calculated yet, it updates the sensor reading
     * and applies the conversion function to compute the distance.
     *
     * @return the distance measured by the sensor
     */
    public Double getDistance() {
        return Optional.of(getReading()).orElse(1.0);
    }

    /**
     * Sets the sensor conversion function.
     * This function is used to convert raw sensor readings to distance values.
     *
     * @param sensorConversion the linear function to convert raw sensor readings to distance
     */
    public void setSensorConversion(LinearFunction sensorConversion) {
        this.sensorConversion = sensorConversion;
    }

    public abstract double updateSensorRead();

    @Override
    protected Double getRaw() {
        return sensorConversion.evaluateAtInput(updateSensorRead());
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.DISTANCE_SENSOR;
    }
}
