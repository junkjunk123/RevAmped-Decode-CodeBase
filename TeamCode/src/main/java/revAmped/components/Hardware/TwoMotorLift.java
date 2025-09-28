package revAmped.components.Hardware;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class TwoMotorLift<T extends Enum<T> & HwSlide2.SlideState> extends HwSlide2<T> {
    public HwMotor secondaryMotor; //the one that doesn't have the encoder

    protected TwoMotorLift(HardwareMap hardwareMap, String id, String idSecondary, DcMotorSimple.Direction direction, DcMotorSimple.Direction directionSecondary, SlideRetractionDetector retractionDetector, T restState) {
        super(hardwareMap, id, direction, retractionDetector, restState);
        secondaryMotor = new HwMotor(hardwareMap, idSecondary, directionSecondary);
    }

    protected TwoMotorLift(HardwareMap hardwareMap, String id, String idSecondary, DcMotorSimple.Direction direction, DcMotorSimple.Direction directionSecondary, SlideRetractionDetector retractionDetector, T restState, T initialState) {
        super(hardwareMap, id, direction, retractionDetector, restState, initialState);
        secondaryMotor = new HwMotor(hardwareMap, idSecondary, directionSecondary);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        super.setZeroPowerBehavior(behavior);
        secondaryMotor.setZeroPowerBehavior(behavior);
    }

    public void setMode(DcMotor.RunMode mode) {
        super.setMode(mode);
        secondaryMotor.setMode(mode);
    }

    public void setPower(double power) {
        super.setPower(power);
        secondaryMotor.setPower(power);
    }

    @Override
    public void update() {
        super.update();
        secondaryMotor.update();
    }

    @Override
    public void stop() {
        super.stop();
        secondaryMotor.stop();
    }
}
