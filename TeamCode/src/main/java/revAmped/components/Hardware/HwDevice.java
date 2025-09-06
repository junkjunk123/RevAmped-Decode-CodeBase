package revAmped.components.Hardware;

import android.nfc.tech.Ndef;

import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

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
        DIGITAL,
        SERVO,
        MOTOR,
        ANALOG,
        COLOR_SENSOR,
        DISTANCE_SENSOR,
        IMU,
        PINPOINT,
        UNKNOWN
    }

    DeviceType getDeviceType();

    default boolean testResponse() {
        return true;
    }
}
