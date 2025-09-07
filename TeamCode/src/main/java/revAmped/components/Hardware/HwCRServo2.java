package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayedResult;
import revAmped.components.CommandScheduler.DelayedSupplier;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.MathUtil.MathUtil;

public abstract class HwCRServo2<T extends Enum<T> & HwCRServo2.ServoState> extends HwCRServo {
    protected HwSlide2.SlideRetractionDetector resetDetector;
    private Integer currentPos = null;
    private int encoderBase = 0;
    private int targetPosition;

    public record InitActions(Runnable action, double waitMillis) {
        public InitActions() {
            this(() -> {
            }, 0);
        }
    }

    protected EnumMap<T, DoubleSupplier> powerMap;
    protected EnumMap<T, HwCRServo2.InitActions> initMap;

    protected interface ServoState {
        int position();
        default boolean isRetraction() {return false;}
    }

    public interface ServoResetDetector extends HwDevice.Sensor<Boolean> {
        default void update() {}
        Boolean getReading();

        @Override
        default String getId() {return "";}
    }

    protected T servoState;
    private final T restState;
    private Integer prevPos = 0;
    private long prevTimeNano = 0;
    private long currentTimeNano = 0;
    private Double velocity = null;
    public T getCurrentState() {
        return servoState;
    }
    private final boolean isLimSwitch;

    public void setCurrentState(T servoState) {
        setCurrentState(servoState, true);
    }

    public void setCurrentState(T servoState, int position) {
        this.servoState = servoState;
        HwCRServo2.InitActions initActions = initMap.get(servoState);
        initActions.action().run();
        Scheduler.getInstance().add(new Delay(initActions.waitMillis(), () -> setTargetPosition(position)));
    }

    public void setCurrentState(T servoState, boolean runToPos) {
        this.servoState = servoState;
        HwCRServo2.InitActions initActions = initMap.get(servoState);
        initActions.action().run();
        if (runToPos) Scheduler.getInstance().add(new Delay(initActions.waitMillis(), () -> setTargetPosition(servoState.position())));
    }

    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSlide2.SlideRetractionDetector resetDetector, T restState, Encoder encoder) {
        super(hardwareMap, id, direction, encoder);
        this.restState = restState;
        servoState = restState;
        targetPosition = restState.position();
        this.resetDetector = resetDetector;
        resetPosition();
        isLimSwitch = resetDetector instanceof HwSwitch;
    }

    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSlide2.SlideRetractionDetector resetDetector, T restState, T initialState, Encoder encoder) {
        super(hardwareMap, id, direction, encoder);
        this.restState = restState;
        servoState = initialState;
        targetPosition = initialState.position();
        this.resetDetector = resetDetector;
        if (initialState == restState) resetPosition();
        isLimSwitch = resetDetector instanceof HwSwitch;
    }

    @Override
    public void update() {
        resetDetector.update();
        prevTimeNano = currentTimeNano;
        currentTimeNano = System.nanoTime();
        prevPos = currentPos;
        currentPos = null;
        velocity = null;
        setPower(powerMap.get(servoState).getAsDouble());

        if (servoState.isRetraction() && atBottom()) {
            if (isLimSwitch) resetPosition();
            setPower(0f);
            setCurrentState(restState);
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
    }

    protected boolean atBottom() {
        if (servoState.equals(restState)) return true;
        return resetDetector.getReading();
    }

    /**
     * set the motor target position
     * @param position target position
     */
    public void setTargetPosition(int position) {
        targetPosition = position;
        updateController(targetPosition);
    }

    /**
     * get the current encoder target position
     * @return current encoder target position
     */
    public int getTargetPosition() {
        return targetPosition;
    }

    public abstract Class<T> getStateClass();
    public abstract void buildPowerMap();
    public abstract void buildInitMap();

    public void finetune(int val) {
        setCurrentState(servoState, targetPosition + val);
    }

    public HwSlide2.SlideRetractionDetector limitSwitch() {
        return resetDetector;
    }

    public abstract void updateController(int targetPosition);

    public void init() {
        powerMap = new EnumMap<>(getStateClass());
        initMap = new EnumMap<>(getStateClass());
        buildPowerMap();
        buildInitMap();
    }

    public double getVelocity() {
        if (velocity == null) {
            if (prevPos == null) {
                return 0;
            }

            int currentPos = getCurrentPosition();

            double deltaTime = (currentTimeNano - prevTimeNano) / Math.pow(10, 9);

            if (deltaTime == 0) velocity = 0.0;
            else velocity = (currentPos - prevPos) / deltaTime;
        }

        return velocity;
    }

    public void retract() {
        retract(3000);
    }

    public void retract(double timeout) {
        retraction();
        Scheduler.getInstance().add(new Delay(timeout, () -> {
            if (isLimSwitch) resetPosition();
            setPower(0f);
            setCurrentState(restState);
        }).setCancellation(() -> !servoState.isRetraction()));
    }

    public void retract(Runnable onTimeout) {
        retract(3000, onTimeout);
    }

    public void retract(double timeout, Runnable onTimeout) {
        retraction();
        Scheduler.getInstance().add(new Delay(timeout, () -> {
            if (isLimSwitch) resetPosition();
            setPower(0f);
            setCurrentState(restState);
            onTimeout.run();
        }).setCancellation(() -> !servoState.isRetraction()));
    }

    protected abstract void retraction();

    public DelayedResult<Integer> testEncodersAndSwitch() {
        HwCRServo2.InitActions initActions = initMap.get(getTestState());
        initActions.action().run();
        servo.setPower(0.75);
        AtomicBoolean encoders = new AtomicBoolean(false);
        AtomicBoolean limitSwitch = new AtomicBoolean(false);
        Scheduler.getInstance().add(new Delay(400, () -> {
            encoders.set(encoder.getPosition() > 50);
            retract(800, () -> limitSwitch.set(false));
            Scheduler.getInstance().add(new Delay(resetDetector::getReading, () -> limitSwitch.set(true)));
        }));
        BooleanSupplier set = MathUtil.boleanSupplierfromTimeDelay(1400);
        return new DelayedSupplier<>(() -> limitSwitch.get() || set.getAsBoolean(), () -> {
            servo.setPower(0f);
            if (!encoders.get()) {
                return -1; // encoders failed
            } else if (!limitSwitch.get()) {
                return -2; // limit switch failed
            } else {
                return 1; // all good
            }
        }).setMaxTime(1400).get();
    }

    public HwSlide2.SlideRetractionDetector getResetDetector() {
        return resetDetector;
    }

    public abstract T getTestState();
}
