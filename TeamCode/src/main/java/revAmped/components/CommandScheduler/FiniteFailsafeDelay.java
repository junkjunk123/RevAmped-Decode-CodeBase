package revAmped.components.CommandScheduler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * FiniteFailsafeDelay is a specialized Delay that allows for multiple retries with conditions and actions.
 */
public class FiniteFailsafeDelay extends Delay {
    public ArrayList<Failsafe> failsafes = new ArrayList<>();

    /**
     * Failsafe is a class that encapsulates a retry mechanism with conditions and actions.
     * It allows for a specified number of retries with a condition and an action to run on each retry.
     */
    public static class Failsafe {
        public final Function<Integer, BooleanSupplier> condition;
        public final Function<Integer, Consumer<Runnable>> action;
        private BooleanSupplier curCondition;
        private Consumer<Runnable> curAction;
        public final Runnable andThen;
        private boolean active = true;
        public final int times;
        public int currentCount = 0;

        /**
         * Constructor for Failsafe.
         * @param condition a function that takes the current count and returns a BooleanSupplier condition
         * @param action a function that takes the current count and returns a Consumer<Runnable> action
         * @param times the number of times to retry
         * @param andThen an optional Runnable to run after all retries are completed
         */
        public Failsafe(Function<Integer, BooleanSupplier> condition, Function<Integer, Consumer<Runnable>> action, int times, Runnable andThen) {
            this.condition = condition;
            this.action = action;
            this.andThen = andThen;
            this.times = times;
            reset();
        }

        /**
         * Resets the Failsafe to its initial state.
         * This method sets the current count to 0, updates the current condition and action,
         * and sets active to true.
         */
        public void reset() {
            curCondition = condition.apply(currentCount);
            curAction = action.apply(currentCount);
            active = true;
        }

        /**
         * Retries the action if the current count is less than the specified times.
         * If the maximum number of retries is reached, it runs the andThen action if it is not null.
         */
        public void retry() {
            if (currentCount < times) {
                active = false;
                curAction.accept(this::reset);
                currentCount++;
            }

            if (currentCount >= times && andThen != null) {
                andThen.run();
            }
        }

        /**
         * Checks if the Failsafe has completed all retries.
         * @return true if the current count is greater than or equal to times, false otherwise
         */
        public boolean completed() {
            return currentCount >= times;
        }

        /**
         * Updates the Failsafe by checking the current condition.
         * If the condition is met, it retries the action.
         * @return true if the Failsafe has completed all retries, false otherwise
         */
        public boolean update() {
            if (!active) return completed();

            if (curCondition.getAsBoolean()) {
                retry();
            }

            return completed();
        }
    }

    /**
     * Constructor for FiniteFailsafeDelay.
     * @param condition a BooleanSupplier that returns true when the delay should be completed
     * @param action a Runnable action to run when the delay is completed
     */
    public FiniteFailsafeDelay(BooleanSupplier condition, Runnable action) {
        super(condition, action);
    }

    /**
     * Constructor for FiniteFailsafeDelay with a specified wait time.
     * @param wait the time in milliseconds to wait before executing the action
     * @param action a Runnable action to run when the delay is completed
     */
    public FiniteFailsafeDelay(double wait, Runnable action) {
        super(wait, action);
    }

    /**
     * Sets a failsafe condition with a retry mechanism.
     * @param failsafeCondition a BooleanSupplier that returns true when the failsafe condition is met
     * @param retry a Consumer<Runnable> that takes a Runnable to retry the action
     * @param times the number of times to retry the action
     * @param andThen an optional Runnable to run after all retries are completed
     * @return the FiniteFailsafeDelay instance for method chaining
     */
    public FiniteFailsafeDelay setFailsafe(BooleanSupplier failsafeCondition, Consumer<Runnable> retry, int times, Runnable andThen) {
        failsafes.add(new Failsafe(i -> failsafeCondition, i -> retry, times, andThen));
        return this;
    }

    /**
     * Sets a failsafe condition with a retry mechanism using a TimerCondition.
     * @param failsafes a collection of Failsafe objects to be added to the FiniteFailsafeDelay
     * @return the FiniteFailsafeDelay instance for method chaining
     */
    public FiniteFailsafeDelay setFailsafe(Collection<Failsafe> failsafes) {
        this.failsafes.addAll(failsafes);
        return this;
    }

    /**
     * Sets a failsafe condition with a retry mechanism using a fixed delay in milliseconds.
     * @param millis the delay in milliseconds before the retry action is executed
     * @param retry a Consumer<Runnable> that takes a Runnable to retry the action
     * @param times the number of times to retry the action
     * @param andThen an optional Runnable to run after all retries are completed
     * @return the FiniteFailsafeDelay instance for method chaining
     */
    public FiniteFailsafeDelay setFailsafe(double millis, Consumer<Runnable> retry, int times, Runnable andThen) {
        return setFailsafe(new TimerCondition(millis), retry, times, andThen);
    }

    /**
     * Sets a failsafe condition with a retry mechanism using a TimerCondition.
     * @param millis a Function that takes an integer and returns a double representing the delay in milliseconds
     * @param retry a Consumer<Runnable> that takes a Runnable to retry the action
     * @param times the number of times to retry the action
     * @param andThen an optional Runnable to run after all retries are completed
     * @return the FiniteFailsafeDelay instance for method chaining
     */
    public FiniteFailsafeDelay setFailsafe(Function<Integer, Double> millis, Consumer<Runnable> retry, int times, Runnable andThen) {
        failsafes.add(new Failsafe(i -> new TimerCondition(millis.apply(i)), i -> retry, times, andThen));
        return this;
    }

    /**
     * Sets a failsafe condition with a retry mechanism using a TimerCondition.
     * @param millis a Function that takes an integer and returns a double representing the delay in milliseconds
     * @param retry a Function that takes an integer and returns a Consumer<Runnable> to retry the action
     * @param times the number of times to retry the action
     * @param andThen an optional Runnable to run after all retries are completed
     * @return the FiniteFailsafeDelay instance for method chaining
     */
    public FiniteFailsafeDelay setFailsafe(Function<Integer, Double> millis, Function<Integer, Consumer<Runnable>> retry, int times, Runnable andThen) {
        failsafes.add(new Failsafe(i -> new TimerCondition(millis.apply(i)), retry, times, andThen));
        return this;
    }

    /**
     * Updates the FiniteFailsafeDelay by checking all failsafes.
     * @return true if all failsafes have completed, false otherwise
     */
    @Override
    public boolean update() {
        failsafes.removeIf(Failsafe::update);
        return super.update();
    }
}
