package org.firstinspires.ftc.teamcode.revAmped.components.scheduler;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;

/**
 * Scheduler is a singleton class that manages delays and delay chains.
 */
public final class Scheduler {
    /**
     * The singleton instance of the Scheduler.
     * This is used to ensure that only one instance of Scheduler exists throughout the application.
     */
    private static Scheduler INSTANCE;

    /**
     * The DelayManager that manages all Delay objects.
     * It allows adding, updating, and clearing delays.
     */
    public DelayManager delays;

    /**
     * The DelayChainManager that manages all DelayChain objects.
     * It allows adding, running, and checking the status of delay chains.
     */
    public DelayChainManager delayChains;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the DelayManager and DelayChainManager.
     */
    public Scheduler() {
        delays = new DelayManager();
        delayChains = new DelayChainManager();
        INSTANCE = this;
    }

    /**
     * Returns the singleton instance of the Scheduler.
     * If the instance is null, it creates a new instance.
     * @return the singleton instance of Scheduler
     */
    public static synchronized Scheduler getInstance() {
        if (INSTANCE == null) INSTANCE = new Scheduler();
        return INSTANCE;
    }

    /**
     * Clears all delays and delay chains from the scheduler.
     * This method removes all Delay objects and DelayChain objects from their respective managers.
     */
    public void clear() {
        delays.clear();
        delayChains.clear();
    }

    /**
     * Updates the scheduler by updating all delays and delay chains.
     * This method calls the update method on both DelayManager and DelayChainManager.
     */
    public void update() {
        delays.update();
        delayChains.update();
    }

    /**
     * Adds a Delay object to the scheduler.
     * @param delay the Delay object to be added
     */
    public void add(Delay delay) {
        delays.add(delay);
    }

    /**
     * Adds a delay with a specified time and action to the scheduler.
     * @param runInstantly if true, the action will run immediately; if false, it will be added to the delay manager
     * @param delay the Delay object to be added
     */
    public void addOptionalDelay(boolean runInstantly, Delay delay) {
        delays.addOptionalDelay(runInstantly, delay);
    }

    /**
     * Adds a delay with a specified time and action to the scheduler.
     * @param identifier a unique identifier for the delay chain
     */
    public void run(String identifier) {
        delayChains.run(identifier);
    }

    /**
     * Checks if a delay chain identified by the given identifier is finished.
     * @param identifier the unique identifier for the delay chain
     * @param onComplete a Runnable action to be executed when the delay chain is finished
     * @return true if the delay chain is finished, false otherwise
     */
    public boolean whenFinished(String identifier, Runnable onComplete) {
        boolean finished = delayChains.isFinished(identifier);

        if (finished && onComplete != null) {
           onComplete.run();
        }

        return finished;
    }


    /**
     * Checks if a delay chain identified by the given identifier is finished.
     * @param identifier the unique identifier for the delay chain
     * @return true if the delay chain is finished, false otherwise
     */
    public boolean isFinished(String identifier) {
        return delayChains.isFinished(identifier);
    }
}
