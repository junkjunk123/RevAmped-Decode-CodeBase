package revAmped.components.CommandScheduler;

/**
 * Command is a simple implementation of the RunnableTask interface that allows you to run a Runnable.
 * It can be used to create commands that can be scheduled and executed by the CommandScheduler.
 */
public class Command implements RunnableTask {
    /**
     * Indicates whether the command has been completed.
     */
    private boolean completed = false;

    /**
     * The runnable that will be executed when the command is run.
     */
    private final Runnable runnable;

    /**
     * Constructor for the Command class.
     * @param runnable the Runnable to be executed when the command is run
     */
    public Command(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Constructor for the Command class with a default runnable that does nothing.
     */
    @Override
    public void reset() {
        completed = false;
    }

    /**
     * Updates the command by running the runnable if it has not been completed yet.
     * @return true if the command has been completed, false otherwise
     */
    @Override
    public boolean update() {
        if (!completed) {
            runnable.run();
            completed = true;
        }

        return true;
    }

    /**
     * Checks if the command has been completed.
     * @return true if the command has been completed, false otherwise
     */
    @Override
    public boolean isCompleted() {
        return completed;
    }
}
