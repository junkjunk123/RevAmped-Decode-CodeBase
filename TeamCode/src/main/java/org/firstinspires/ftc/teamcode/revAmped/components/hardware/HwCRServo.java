package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by zwang on 2/7/2016.
 */
public class HwCRServo implements HwDevice, HwDevice.Motor {
    public final CRServoImplEx servo;
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
                this.servo = hardwareMap.get(CRServoImplEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
            this.servo.setDirection(direction);
            this.encoder = encoder;
        } else {
            servo = null;
        }
    }

    /**
     * initialize the motor
     * @param hardwareMap HardwareMap to get motor from
     * @param id name of motor
     * @param direction direction of motor
     */

    public HwCRServo (HardwareMap hardwareMap,
                      String id,
                      DcMotor.Direction direction)
            throws IllegalArgumentException
    {
        this.id = id;
        if (!testing) {
            try {
                this.servo = hardwareMap.get(CRServoImplEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
            this.servo.setDirection(direction);
        } else {
            servo = null;
        }
    }

    public void setPower(float power) {
        if (Math.abs(this.power - power) >= 0.01) {
            if (!testing) servo.setPower(power);
            this.power = power;
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
            if (encoder == null)
                throw new UnsupportedOperationException("Encoder not defined for motor " + getId());
            if (!testing) currentPos = encoder.getPosition() - encoderBase;
            else return 1;
        }

        return currentPos;
    }

    /**
     * reset the motor encoder value
     */
    public void resetPosition() {
        if (!testing && encoder != null)
            encoderBase = encoder.getPosition();
    }

    public void resetPosition(int currentPos) {
        if (!testing && encoder != null)
            encoderBase = encoder.getPosition() - currentPos;
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
