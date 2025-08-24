package revAmped.components.CommandScheduler;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import revAmped.components.MathUtil.MathUtil;

/**
 * Delay is a class that implements RunnableTask and allows you to run an action after a certain condition is met.
 * It can be used to create delays in command execution, either based on time or a custom condition.
 */
public class Delay implements RunnableTask {
    /**
     * The action that will be executed when the condition is met.
     */
    private final Runnable action;

    /**
     * Indicates whether the delay has been completed.
     */
    protected boolean completed;

    /**
     * The condition that must be met for the action to be executed.
     * This can be a time delay or a custom condition.
     */
    private final BooleanSupplier condition;

    /**
     * A list of cancellation conditions that can cancel the delay.
     * Each cancellation condition has a condition and an action to run when cancelled.
     */
    private ArrayList<Cancellation> cancelers = new ArrayList<>();

    /**
     * A record that represents a cancellation condition.
     * It contains a BooleanSupplier condition and a Runnable action to run when cancelled.
     */
    private record Cancellation(BooleanSupplier condition, Runnable cancellationAction) {}

    /**
     * TimerCondition is a class that implements BooleanSupplier and checks if a certain amount of time has passed.
     * It is used to create time-based delays.
     */
    public static class TimerCondition implements BooleanSupplier{
        /**
         * The amount of time in milliseconds that must pass before the condition is met.
         */
        private final double millis;
        /**
         * The initial time when the TimerCondition was created.
         * This is used to calculate the elapsed time.
         */
        private long initialTime;

        /**
         * Constructor for TimerCondition.
         * @param millis the amount of time in milliseconds that must pass before the condition is met
         */
        public TimerCondition(double millis) {
            this.millis = millis;
            this.initialTime = MathUtil.millisTime();
        }

        /**
         * Checks if the specified amount of time has passed since the initial time.
         * @return true if the specified amount of time has passed, false otherwise
         */
        @Override
        public boolean getAsBoolean() {
            return (MathUtil.millisTime() - initialTime) >= millis;
        }

        /**
         * Resets the TimerCondition by setting the initial time to the current time.
         * This allows the condition to be reused for another delay.
         */
        public void reset() {
            initialTime = MathUtil.millisTime();
        }
    }

    /**
     * Constructor for Delay that takes a BooleanSupplier condition and a Runnable action.
     * This allows for custom conditions to be used for the delay.
     * @param condition the condition that must be met for the action to be executed
     * @param action the action to be executed when the condition is met
     */
    public Delay(BooleanSupplier condition, Runnable action) {
        this.action = action;
        this.condition = condition;
    }

    /**
     * Constructor for Delay that takes a time in milliseconds and a Runnable action.
     * This creates a time-based delay where the action will be executed after the specified time.
     * @param wait the amount of time in milliseconds to wait before executing the action
     * @param action the action to be executed after the delay
     */
    public Delay(double wait, Runnable action) {
        this.action = action;
        condition = new TimerCondition(wait);
    }

    /**
     * Sets a cancellation condition for the delay.
     * @param canceler a BooleanSupplier that returns true when the delay should be cancelled
     * @return the Delay instance for method chaining
     */
    public Delay setCancellation(BooleanSupplier canceler) {
        cancelers.add(new Cancellation(canceler, () -> {}));
        return this;
    }

    /**
     * Sets a cancellation condition for the delay with an action to run when cancelled.
     * @param activePeriod the amount of time in milliseconds that must pass before the delay is cancelled
     * @return the Delay instance for method chaining
     */
    public Delay setCancellation(double activePeriod) {
        setCancellation(new TimerCondition(activePeriod));
        return this;
    }

    /**
     * Sets a cancellation condition for the delay with an action to run when cancelled.
     * @param activePeriod the amount of time in milliseconds that must pass before the delay is cancelled
     * @param cancelationAction the action to run when the delay is cancelled
     * @return the Delay instance for method chaining
     */
    public Delay setCancellation(double activePeriod, Runnable cancelationAction) {
        setCancellation(new TimerCondition(activePeriod), cancelationAction);
        return this;
    }

    /**
     * Sets a cancellation condition for the delay with a custom canceler and an action to run when cancelled.
     * @param canceler a BooleanSupplier that returns true when the delay should be cancelled
     * @param cancelationAction the action to run when the delay is cancelled
     * @return the Delay instance for method chaining
     */
    public Delay setCancellation(BooleanSupplier canceler, Runnable cancelationAction) {
        cancelers.add(new Cancellation(canceler, cancelationAction));
        return this;
    }

    /**
     * Sets the maximum time to run the delay before it is automatically cancelled.
     * @param millis the maximum time in milliseconds to run the delay
     * @return the Delay instance for method chaining
     */
    public Delay setMaxTimeToRun(double millis) {
        return setCancellation(millis, action);
    }

    /**
     * Gets the condition that must be met for the action to be executed.
     * @return the BooleanSupplier condition
     */
    public BooleanSupplier getCondition() {
        return condition;
    }

    /**
     * Gets the action that will be executed when the condition is met.
     * @return the Runnable action
     */
    public Runnable getAction() {
        return action;
    }

    /**
     * Checks if the delay has been completed.
     * @return true if the delay has been completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Runs the action associated with the delay.
     * This method is called when the condition is met.
     */
    public void run() {
        completed = true;
        action.run();
    }

    /**
     * Resets the delay, allowing it to be reused.
     * This method resets the completed state and the TimerCondition if applicable.
     */
    public void reset() {
        completed = false;

        if (condition instanceof TimerCondition timerCondition) {
            timerCondition.reset();
        }

        for (Cancellation canceler : cancelers) {
            if (canceler.condition() instanceof TimerCondition timerCondition) {
                timerCondition.reset();
            }
        }
    }

    /**
     * Updates the delay by checking the condition and executing the action if the condition is met.
     * It also checks for any cancellation conditions and runs their actions if they are met.
     * @return true if the delay has been completed, false otherwise
     */
    public boolean update() {
        if (!completed) {
            for (Cancellation canceler : cancelers) {
                if (canceler.condition().getAsBoolean()) {
                    completed = true;
                    canceler.cancellationAction().run();
                    return isCompleted();
                }
            }

            if (condition.getAsBoolean()) {
                completed = true;
                action.run();
            }
        }

        return isCompleted();
    }

    /**
     * Checks if the delay is a time-based delay.
     * @return true if the condition is a TimerCondition, false otherwise
     */
    public boolean isTimeDelay() {
        return condition instanceof TimerCondition;
    }

    /**
     * Checks if the delay is a condition-based delay.
     * This is the opposite of a time-based delay.
     * @return true if the condition is not a TimerCondition, false otherwise
     */
    public boolean isConditionDelay() {
        return !isTimeDelay();
    }

    /**
     * Returns a string representation of the Delay object.
     * @return a string containing the class name and the completed state
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delay delay2)) return false;
        return isCompleted() == delay2.isCompleted() && Objects.equals(getAction(), delay2.getAction()) && Objects.equals(getCondition(), delay2.getCondition());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getAction(), getCondition());
    }
}
