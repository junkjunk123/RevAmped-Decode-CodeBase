package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class TwoMotorMechanism<T extends Enum<T> & HwMotor2.MotorState> extends HwMotor2<T> {
    private final HwMotor motor2;

    protected TwoMotorMechanism(HardwareMap hardwareMap, String id, String id2, DcMotorSimple.Direction direction, DcMotorSimple.Direction direction2, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState);
        motor2 = new HwMotor(hardwareMap, id2, direction2);
    }

    protected TwoMotorMechanism(HardwareMap hardwareMap, String id, String id2, DcMotorSimple.Direction direction, DcMotorSimple.Direction direction2, DcMotor.RunMode runMode, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode, motorState);
        motor2 = new HwMotor(hardwareMap, id2, direction2, runMode);
    }

    protected TwoMotorMechanism(HardwareMap hardwareMap, String id, String id2, DcMotorSimple.Direction direction, DcMotorSimple.Direction direction2, double maxPower, T motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower, motorState);
        motor2 = new HwMotor(hardwareMap, id2, direction2, maxPower);
    }

    public void setMode(DcMotor.RunMode mode) {
        super.setMode(mode);
        motor2.setMode(mode);
    }

    public void setPower(double power) {
        super.setPower(power);
        motor2.setPower(power);
    }

    @Override
    public void stop() {
        super.stop();
        motor2.stop();
    }
}
