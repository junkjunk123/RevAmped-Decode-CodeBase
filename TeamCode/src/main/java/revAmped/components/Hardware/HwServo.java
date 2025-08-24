package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

/**
 * Created by zwang on 2/7/2016.
 */
public class HwServo implements HwDevice {
    public final ServoImplEx servo;
    private final String id;

    private float initialPosition;
    private float position;

    /**
     * initialize servos
     * @param hardwareMap HardwareMap to get servos from
     * @param id name of servo
     * @param initialPosition starting servo position
     * @throws IllegalArgumentException exception
     */
    public HwServo(HardwareMap hardwareMap,
                   String id,
                   float initialPosition)
        throws IllegalArgumentException
    {
        this.id = id;
        this.initialPosition = initialPosition;
        position = initialPosition;
        this.servo = hardwareMap.get(ServoImplEx.class, id);
        this.servo.setPosition(initialPosition);
    }

    public HwServo(HardwareMap hardwareMap, String id) throws IllegalArgumentException {
        this.id = id;
        this.servo = hardwareMap.get(ServoImplEx.class, id);
        initialPosition = (float) servo.getPosition();
        setPosition(initialPosition);
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        if (Math.abs(this.position - position) >= 1/255f) {
            servo.setPosition(position);
            this.position = position;
        }
    }

    /**
     * get servo initial position
     * @return initial servo position
     */
    public float getInitialPosition() {
        return initialPosition;
    }

    /**
     * @param range the number of degrees the servo can spin - 355 for a dongled axon, 180 for gobilda
     * @param speed seconds per 180 degrees
     * @param currentPos
     * @param targetPos
     * @return time to targetpos
     */
    public static float millisToPosition(float range, float speed, float currentPos, float targetPos) {
       return range*Math.abs(targetPos - currentPos) / 180f * speed;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {

    }

    protected void reached(HwServo2.ServoState servoState) {

    }

    protected void setInitialPosition(float position) {
        initialPosition = position;
    }
}