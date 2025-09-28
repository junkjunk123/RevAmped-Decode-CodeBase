package revAmped.components.Hardware;

import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class HwCRServo2<T extends Enum<T> & CRServoInternal.ServoState> extends CRServoInternal<T> {
    public HwSlide2.SlideRetractionDetector retractionDetector;

    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, T motorState, HwSlide2.SlideRetractionDetector detector, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState, encoder);
        this.retractionDetector = detector;
    }

    public void update() {
        super.update();
        retractionDetector.update();

        if ((Math.abs(statePowerMap.get(servoState).power()) > 0.05)) {
            if (retractionDetector != null && retractionDetector.getReading()) {
                resetPosition();
            }
        }
    }

    public abstract int ticksPerRotation();

    public double getError(int targetPosition) {
        return ((targetPosition - currentPos % ticksPerRotation()) + ticksPerRotation() * 1.5) % ticksPerRotation() - ticksPerRotation() / 2.0;
    }
}
