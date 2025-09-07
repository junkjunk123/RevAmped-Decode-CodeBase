package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.HashMap;

public abstract class HwMotor2<T extends Enum<T> & HwMotor2.MotorState> extends HwMotor {
    protected final HashMap<MotorState, MotorPower> statePowerMap = new HashMap<>();

    public T getCurrentState() {
        return motorState;
    }

    public void setCurrentState(T motorState) {
        this.motorState = motorState;
        motor.setPower(statePowerMap.get(motorState).power());
    }

    public void setCurrentState(T motorState, float power) {
        this.motorState = motorState;
        setPower(power);
    }

    protected interface MotorState { }

    @FunctionalInterface
    protected interface MotorPower {
        float power();
        default boolean constant() {
            return false;
        };

        static MotorPower constant(float power) {
            return new MotorPower() {
                @Override
                public float power() {
                    return power;
                }

                @Override
                public boolean constant() {
                    return true;
                }
            };
        }
    }

    protected T motorState;

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction);
        this.motorState = motorState;
        buildStatePowerMap();
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, DcMotor.RunMode runMode, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode);
        this.motorState = motorState;
        buildStatePowerMap();
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, double maxPower, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower);
        this.motorState = motorState;
        buildStatePowerMap();
    }

    @Override
    public boolean testResponse() {
        return motor.getCurrent(CurrentUnit.AMPS) > 0.1;
    }

    public abstract void buildStatePowerMap();

    @Override
    public void update() {
        super.update();

        MotorPower power = statePowerMap.get(motorState);
        if(!power.constant()) motor.setPower(power.power());
    }

    public abstract T getRest();

    @Override
    public boolean isBusy() {
        return getCurrentState() != getRest();
    }

    public boolean atRest() {
        return getCurrentState() == getRest();
    }

    public void stop() {
        setCurrentState(getRest());
    }
}
