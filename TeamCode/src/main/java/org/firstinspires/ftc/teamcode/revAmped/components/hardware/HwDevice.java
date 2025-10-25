package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import android.graphics.Camera;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;

/**
 * Interface representing a hardware device.
 */
public interface HwDevice {

    /**
     * Gets the hardware device ID.
     * @return the hardwareMap name for the device
     */
    String getId();

    /**
     * Updates the hardware device
     */
    void update();

    interface Sensor<T> extends HwDevice {
        /**
         * Gets the current reading from the sensor.
         * @return the current reading
         */
        T getReading();
    }

    interface Servo extends HwDevice {
        /**
         * Sets the position of the servo.
         * @param position the position to set
         */
        void setPosition(float position);

        /**
         * Gets the current position of the servo.
         * @return the current position
         */
        float getPosition();

        @Override
        default DeviceType getDeviceType() {
            return DeviceType.SERVO;
        };
    }

    interface Motor extends HwDevice {
        /**
         * Sets the power of the motor.
         * @param power the power to set
         */
        void setPower(float power);

        /**
         * Gets the current power of the motor.
         * @return the current power
         */
        float getPower();

        @Override
        default DeviceType getDeviceType() {
            return DeviceType.MOTOR;
        };
    }

    enum DeviceType {
        DIGITAL(DigitalChannel.class),
        SERVO(Servo.class),
        MOTOR(Motor.class),
        ANALOG(AnalogInput.class),
        COLOR_SENSOR(ColorSensor.class),
        DISTANCE_SENSOR(DistanceSensor.class),
        IMU(com.qualcomm.robotcore.hardware.IMU.class),
        PINPOINT(GoBildaPinpointDriver.class),
        CAMERA(Camera.class),
        VOLTAGE_SENSOR(VoltageSensor.class),
        UNKNOWN(null);

        public Class<?> getCanonicalClass() {
            return canonicalClass;
        }

        final Class<?> canonicalClass;

        DeviceType(Class<?> canonicalClass) {
            this.canonicalClass = canonicalClass;
        }
    }

    DeviceType getDeviceType();

    default DelayedResult<Boolean> testResponse() {
        return new DelayedResult<>(() -> true);
    }
}
