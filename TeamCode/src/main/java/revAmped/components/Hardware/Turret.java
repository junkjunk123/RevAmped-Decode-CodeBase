package revAmped.components.Hardware;

import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class Turret<T extends Enum<T> & HwMotor2.MotorState> extends HwMotor2<T> {
    public HwSlide2.SlideRetractionDetector retractionDetector;

    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, T motorState, HwSlide2.SlideRetractionDetector detector) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState);
        this.retractionDetector = detector;
    }

    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, DcMotor.RunMode runMode, T motorState, HwSlide2.SlideRetractionDetector detector) throws IllegalArgumentException {
        super(hardwareMap, id, direction, runMode, motorState);
        this.retractionDetector = detector;
    }

    protected Turret(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, double maxPower, T motorState, HwSlide2.SlideRetractionDetector detector) throws IllegalArgumentException {
        super(hardwareMap, id, direction, maxPower, motorState);
        this.retractionDetector = detector;
    }

    public void update() {
        super.update();
        retractionDetector.update();

        if ((!motorState.useComputePower() && Math.abs(motorState.power()) > 0.05) || (Math.abs(motorState.computePower().get()) > 0.05)) {
            if (retractionDetector != null && retractionDetector.getReading()) {
                resetPosition();
            }
        }
    }

    public abstract int ticksPerRotation();

    public double getError(int targetPosition) {
        return MathFunctions.normalizeAngle((double) (targetPosition - currentPos) / ticksPerRotation());
    }
}
