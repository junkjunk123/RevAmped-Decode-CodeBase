package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.function.BooleanSupplier;

/**
 * Stuck is a utility class that checks if the Follower is stuck at the end of a path.
 * It uses a velocity threshold and a time threshold to determine if the Follower is stuck.
 */
public class Stuck {
    /**
     * Evaluates if the robot is not moving
     */
    private BooleanSupplier notMoving;

    /**
     * The threshold amount of time (in milliseconds) that the notMoving condition must be true.
     */
    private double timeThreshold;

    /**
     * ElapsedTime instance to track the time the Follower has been below the velocity threshold.
     */
    private ElapsedTime elapsedTime = new ElapsedTime();

    /**
     * Indicates whether the Follower is currently stuck.
     * This is set to true if the Follower has been below the velocity threshold for the specified time.
     */
    private Boolean isStuck = null;

    /**
     * Constructor to initialize the Stuck utility with a Follower, velocity threshold, and time threshold.
     *
     * @param follower the Follower instance to monitor
     * @param velThreshold the velocity threshold below which the Follower is considered stuck
     * @param timeThreshold the time threshold in milliseconds that the Follower must be below the velocity threshold to be considered stuck
     */
    public Stuck(Follower follower, double velThreshold, double timeThreshold) {
        notMoving = () -> {
            if (follower.getCurrentPath() == null) return false;
            return follower.getVelocity().getMagnitude() < velThreshold;
        };
        this.timeThreshold = timeThreshold;
    }

    /**
     * Constructor to initialize the Stuck utility with a Follower and default velocity and time thresholds.
     *
     * @param follower the Follower instance to monitor
     */
    public Stuck(Follower follower) {
        this(follower, PathConstraints.defaultConstraints.getVelocityConstraint(), 500);
    }

    /**
     * Constructor to initialize the Stuck utility with a BooleanSupplier that checks if the Follower is not moving,
     * and a time threshold.
     *
     * @param notMoving a BooleanSupplier that returns true if the Follower is not moving
     * @param timeThreshold the time threshold in milliseconds that the Follower must be below the velocity threshold to be considered stuck
     */
    public Stuck(BooleanSupplier notMoving, double timeThreshold) {
        this.notMoving = notMoving;
        this.timeThreshold = timeThreshold;
    }

    /**
     * Constructor to initialize the Stuck utility with a BooleanSupplier that checks if the Follower is not moving,
     * and a default time threshold of 500 milliseconds.
     *
     * @param notMoving a BooleanSupplier that returns true if the Follower is not moving
     */
    public Stuck(BooleanSupplier notMoving) {
        this(notMoving, 500);
    }

    /**
     * Checks if the Follower is stuck at the end of a path.
     * The Follower is considered stuck if it is at the end of the path and its velocity is below the threshold for a specified time.
     *
     * @return true if the Follower is stuck, false otherwise
     */
    private boolean isStuck() {
        if (!notMoving.getAsBoolean()) {
            elapsedTime.reset();
            return false;
        }

        if (elapsedTime.milliseconds() > timeThreshold) {
            elapsedTime.reset();
            return true;
        }

        return false;
    }

    /**
     * Resets the elapsed time tracker.
     * This method can be called to reset the timer if needed, for example, when starting a new path.
     */
    public void reset() {
        elapsedTime.reset();
    }

    public void update() {
        isStuck = null;
    }

    public boolean evaluate() {
        return isStuck != null ? isStuck : (isStuck = isStuck());
    }

    public void setStuck(Boolean stuck) {
        isStuck = stuck;
    }
}
