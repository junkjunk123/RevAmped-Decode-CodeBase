package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public abstract class HwMotor2<T extends Enum<T> & HwMotor2.MotorState> extends HwMotor {
    public T getMotorState() {
        return motorState;
    }

    public void setMotorState(T motorState) {
        this.motorState = motorState;
        setPower(motorState.power());
    }

    public void setMotorState(T motorState, float power) {
        this.motorState = motorState;
        setPower(power);
    }

    @FunctionalInterface
    protected interface MotorState {
        float power();
    }

    protected T motorState;

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction);
        this.motorState = motorState;
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, DcMotor.RunMode runMode, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode);
        this.motorState = motorState;
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, double maxPower, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower);
        this.motorState = motorState;
    }

    @Override
    public boolean testResponse() {
        return motor.getCurrent(CurrentUnit.AMPS) > 0.1;
    }

    @Override
    public void update() {
        super.update();
    }
}
