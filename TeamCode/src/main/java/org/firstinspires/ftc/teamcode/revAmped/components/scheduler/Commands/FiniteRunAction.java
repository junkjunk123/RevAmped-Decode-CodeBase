package org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands;

/**
 * FiniteRunAction is a class that allows you to run a Runnable a specified number of times.
 * It keeps track of how many times it has been run and provides a method to check if it is done.
 */
public class FiniteRunAction {
    private int runCount = 0;
    private int totalRunCount;
    private Runnable runnable;

    /**
     * Constructor for FiniteRunAction.
     * @param runnable the Runnable to be executed
     * @param totalRunCount the total number of times the runnable should be run
     */
    public FiniteRunAction(Runnable runnable, int totalRunCount) {
        this.totalRunCount = totalRunCount;
        this.runnable = runnable;
    }

    /**
     * Runs the runnable if it has not been run the specified number of times.
     * Increments the run count after running the runnable.
     */
    public void run() {
        if (runCount < totalRunCount) {
            runnable.run();
            runCount++;
        }
    }

    /**
     * Checks if the runnable has been run the specified number of times.
     * @return true if the runnable has been run the total number of times, false otherwise
     */
    public boolean isDone() {
        return runCount >= totalRunCount;
    }
}
