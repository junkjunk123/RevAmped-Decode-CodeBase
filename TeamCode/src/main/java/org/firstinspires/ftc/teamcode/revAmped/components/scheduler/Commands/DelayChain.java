package org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands;
import java.util.List;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;

/**
 * DelayChain is a class that allows you to chain multiple RunnableTasks together,
 */
public class DelayChain implements RunnableTask {
    private final List<RunnableTask> delays;
    private int currentIndex = 0;
    private boolean completed = false;
    private boolean active = false;

    /**
     * Constructor for DelayChain that takes a list of RunnableTasks. This will be added to the Scheduler.
     * @param delays the list of RunnableTasks to be chained together
     */
    public DelayChain(String identifier, RunnableTask... delays) {
        this.delays = List.of(delays);
        Scheduler.getInstance().delayChains.add(this, identifier);
    }

    /**
     * Constructor for DelayChain that takes an array of RunnableTasks.
     * @param delays the array of RunnableTasks to be chained together
     */
    public DelayChain(RunnableTask... delays) {
        this.delays = List.of(delays);
    }

    /**
     * Resets the DelayChain to its initial state.
     * This method sets completed to false, currentIndex to 0, and resets each RunnableTask in the chain.
     */
    public void reset() {
        completed = false;
        currentIndex = 0;
        delays.forEach(RunnableTask::reset);
    }

    /**
     * Updates the DelayChain by running the current RunnableTask in the chain.
     * If the current task is completed, it moves to the next task.
     * If all tasks are completed, it sets completed to true.
     * @return true if the DelayChain is completed, false otherwise
     */
    public boolean update() {
        if (completed || delays.isEmpty()) return true;
        RunnableTask current = delays.get(currentIndex);
        current.update();

        if (current.isCompleted()) {
            currentIndex = current.nextState(currentIndex);
            if (currentIndex >= delays.size()) {
                completed = true;
            }
        }

        return isCompleted();
    }

    /**
     * Checks if the DelayChain is completed.
     * @return true if the DelayChain is completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Activates the DelayChain.
     */
    private void activate() {
        active = true;
    }

    /**
     * Checks if the DelayChain is currently active.
     * @return true if the DelayChain is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the current index of the DelayChain.
     * @return the current index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Runs the DelayChain.
     */
    public void run() {
        reset();
        activate();
    }

    /**
     * Checks if the current index of the DelayChain is complete.
     * @return true if the current index is complete, false otherwise
     */
    public boolean currentIndexComplete() {
        if (currentIndex >= delays.size()) return true;
        return delays.get(currentIndex).isCompleted();
    }
}
