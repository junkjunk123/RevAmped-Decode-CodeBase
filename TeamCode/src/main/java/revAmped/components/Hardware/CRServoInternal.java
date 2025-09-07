package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.function.Supplier;

public abstract class CRServoInternal<T extends Enum<T> & CRServoInternal.MotorState> extends HwCRServo {
    public T getMotorState() {
        return motorState;
    }

    public void setMotorState(T motorState) {
        this.motorState = motorState;
        if (!motorState.useComputePower()) setPower(motorState.power());
        else servo.setPower(motorState.computePower().get());
    }

    public void setMotorState(T motorState, float power) {
        this.motorState = motorState;
        setPower(power);
    }

    @FunctionalInterface
    protected interface MotorState {
        float power();
        default Supplier<Float> computePower() {
            return () -> null;
        };
        default boolean useComputePower() {
            return false;
        }
    }

    protected T motorState;

    protected CRServoInternal(HardwareMap hardwareMap, String id, DcMotor.Direction direction, T motorState, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, id, direction, encoder);
        this.motorState = motorState;
    }

    @Override
    public void update() {
        super.update();

        if (motorState.useComputePower()) servo.setPower(motorState.computePower().get());
    }
}
