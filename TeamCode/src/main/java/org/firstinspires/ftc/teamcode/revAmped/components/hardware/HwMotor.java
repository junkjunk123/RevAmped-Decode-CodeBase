package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;

/**
 * Created by zwang on 2/7/2016.
 */
public class HwMotor implements HwDevice, HwDevice.Motor {
    public final DcMotorEx motor;
    protected float power;
    protected int currentPos = -1;
    protected int encoderBase = 0;
    private final String id;
    private DcMotor.RunMode currentMode;
    private DcMotor.ZeroPowerBehavior currentZPB = ZeroPowerBehavior.FLOAT;
    protected Double current = 0.0;

    /**
     * initialize the motor
     * @param hardwareMap HardwareMap to get motor from
     * @param id name of motor
     * @param direction direction of motor
     */
    public HwMotor (HardwareMap hardwareMap,
                    String id,
                    DcMotor.Direction direction)
            throws IllegalArgumentException
    {
        this(hardwareMap,
             id,
             direction,
             RUN_WITHOUT_ENCODER);
    }

    /**
     * initialize the motor
     * @param hardwareMap HardwareMap to get motor from
     * @param id name of motor
     * @param direction direction of motor
     * @param runMode runMode of motor
     */
    public HwMotor (HardwareMap hardwareMap,
                    String id,
                    DcMotor.Direction direction,
                    DcMotor.RunMode runMode)
        throws IllegalArgumentException
    {
        this.id = id;
        if (!testing) {
            try {
                this.motor = hardwareMap.get(DcMotorEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
            this.motor.setDirection(direction);
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
            motor.setMode(runMode);
            currentMode = runMode;
            motor.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
            currentZPB = ZeroPowerBehavior.BRAKE;
        } else {
            motor = null;
        }
    }

    public HwMotor (HardwareMap hardwareMap,
                    String id,
                    DcMotor.Direction direction,
                    double maxPower)
            throws IllegalArgumentException
    {
        this.id = id;
        if (!testing) {
            try {
                this.motor = hardwareMap.get(DcMotorEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
            this.motor.setDirection(direction);
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(maxPower);
            motor.setMotorType(motorConfigurationType);
            setMode(RUN_WITHOUT_ENCODER);
            setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        } else {
            motor = null;
        }
    }

    public void setPower(float power) {
        if (Math.abs(this.power - power) >= 0.01) {
            if (!testing) motor.setPower(power);
            this.power = power;
        }
    }

    public void setPower(double power) {
        power = Range.clip(power, -1, 1);
        if (Math.abs(this.power - power) >= 0.01) {
            if (!testing) motor.setPower(power);
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

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        if (currentZPB != behavior) {
            if (!testing) motor.setZeroPowerBehavior(behavior);
            currentZPB = behavior;
        }
    }

    public ZeroPowerBehavior getZeroPowerBehavior() {
        return currentZPB;
    }

    /**
     * @return motor encoder value
     */
    public int getCurrentPosition() {
        if (currentPos == -1) {
            if (!testing) currentPos = motor.getCurrentPosition() - encoderBase;
            else return 1;
        }

        return currentPos;
    }

    /**
     * reset the motor encoder value
     */
    public void resetPosition() {
        if (!testing) encoderBase = motor.getCurrentPosition();
    }

    public void resetPosition(int currentPos) {
        if (!testing) encoderBase = motor.getCurrentPosition() - currentPos;
    }

    /**
     * set the motor runMode
     * @param runMode motor mode
     */
    public void setMode(DcMotor.RunMode runMode) {
        if (currentMode != runMode) {
            if (!testing) motor.setMode(runMode);
            currentMode = runMode;
        }
    }

    /**
     * get the motor init mode
     * @return init mode of motor
     */
    public DcMotor.RunMode getMode() {
        return currentMode;
    }

    public double getVelocity() {
        return !testing? motor.getVelocity() : 0;
    }

    public double getAngularVelocity(AngleUnit unit) {
        return !testing? motor.getVelocity(unit) : 0;
    }

    public boolean isBusy() {
        return !testing && motor.isBusy();
    }

    public int getTargetPosition() {
        return !testing? motor.getTargetPosition() : 0;
    }

    public void setTargetPosition(int pos) {
        if (!testing) motor.setTargetPosition(pos);
    }

    public void setMaxAchievablePower(double set) {
        if (!testing) motor.getMotorType().clone().setAchieveableMaxRPMFraction(set);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        currentPos = -1;
        current = null;
    }

    public double getCurrentAmps() {
        if (testing) return 0.5;
        if (current == null) current = motor.getCurrent(CurrentUnit.AMPS);
        return current;
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }
}
