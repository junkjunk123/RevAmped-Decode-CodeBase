package revAmped.components.CommandScheduler;

import java.util.function.BooleanSupplier;

/**
 * ArtificialThread is an abstract class that represents a thread-like structure
 * @param <T> the type of result this thread will produce
 */
public abstract class ArtificialThread<T> {
    private boolean isRunning = false;
    private boolean init = false;
    private boolean terminated = false;
    public T result;

    /**
     * Starts the thread. This method should be called to begin the execution of the thread.
     */
    public void start() {
        isRunning = true;
    }

    /**
     * Runs the thread. This method should be called repeatedly to execute the thread's logic.
     * It initializes the thread if it hasn't been initialized yet, evaluates its state,
     * and runs the loop if not already running.
     */
    public void run() {
        if (terminated) {
            return; // Do not run if terminated
        }

        if (!init) {
            init();
            init = true;
        }

        if (!isRunning) {
            evaluate();
        }

        if (!isRunning) {
            loop();
        }
    }

    /**
     * The main loop of the thread. This method should contain the logic that needs to be executed repeatedly.
     */
    public abstract void loop();

    /**
     * Initializes the thread. This method should be overridden to set up any necessary state or resources.
     */
    public abstract void init();

    /**
     * Evaluates the state of the thread. This method should be overridden to check conditions or perform actions
     * that determine whether the thread should continue running or not.
     */
    public abstract void evaluate();

    /**
     * Checks if the thread is currently running.
     * @return true if the thread is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Stops the thread. This method sets the running state to false, effectively stopping the thread.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Sleeps the thread for a specified duration in milliseconds.
     * @param millis the duration to sleep in milliseconds
     */
    public void sleep(double millis) {
        isRunning = false;
        Scheduler.getInstance().add(new Delay(millis, () -> isRunning = true));
    }

    /**
     * Sleeps the thread until a specified condition is met.
     * @param condition a BooleanSupplier that returns true when the condition is met
     */
    public void sleepUntil(BooleanSupplier condition) {
        isRunning = false;
        Scheduler.getInstance().add(new Delay(condition, () -> isRunning = true));
    }

    /**
     * Sleeps the thread until a DelayedResult condition is met.
     * @param condition a DelayedResult that indicates when the thread should wake up
     */
    public void sleepUntil(DelayedResult<T> condition) {
        isRunning = false;
        Scheduler.getInstance().add(new Delay(condition::isDone, () -> isRunning = true));
    }

    /**
     * Terminates the thread and returns the result.
     * @return the result of the thread execution
     */
    public T terminateAndGetResult() {
        isRunning = false;
        terminated = true;
        return result;
    }

    /**
     * Checks if the thread has been terminated.
     * @return true if the thread is terminated, false otherwise
     */
    public boolean isTerminated() {
        return terminated;
    }

    /**
     * Terminates the thread without returning a result.
     */
    public void terminate() {
        isRunning = false;
        terminated = true;
        result = null; // Clear result on termination
    }

    /**
     * Gets the result of the thread execution immediately.
     * @return the result of the thread execution
     */
    public T getResultNow() {
        return result;
    }

    /**
     * Gets the result of the thread execution wrapped in a DelayedResult.
     * This allows for asynchronous retrieval of the result.
     * @return a DelayedResult containing the result of the thread execution
     */
    public DelayedResult<T> getResult() {
        return new DelayedResult<>(() -> result);
    }
}
