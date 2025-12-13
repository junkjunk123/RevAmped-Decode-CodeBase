package org.firstinspires.ftc.teamcode.revAmped.components.architecture;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.AutonomousCommandAction;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.RunnableTask;
import org.firstinspires.ftc.teamcode.revAmped.util.Lockable;

import java.util.function.BooleanSupplier;

/**
 * AutonomousEnumeratedAction is an abstract class that defines the structure for autonomous actions
 * in a robot's autonomous mode. It provides methods for initialization, looping through the action,
 * checking if the action is finished driving, and generating the next state.
 */
public abstract class AutonomousEnumeratedAction implements Lockable {
    protected boolean completed = false;
    protected boolean initCompleted = false;
    protected abstract void init();
    protected abstract void loop();
    public abstract boolean isFinishedDriving();
    protected boolean locked = false;
    private double millisToDrive = -1;

    /**
     * Checks if the action has finished driving.
     * @return true if the action is finished driving or the preload drive timer has exceeded the specified milliseconds to drive,
     */
    public boolean finishedDriving() {
        boolean result = (isFinishedDriving() ||
                (Globals.aae.preloadDriveTimer.milliseconds() > millisToDrive() && millisToDrive() > 0
                        && Globals.followerManager.follower.getVelocity().getMagnitude()
                        < Globals.followerManager.getCurrentPath().getPathEndVelocityConstraint())) && Globals.aae.driving;
        if (result) Globals.aae.driving = false;
        return result;
    }

    /**
     * Returns a unique identifier for the action.
     * @return an empty string by default, can be overridden to provide a specific identifier
     */
    public String identifier() {return "";}

    /**
     * Returns a NextStateGenerator that generates the next state of the action.
     * @return a default implementation that increments the current state by 1
     */
    public NextStateGenerator nextStateGenerator() {return i -> i+1;}

    /**
     * Updates the action by initializing it if not already completed, and looping through it if it is not completed.
     * @return true if the action is completed, false otherwise
     */
    public boolean update() {
        if (locked)
            return false;

        if (!initCompleted) {
            if (millisToDrive() > 0)
                Globals.aae.preloadDriveTimer.reset();
            init();
            initCompleted = true;
        } else if (!completed) {
            loop();
        }

        return completed;
    }

    /**
     * NextStateGenerator is an interface that defines a method to generate the next state of an action.
     */
    public interface NextStateGenerator {
        int nextState(int current);
    }

    /**
     * Creates a BooleanSupplier that checks if the Follower is busy.
     * @param follower the Follower instance to check
     * @return a BooleanSupplier that returns true if the Follower is not busy, false otherwise
     */
    public static BooleanSupplier followerBusy(Follower follower) {
        return () -> !follower.isBusy();
    }

    /**
     * Returns the number of milliseconds to drive for this action.
     * By default, it returns -1, indicating no specific time limit.
     * Subclasses can override this method to specify a different duration.
     * @return the number of milliseconds to drive, or -1 if not specified
     */
    public double millisToDrive() {
        return millisToDrive;
    }

    /**
     * Resets the action to its initial state.
     * This method sets the completed flag to false and the initCompleted flag to false.
     */
    public boolean isCompleted() {
        return completed;
    }

    public RunnableTask toRunnableTask() {
        AutonomousEnumeratedAction parent = this;
        return new AutonomousCommandAction() {
            @Override
            protected void init() {
                parent.init();
            }

            @Override
            protected void loop() {
                parent.loop();
            }

            @Override
            public int nextState(int currentState) {
                return parent.nextStateGenerator().nextState(currentState);
            }
        };
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public AutonomousEnumeratedAction setMillisToDrive(double millisToDrive) {
        this.millisToDrive = millisToDrive;
        return this;
    }
}
