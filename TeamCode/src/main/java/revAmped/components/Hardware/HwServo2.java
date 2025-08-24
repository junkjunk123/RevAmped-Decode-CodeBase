
package revAmped.components.Hardware;

import com.pedropathing.util.NanoTimer;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Timer;

import revAmped.components.MathUtil.RevAmpedTimer;

public abstract class HwServo2 implements HwDevice {
    protected interface ServoState {
        float getPosition();
    }

    public final ServoImplEx servo;
    private ServoState currentState;
    private final String id;
    protected float position;
    private final ServoState initialState;
    private float initialPosition;
    private final RevAmpedTimer timer = new RevAmpedTimer(true);
    private double approxTravelTime = 0;
    private boolean reached = true;

    public enum ServoBrand {
        UNKNOWN,
        AXON_MINI(0.090),
        AXON_MAX(0.115),
        AXON_MICRO(0.075);

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

    protected HwServo2(HardwareMap hardwareMap, String id, boolean setPosition, ServoState initialState, ServoBrand brand) throws IllegalArgumentException {
        this.id = id;
        currentState = initialState;
        this.initialState = initialState;
        this.servo = hardwareMap.get(ServoImplEx.class, id);
        initialPosition = initialState.getPosition();
        this.brand = brand;

        if (setPosition) {
            setCurrentState(currentState);
        }
    }

    protected HwServo2(HardwareMap hardwareMap, String id, float initialPosition, ServoState initialState, ServoBrand brand) throws IllegalArgumentException {
        this.id = id;
        currentState = initialState;
        this.initialState = initialState;
        this.servo = hardwareMap.get(ServoImplEx.class, id);
        this.initialPosition = initialPosition;
        setCurrentState(initialState, initialPosition);
        this.brand = brand;
    }

    protected float stateToPos(ServoState state) {
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
            servo.setPosition(position);
            approxTravelTime = brand.millisToPos(position, this.position);
            timer.start(true);
            this.position = position;
            reached = false;
        }
    }

    protected ServoState getCurrentState() {
        return currentState;
    }

    protected void setCurrentState(ServoState state) {
        currentState = state;
        float set = state.getPosition();
        if (set != -1f) setPosition(state.getPosition());
    }

    protected void setCurrentState(ServoState state, float position) {
        currentState = state;
        setPosition(position);
    }

    protected ServoState getInitialState() {
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

    protected double millisToPos(ServoState state) {
        return brand.millisToPos(state.getPosition(), getPosition());
    }

    protected boolean reached(ServoState servoState) {
        return reached && getPosition() == servoState.getPosition();
    }

    protected boolean reached(ServoState[] servoState) {
        for (ServoState state : servoState) {
            if (reached(state)) {
                return true;
            }
        }

        return false;
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
}
