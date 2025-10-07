package org.firstinspires.ftc.teamcode.revAmped.components.misc;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import revAmped.components.scheduler.Commands.Delay;
import revAmped.components.scheduler.Scheduler;

/**
 * Logger class for logging messages and debugging information.
 * This class provides methods to log messages, test conditions, and manage telemetry.
 * It also supports permanent logging of key-value pairs that persist until reset.
 */
public class Logger {
    /**
     * A map to hold permanent log entries.
     * This map is used to store key-value pairs for logging purposes.
     * The entries in this map will persist until reset.
     */
    private static HashMap<String, String> permaLog = new HashMap<>();

    /**
     * Resets the permanent log entries.
     * This method clears all entries in the permaLog map, effectively resetting the log.
     */
    public static void reset() {
        permaLog.clear();
    }

    /**
     * Tests a list of boolean conditions and runs an action if all conditions are true.
     * @param trigger The trigger that tells us the action should have init
     * @param action The action to init if all conditions are true
     * @param conditions The conditions to test
     * @return The index of the first condition that is false, or -1 if all conditions are true. Returns -2 if trigger is false.
     */

    public static int testConditions(boolean trigger, Runnable action, boolean... conditions) {
        if (trigger) {
            for (int i = 0; i < conditions.length; i++) {
                if (!conditions[i]) {
                    return i;
                }
            }

            action.run();
            return -1;
        }

        return -2;
    }

    /**
     * Logs a stack trace if the assumption is false.
     * @param assumption The assumption to test
     */
    public static void debugState(BooleanSupplier assumption) {
        if (!assumption.getAsBoolean()) {
            // Create and log a custom stack trace
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            StringBuilder stackTraceString = new StringBuilder();

            for (StackTraceElement element : stackTrace) {
                stackTraceString.append(element.toString()).append("\n");
            }

            Log.e("MyTag", "State issue detected! Stack trace:\n" + stackTraceString);
        }
    }

    /**
     * Throws an exception if the assumption is false.
     * @param assumption The assumption to test
     */
    public static void assertState(BooleanSupplier assumption) {
        assert assumption.getAsBoolean();
    }

    /**
     * Logs a message to telemetry for the remainder of the OpMode
     * @param caption The caption to display
     * @param value The value to display
     */
    public static void permaLog(String caption, Object value) {
        permaLog.put(caption, value.toString());
    }

    /**
     * Updates the telemetry with the permanent log entries.
     * @param telemetry The telemetry instance to update with the permanent log entries
     */
    public static void update(Telemetry telemetry) {
        for (String key : permaLog.keySet()) {
            telemetry.addData(key, permaLog.get(key));
        }
    }

    /**
     * Creates a telemetry instance that combines the provided telemetry with the FtcDashboard telemetry.
     * This allows for logging to both the standard telemetry and the FtcDashboard simultaneously.
     * @param opTelemetry The original telemetry instance to combine with FtcDashboard telemetry
     * @return A new Telemetry instance that combines both
     */
    public static Telemetry createMultiTelemetry(Telemetry opTelemetry) {
        return new MultipleTelemetry(opTelemetry, FtcDashboard.getInstance().getTelemetry());
    }

    /**
     * Tests a list of boolean conditions and runs an action if all conditions are true.
     * @param trigger The trigger that tells us the action should have init
     * @param action The action to init if all conditions are true
     * @param conditions The conditions to test
     */
    public static void permaLogConditionTest(boolean trigger, Runnable action, boolean... conditions) {
        if (trigger) {
            permaLog("Condition Test",testConditions(true, action, conditions));
        }
    }

    /**
     * Measures the time taken for an event to occur and saves it to an AtomicLong.
     * @param event The event to measure
     * @param scheduler The scheduler to add the delay to
     * @param save The AtomicLong to save the elapsed time
     */
    public static void timeToEvent(BooleanSupplier event, Scheduler scheduler, AtomicLong save) {
        long startTime = System.nanoTime();
        scheduler.add(new Delay(event, () -> {
            long elapsedTime = System.nanoTime() - startTime;
            save.set(elapsedTime);
        }));
    }

    /**
     * Logs the time taken for an event to occur and saves it to an AtomicLong.
     * @param event The event to measure
     * @param scheduler The scheduler to add the delay to
     * @param caption The caption for the logged time
     */
    public static void logTimeToEvent(BooleanSupplier event, Scheduler scheduler, String caption) {
        AtomicLong save = new AtomicLong();
        timeToEvent(event, scheduler, save);
        scheduler.add(new Delay(() -> save.get() != 0, () -> permaLog(caption, save.get())));
    }
}