package org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util;

import android.util.Log;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.DelayScheduler;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * DelayedResult is a class that allows you to lazily evaluate a result using a Callable supplier.
 * It provides methods to check if the result is ready, get the result, and perform actions based on the result.
 *
 * @param <T> the type of the result
 */
public class DelayedResult<T> {
    private final Callable<T> supplier;
    private T val;

    /**
     * Constructor for DelayedResult.
     *
     * @param supplier a Callable that provides the result when called
     */
    public DelayedResult(Callable<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Checks if the result is ready.
     * @return true if the result is ready (not null), false otherwise
     */
    public boolean isDone() {
        try {
            val = supplier.call();
            return val != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the result, evaluating it if it hasn't been done yet.
     *
     * @return the result of type T
     * @throws RuntimeException if an error occurs while getting the value
     */
    public T get() {
        if (val == null) {
            try {
                val = supplier.call();
            } catch (Exception e) {
                Log.e("DelayedResult", "Error getting value", e);
                throw new RuntimeException(e);
            }
        }

        return val;
    }

    /**
     * Gets the result and runs an action if the result is not null.
     *
     * @param action a Runnable to be executed if the result is not null
     * @return the result of type T
     * @throws RuntimeException if an error occurs while getting the value
     */
    public T getAndThen(Runnable action) {
        if (val == null) {
            try {
                val = supplier.call();

                if (val != null) {
                    action.run();
                }
            } catch (Exception e) {
                Log.e("DelayedResult", "Error getting value", e);
                throw new RuntimeException(e);
            }
        }

        return val;
    }

    /**
     * Gets the result as an Optional, allowing for null values.
     *
     * @return an Optional containing the result, or empty if the result is null
     */
    public Optional<T> getOptional() {
        try {
            return Optional.ofNullable(supplier.call());
        } catch (Exception e) {
            Log.e("DelayedResult", "Error getting optional value", e);
            return Optional.empty();
        }
    }

    /**
     * Sets the value using a Consumer.
     *
     * @param set a Consumer that accepts the value to be set
     */
    public void set(Consumer<T> set) {
        set.accept(val);
    }

    /**
     * Executes an action after the result is ready.
     *
     * @param action a Consumer that accepts the result of type T
     */
    public void andThen(Consumer<T> action) {
        DelayScheduler.getInstance().add(new Delay(this::isDone, () -> {
            try {
                action.accept(val);
            } catch (Exception e) {
                Log.e("DelayedResult", "Error executing action", e);
            }
        }));
    }
}
