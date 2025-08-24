package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class HwMotor2 extends HwMotor {
    protected MotorState getMotorState() {
        return motorState;
    }

    protected void setMotorState(MotorState motorState) {
        this.motorState = motorState;
    }

    protected void setMotorState(MotorState motorState, int position) {
        setMotorState(motorState);

    }

    @FunctionalInterface
    protected interface MotorState {
        float power();
    }

    private MotorState motorState = () -> 0;

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction) throws IllegalArgumentException {
        super(hardwareMap, id, direction);
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, DcMotor.RunMode runMode) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode);
    }

    protected HwMotor2(HardwareMap hardwareMap, String id, DcMotor.Direction direction, double maxPower) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower);
    }

    @Override
    public void update() {
        super.update();
        setPower(motorState.power());
    }
}
