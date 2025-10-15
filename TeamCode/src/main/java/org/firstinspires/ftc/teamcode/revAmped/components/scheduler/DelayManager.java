package org.firstinspires.ftc.teamcode.revAmped.components.scheduler;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.BooleanSupplier;

/**
 * DelayManager is a class that manages multiple Delay objects.
 * It allows you to add, remove, and update delays, as well as run actions after a specified delay.
 */
public class DelayManager {
    /**
     * A set of Delay objects that are managed by this DelayManager.
     * It uses a ConcurrentSkipListSet to allow for thread-safe operations and sorted access.
     */
    private final ConcurrentSkipListSet<Delay> delays = new ConcurrentSkipListSet<>(Comparator.comparingInt(Delay::hashCode));

    public DelayManager() {}

    /**
     * Adds a Delay object to the set.
     */
    public void add(Delay delay) {
        delays.add(delay);
    }

    /**
     * @param millis time for delay
     * @param action action to be performed
     */
    public void add(double millis, Runnable action) {
        add(new Delay(millis, action));
    }

    /**
     * Sets the action to occur a set amount of time after the condition is satisfied
     */
    public void add(double millis, BooleanSupplier condition, Runnable action) {
        add(new Delay(condition, () -> add(new Delay(millis, action))));
    }

    public void addOptionalDelay(boolean runInstantly, Delay delay) {
        if (runInstantly) {
            delay.run();
        } else {
            add(delay);
        }
    }

    public void addOptionalDelay(boolean runInstantly, double millis, Runnable action) {
        if (runInstantly) {
            action.run();
        } else {
            add(millis, action);
        }
    }

    public void addMinTimeToRunDelay(double minTime, BooleanSupplier condition, Runnable action) {
        add(new Delay(minTime, () -> add(new Delay(condition, action))));
    }

    /**
     * Removes a Delay object from the set.
     */
    public void remove(Delay delay) {
        delays.remove(delay);
    }

    /**
     * Returns a safe, unmodifiable copy of the current set of delays.
     * Useful for asynchronous access without risk of ConcurrentModificationException.
     */
    public Set<Delay> getDelays() {
        return Set.copyOf(delays);  // Returns a snapshot of the set for safety
    }

    public void clear() {
        delays.clear();
    }

    /**
     * Updates all Delay objects in the set.
     * If a Delay is completed, it is removed from the set.
     */
    public void update() {
        // Iterate through the set and check for completed delays
        for (Delay delay : delays) {
            delay.update();
            if (delay.isCompleted()) {
                delays.remove(delay);  // Remove the completed delay from the set
            }
        }
    }
}