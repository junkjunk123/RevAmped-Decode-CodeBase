package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;

public abstract class CRServoInternal<T extends Enum<T> & CRServoInternal.ServoState> extends HwCRServo {
    protected final HashMap<ServoState, ServoPower> statePowerMap = new HashMap<>();

    public T getServoState() {
        return servoState;
    }

    public void setServoState(T servoState) {
        this.servoState = servoState;
        servo.setPower(statePowerMap.get(this.servoState).power());
    }

    public void setServoState(T servoState, float power) {
        this.servoState = servoState;
        setPower(power);
    }

    protected interface ServoState {}

    @FunctionalInterface
    protected interface ServoPower {
        float power();
        default boolean constant() {
            return false;
        };

        static ServoPower constant(float power) {
            return new ServoPower() {
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

    protected T servoState;

    protected CRServoInternal(HardwareMap hardwareMap, String id, DcMotor.Direction direction, T servoState, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, id, direction, encoder);
        this.servoState = servoState;
        buildStatePowerMap();
    }

    public abstract void buildStatePowerMap();

    @Override
    public void update() {
        super.update();

        ServoPower power = statePowerMap.get(servoState);
        if(!power.constant()) servo.setPower(power.power());
    }
}
