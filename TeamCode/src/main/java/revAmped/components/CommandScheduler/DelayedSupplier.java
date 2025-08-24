package revAmped.components.CommandScheduler;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * DelayedSupplier is a class that allows you to lazily evaluate a supplier after a delay.
 * It can be used to create delayed execution of a supplier's result.
 * @param <T> the type of the result produced by the supplier
 */
public class DelayedSupplier<T> {
    private T value;
    private Delay delay;

    /**
     * Constructor for DelayedSupplier.
     * @param delay a BooleanSupplier that determines when the delay is over
     * @param supplier a Supplier that provides the result when the delay is over
     */
    public DelayedSupplier(BooleanSupplier delay, Supplier<T> supplier) {
        value = null;
        this.delay = new Delay(delay, () -> {
            value = supplier.get();
        });
    }

    /**
     * Constructor for DelayedSupplier with a fixed delay in seconds.
     * @param delay the delay in seconds before the supplier is evaluated
     * @param supplier a Supplier that provides the result when the delay is over
     */
    public DelayedSupplier(double delay, Supplier<T> supplier) {
        value = null;
        this.delay = new Delay(delay, () -> {
            value = supplier.get();
        });
    }

    /**
     * Sets a cancelation condition for the delay.
     * @param canceler a BooleanSupplier that determines if the delay should be canceled
     * @return this DelayedSupplier instance for method chaining
     */
    public DelayedSupplier<T> setCancelation(BooleanSupplier canceler) {
        if (delay != null) {
            delay.setCancellation(canceler);
        }
        return this;
    }

    /**
     * Sets a cancelation condition for the delay with an active period.
     * @param activePeriod the amount of time in seconds that must pass before the delay is canceled
     * @return this DelayedSupplier instance for method chaining
     */
    public DelayedSupplier<T> setCancelation(double activePeriod) {
        if (delay != null) {
            delay.setCancellation(activePeriod);
        }
        return this;
    }

    /**
     * Sets a cancelation condition for the delay with an active period and then it is run.
     * @param maxTime the amount of time in seconds that must pass before the delay is run anyways
     * @return this DelayedSupplier instance for method chaining
     */
    public DelayedSupplier<T> setMaxTime(double maxTime) {
        if (delay != null) {
            delay.setMaxTimeToRun(maxTime);
        }
        return this;
    }

    /**
     * Gets the result of the supplier after the delay.
     * @return a DelayedResult that will provide the value after the delay
     */
    public DelayedResult<T> get() {
        Scheduler.getInstance().add(delay);
        return new DelayedResult<>(() -> value);
    }

    /**
     * Gets the result of the supplier after the delay, blocking the current thread until the value is available.
     * @param thread the ArtificialThread that will be stopped while waiting for the value
     * @return a DelayedResult that will provide the value after the delay
     */
    public DelayedResult<T> get(ArtificialThread<?> thread) {
        thread.stop();
        Scheduler.getInstance().add(new Delay(() -> value != null, thread::start));
        return get();
    }
}
