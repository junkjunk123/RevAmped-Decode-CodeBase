package revAmped.components.CommandScheduler.Commands;

/**
 * RunnableTask is an interface that defines a task that can be run, updated, and checked for completion.
 * It provides methods to reset the task, update its state, and check if it has been completed.
 */
public interface RunnableTask {
    /**
     * Resets the task to its initial state.
     */
    void reset();

    /**
     * Updates the task's state.
     * @return true if the task is completed, false otherwise
     */
    boolean update();

    /**
     * Checks if the task has been completed.
     * @return true if the task is completed, false otherwise
     */
    boolean isCompleted();

    /**
     * The next state to transition to after this task is completed.
     */
    default int nextState(int currentState) {return currentState + 1;}
}
