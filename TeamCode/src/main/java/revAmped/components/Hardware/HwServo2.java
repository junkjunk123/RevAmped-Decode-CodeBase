
package revAmped.components.Hardware;

import static revAmped.components.Misc.Globals.testing;

import androidx.annotation.NonNull;

import com.pedropathing.util.NanoTimer;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;

import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.RevAmpedTimer;
import revAmped.util.Range;

public abstract class HwServo2<T extends Enum<T> & HwServo2.ServoState> implements HwDevice, HwDevice.Servo {
    public interface ServoState {
        float getPosition();
    }

    public final ServoImplEx servo;
    private T currentState;
    private final String id;
    protected float position;
    private final T initialState;
    private float initialPosition;
    private RevAmpedTimer timer = null;
    private double approxTravelTime = 0;
    private boolean reached = true;
    private T previousState;

    public enum ServoBrand {
        UNKNOWN,
        AXON_MINI(0.090),
        AXON_MAX(0.115),
        AXON_MICRO(0.075),
        GOBILDA_SUPERSPEED(0.043);

        double hertz;
        int degRange;

        /**
         * @param hertz seconds for 60 degrees
         */
        ServoBrand(double hertz, int degRange) {
            this.hertz = hertz;
            this.degRange = degRange;
        }

        ServoBrand() {
            this(0, 0);
        }

        ServoBrand(double hertz) {
            this(hertz, 355);
        }

        public double getHertz() {
            return hertz;
        }

        public double millisToPos(float target, float current) {
            return degRange * (target - current) * 1000 * hertz / 60;
        }
    }
    private final ServoBrand brand;

    protected HwServo2(HardwareMap hardwareMap, String id, boolean setPosition, T initialState, ServoBrand brand) throws IllegalArgumentException {
        this.id = id;
        previousState = initialState;
        currentState = initialState;
        this.initialState = initialState;

        if (!testing) {
            try {
                this.servo = hardwareMap.get(ServoImplEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        } else {
            servo = null;
        }

        initialPosition = initialState.getPosition();
        this.brand = brand;

        if (setPosition) {
            setCurrentState(currentState);
        }
    }

    protected HwServo2(HardwareMap hardwareMap, String id, float initialPosition, T initialState, ServoBrand brand) throws IllegalArgumentException {
        this.id = id;
        previousState = initialState;
        currentState = initialState;
        this.initialState = initialState;

        if (!testing) {
            try {
                this.servo = hardwareMap.get(ServoImplEx.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        } else {
            servo = null;
        }

        this.initialPosition = initialPosition;
        setCurrentState(initialState, initialPosition);
        this.brand = brand;
    }

    public float stateToPos(T state) {
        return state.getPosition();
    }

    public float getPosition() {
        return position;
    }

    /**
     * IMPORTANT: USE ONLY FOR TESTERS
     * @param position to go to
     */
    public void setPosition(float position) {
        if (Math.abs(this.position - position) >= 1/255f) {
            if (!testing) {
                servo.setPosition(position);
            }

            if (timer != null) {
                approxTravelTime = brand.millisToPos(position, this.position);
                timer.start(true);
                reached = false;
            }

            this.position = position;
        }
    }

    public T getCurrentState() {
        return currentState;
    }

    public void setCurrentState(T state) {
        previousState = currentState;
        currentState = state;
        float set = state.getPosition();
        if (set != -1f) setPosition(state.getPosition());
        timer = null;
    }

    public void setCurrentState(T state, float position) {
        previousState = currentState;
        currentState = state;
        setPosition(position);
        timer = null;
    }

    public void setCurrentState(T state, boolean useTimer) {
        previousState = currentState;
        currentState = state;
        float set = state.getPosition();
        if (set != -1f) setPosition(state.getPosition());
        if (useTimer && timer == null) timer = new RevAmpedTimer(false);
    }

    public void setCurrentState(T state, float position, boolean useTimer) {
        previousState = currentState;
        currentState = state;
        setPosition(position);
        if (useTimer && timer == null) timer = new RevAmpedTimer(false);
    }

    public T getInitialState() {
        return initialState;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        if (!reached && timer.millis() >= approxTravelTime) {
            reached = true;
            timer.stop();
        }
    }

    protected double millisToPos(float target) {
        return brand.millisToPos(target, getPosition());
    }

    public double millisToPos(T state) {
        return brand.millisToPos(state.getPosition(), getPosition());
    }

    public ServoBrand getBrand() {
        return brand;
    }

    protected boolean reached() {
        return reached;
    }

    public float getInitialPosition() {
        return initialPosition;
    }

    public double millisSinceTravelStart() {
        return timer.millis();
    }

    public double getApproxTravelTime() {
        return approxTravelTime;
    }

    public T getPreviousState() {
        return previousState;
    }

    public static float getPositionFromRad(double rad, double minRad, double maxRad, float minRadPos, float maxRadPos) {
        if (minRad < maxRad) {
            float pos = (float) Range.scale(rad, minRad, maxRad, minRadPos, maxRadPos);
            if (minRadPos < maxRadPos) return pos;
            else return (minRadPos - pos) + maxRadPos;
        }

        float pos = (float) Range.scale(rad, maxRad, minRad, maxRadPos, minRadPos);
        if (maxRadPos < minRadPos) return pos;
        return (maxRadPos - pos) + minRadPos;
    }

    @Override
    public boolean testResponse() {
        return servo.isPwmEnabled();
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }
}
