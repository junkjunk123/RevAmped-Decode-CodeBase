package revAmped.components.Hardware;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;

import static revAmped.components.Misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

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
