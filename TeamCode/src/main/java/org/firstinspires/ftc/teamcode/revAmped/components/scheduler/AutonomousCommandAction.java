package org.firstinspires.ftc.teamcode.revAmped.components.scheduler;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.RunnableTask;

/**
 * AutonomousCommandAction is an abstract class that represents a command action
 */
public abstract class AutonomousCommandAction implements RunnableTask {
    protected boolean completed = false;
    protected boolean initCompleted = false;

    /**
     * Initializes the command action.
     */
    protected abstract void init();

    /**
     * The main loop of the command action. This method should contain the logic that needs to be executed repeatedly.
     */
    protected abstract void loop();

    /**
     * Updates the command action.
     * @return true if the command action is completed, false otherwise
     */
    public boolean update() {
        if (!initCompleted) {
            init();
            initCompleted = true;
        } else if (!completed) {
            loop();
        }

        return completed;
    }

    /**
     * Checks if the command action is completed.
     * @return true if the command action is completed, false otherwise
     */
    @Override
    public boolean isCompleted() {
        return completed;
    }

    /**
     * ts doesn't work because these can't be reset
     */
    @Override
    public void reset() {
        //lol
    }
}
