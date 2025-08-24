package revAmped.components.Hardware;

import com.pedropathing.util.CustomPIDFCoefficients;
import com.pedropathing.util.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class HwSlide extends HwMotor {
    protected final HwSwitch limitSwitch;
    private int currentPos = -1;
    protected final HwSlideRetract retraction;
    private int encoderBase = 0;
    private int targetPosition = 0;

    public HwSlide(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSwitch limitSwitch) {
        super(hardwareMap, id, direction);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setTargetPosition(0);
        this.limitSwitch = limitSwitch;
        retraction = new HwSlideRetract(this, limitSwitch);
        resetPosition();
    }

    public HwSlide(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction) {
        super(hardwareMap, id, direction);
        setTargetPosition(0);
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        limitSwitch = null;
        retraction = null;
        resetPosition();
    }

    public void stop() {
        super.stop();
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if (retraction != null) {
            retraction.stop();
        }
    }

    public void runToPosition(int position) {
        if (retraction != null) {
            retraction.stop();
        }

        setTargetPosition(position);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        setPower(1f);
    }

    /**
     * @return motor encoder value
     */
    public int getCurrentPosition() {
        if (currentPos == -1) {
            currentPos = motor.getCurrentPosition() - encoderBase;
        }

        return currentPos;
    }

    /**
     * reset the motor encoder value
     */
    public void resetPosition() {
        encoderBase = motor.getCurrentPosition();
        setTargetPosition(0);

        if (retraction != null) {
            retraction.stop();
        }
    }

    /**
     * reset the motor encoder value
     */
    public void resetPosition(boolean stopRetraction) {
        encoderBase = motor.getCurrentPosition();
        setTargetPosition(0);

        if (stopRetraction && retraction != null) {
            retraction.stop();
        }
    }

    /**
     * set the motor target position
     * @param position target position
     */
    public void setTargetPosition(int position) {
        targetPosition = position;
        motor.setTargetPosition(encoderBase + targetPosition);
    }

    /**
     * get the current encoder target position
     * @return current encoder target position
     */
    public int getTargetPosition() {
        return targetPosition;
    }

    public void retract(Supplier<Float> power, Runnable action) {
        if (retraction != null) {
            retraction.setRetract(power, () -> {
                action.run();
                resetPosition();
                setPower(0f);
            });
        }
    }

    public void retract(Supplier<Float> power, Runnable action, boolean resetPosition) {
        if (retraction != null) {
            retraction.setRetract(power, () -> {
                action.run();

                if (resetPosition) {
                    resetPosition();
                }

                setPower(0f);
            });
        }
    }

    public void retract(Supplier<Float> power) {
        if (retraction != null) {
            retraction.setRetract(power, () -> {
                resetPosition();
                setPower(0f);
            });
        }
    }

    public void retract(Supplier<Float> power, boolean resetPosition) {
        if (retraction != null) {
            retraction.setRetract(power, () -> {
                if (resetPosition) {
                    resetPosition();
                }
                setPower(0f);
            });
        }
    }

    public void retract(float power) {
        if (retraction != null) {
            retraction.setRetract(() -> power, () -> {
                resetPosition();
                setPower(0f);
            });
        }
    }

    public void retract(float power, Runnable action) {
        if (retraction != null) {
            retraction.setRetract(() -> power, () -> {
                action.run();
                resetPosition();
                setPower(0f);
            });
        }
    }

    public void retract(float power, float finalPower) {
        if (retraction != null) {
            retraction.setRetract(() -> power, () -> {
                resetPosition();
                setPower(finalPower);
            });
        }
    }

    public void retract(float power, Runnable action, float finalPower) {
        if (retraction != null) {
            retraction.setRetract(() -> power, () -> {
                action.run();
                resetPosition();
                setPower(finalPower);
            });
        }
    }

    public boolean isRetracting() {
        return retraction.isRetract();
    }

    @Override
    public void update() {
        currentPos = -1;

        if (retraction != null) {
            retraction.update();
        }
    }

    public double getEncoderBase() {
        return encoderBase;
    }

    public boolean closeEnough(int target, int tolerance) {
        return Math.abs(getCurrentPosition() - target) < tolerance;
    }

    public boolean closeEnough(int tolerance) {
        return Math.abs(getCurrentPosition() - getTargetPosition()) < tolerance;
    }
}
