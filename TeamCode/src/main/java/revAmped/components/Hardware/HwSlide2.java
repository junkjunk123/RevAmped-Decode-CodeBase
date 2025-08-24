package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class HwSlide2 extends HwMotor {
    protected final HwSwitch limitSwitch;
    private int currentPos = -1;
    private int encoderBase = 0;
    private int targetPosition = 0;

    protected interface SlideState {
        float calculatePower(int currentPos, int targetPos);
        int position();
        default boolean isRetraction() {
            return false;
        }
    }

    private SlideState slideState;
    private final SlideState restState;

    protected SlideState getSlideState() {
        return slideState;
    }

    protected void setSlideState(SlideState slideState) {
        setSlideState(slideState, true);
    }

    protected void setSlideState(SlideState slideState, int position) {
        this.slideState = slideState;
        setTargetPosition(position);
    }

    protected void setSlideState(SlideState slideState, boolean runToPos) {
        this.slideState = slideState;
        if (runToPos) setTargetPosition(slideState.position());
    }

    protected void retract(SlideState slideState) {
        targetPosition = slideState.position();
        if (!slideState.isRetraction()) throw new RuntimeException("retraction slide state isn't specified as retraction");
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        setSlideState(slideState, false);
    }

    protected HwSlide2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSwitch limitSwitch, SlideState restState) {
        super(hardwareMap, id, direction);
        this.restState = restState;
        slideState = restState;
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setTargetPosition(restState.position());
        this.limitSwitch = limitSwitch;
        resetPosition();
    }

    @Override
    public void update() {
        currentPos = -1;
        setPower(slideState.calculatePower(currentPos, targetPosition));

        if (slideState.isRetraction() && atBottom()) {
            resetPosition();
            setPower(0f);
            slideState = restState;
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

    public void stop() {
        super.stop();
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    protected boolean atBottom() {
        if (slideState.equals(restState)) return true;
        return limitSwitch.isTouch();
    }

    /**
     * Use for tests only
     * @param position pos to run to
     */
    public void runToPosition(int position) {
        motor.setTargetPosition(position + encoderBase);
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
    }

    /**
     * set the motor target position
     * @param position target position
     */
    public void setTargetPosition(int position) {
        targetPosition = position;
    }

    /**
     * get the current encoder target position
     * @return current encoder target position
     */
    public int getTargetPosition() {
        return targetPosition;
    }
}
