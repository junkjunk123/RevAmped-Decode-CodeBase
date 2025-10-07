package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import static revAmped.components.misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Supplier;

import revAmped.util.Range;

/**
 * Created by zwang on 2/7/2016.
 */
public class HwCRServo implements HwDevice, HwDevice.Motor {
    public final CRServo servo;
    protected float power;
    protected int currentPos = -1;
    protected int encoderBase = 0;
    private final String id;
    protected Encoder encoder;

    /**
     * initialize the motor
     * @param hardwareMap HardwareMap to get motor from
     * @param id name of motor
     * @param direction direction of motor
     */

    public HwCRServo (HardwareMap hardwareMap,
                    String id,
                    DcMotor.Direction direction,
                    Encoder encoder)
            throws IllegalArgumentException
    {
        this.id = id;
        if (!testing) {
            try {
                this.servo = hardwareMap.get(CRServo.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
            this.servo.setDirection(direction);
            this.encoder = encoder;
        } else {
            servo = null;
        }
    }

    public interface Encoder {
        int getPosition();
        double getVelocity();

        static Encoder fromLambdas(Supplier<Integer> position, Supplier<Double> velocity) {
            return new Encoder() {
                @Override
                public int getPosition() {
                    return position.get();
                }

                @Override
                public double getVelocity() {
                    return velocity.get();
                }
            };
        }

        static Encoder external(HwMotor2<?> otherMotor) {
            return new Encoder() {
                @Override
                public int getPosition() {
                    return otherMotor.motor.getCurrentPosition();
                }

                @Override
                public double getVelocity() {
                    return otherMotor.motor.getVelocity();
                }
            };
        }
    }

    public void setPower(float power) {
        if (Math.abs(this.power - power) >= 0.01) {
            if (!testing) servo.setPower(power);
            this.power = power;
        }
    }

    public void setPower(double power) {
        power = Range.clip(power, -1, 1);
        if (Math.abs(this.power - power) >= 0.01) {
            if (!testing) servo.setPower(power);
            this.power = (float) power;
        }
    }

    /**
     * @return motor power
     */
    public float getPower() {
        return power;
    }

    public void stop() {
        setPower(0f);
    }

    /**
     * @return motor encoder value
     */
    public int getCurrentPosition() {
        if (currentPos == -1) {
            if (!testing) currentPos = encoder.getPosition() - encoderBase;
            else return 1;
        }

        return currentPos;
    }

    /**
     * reset the motor encoder value
     */
    public void resetPosition() {
        if (!testing) encoderBase = encoder.getPosition();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        currentPos = -1;
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.SERVO;
    }
}
