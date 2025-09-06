package revAmped.components.Hardware;

import static revAmped.components.Misc.Globals.testing;

import com.qualcomm.robotcore.hardware.DcMotor;
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

public abstract class HwCRServo2<T extends Enum<T> & HwSlide2.SlideState> extends HwCRServo {
    protected HwSlide2.SlideRetractionDetector retractionDetector;
    private Integer currentPos = null;
    private int encoderBase = 0;
    private int targetPosition;
    public record InitActions(Runnable action, double waitMillis) {
        public InitActions() {
            this(() -> {}, 0);
        }
    }

    protected EnumMap<T, DoubleSupplier> powerMap;
    protected EnumMap<T, HwSlide2.InitActions> initMap;

    protected interface SlideState {
        int position();
        default boolean isRetraction() {return false;}
    }

    public interface SlideRetractionDetector extends HwDevice.Sensor<Boolean> {
        default void update() {}
        Boolean getReading();

        @Override
        default String getId() {return "";}
    }

    protected T slideState;
    private final T restState;
    private Integer prevPos = 0;
    private long prevTimeNano = 0;
    private long currentTimeNano = 0;
    private Double velocity = null;
    public T getCurrentState() {
        return slideState;
    }
    private final boolean isLimSwitch;

    public void setCurrentState(T slideState) {
        setCurrentState(slideState, true);
    }

    public void setCurrentState(T slideState, int position) {
        this.slideState = slideState;
        HwSlide2.InitActions initActions = initMap.get(slideState);
        initActions.action().run();
        Scheduler.getInstance().add(new Delay(initActions.waitMillis(), () -> setTargetPosition(position)));
    }

    public void setCurrentState(T slideState, boolean runToPos) {
        this.slideState = slideState;
        HwSlide2.InitActions initActions = initMap.get(slideState);
        initActions.action().run();
        if (runToPos) Scheduler.getInstance().add(new Delay(initActions.waitMillis(), () -> setTargetPosition(slideState.position())));
    }

    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSlide2.SlideRetractionDetector retractionDetector, T restState, Encoder encoder) {
        super(hardwareMap, id, direction, encoder);
        this.restState = restState;
        slideState = restState;
        targetPosition = restState.position();
        this.retractionDetector = retractionDetector;
        resetPosition();
        isLimSwitch = retractionDetector instanceof HwSwitch;
    }

    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSlide2.SlideRetractionDetector retractionDetector, T restState, T initialState, Encoder encoder) {
        super(hardwareMap, id, direction, encoder);
        this.restState = restState;
        slideState = initialState;
        targetPosition = initialState.position();
        this.retractionDetector = retractionDetector;
        if (initialState == restState) resetPosition();
        isLimSwitch = retractionDetector instanceof HwSwitch;
    }

    @Override
    public void update() {
        retractionDetector.update();
        prevTimeNano = currentTimeNano;
        currentTimeNano = System.nanoTime();
        prevPos = currentPos;
        currentPos = null;
        velocity = null;
        setPower(powerMap.get(slideState).getAsDouble());

        if (slideState.isRetraction() && atBottom()) {
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
        if (slideState.equals(restState)) return true;
        return retractionDetector.getReading();
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
        setCurrentState(slideState, targetPosition + val);
    }

    public HwSlide2.SlideRetractionDetector limitSwitch() {
        return retractionDetector;
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
        }).setCancellation(() -> !slideState.isRetraction()));
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
        }).setCancellation(() -> !slideState.isRetraction()));
    }

    protected abstract void retraction();

    public DelayedResult<Integer> testEncodersAndSwitch() {
        HwSlide2.InitActions initActions = initMap.get(getTestState());
        initActions.action().run();
        servo.setPower(0.75);
        AtomicBoolean encoders = new AtomicBoolean(false);
        AtomicBoolean limitSwitch = new AtomicBoolean(false);
        Scheduler.getInstance().add(new Delay(400, () -> {
            encoders.set(encoder.getPosition() > 50);
            retract(800, () -> limitSwitch.set(false));
            Scheduler.getInstance().add(new Delay(retractionDetector::getReading, () -> limitSwitch.set(true)));
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

    public HwSlide2.SlideRetractionDetector getRetractionDetector() {
        return retractionDetector;
    }

    public abstract T getTestState();
}
