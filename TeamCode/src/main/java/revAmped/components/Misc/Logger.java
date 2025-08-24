package revAmped.components.Misc;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class Logger {
    private HashMap<String, String> permaLog = new HashMap<>();

    /**
     * Tests a list of boolean conditions and runs an action if all conditions are true.
     * @param trigger The trigger that tells us the action should have run
     * @param action The action to run if all conditions are true
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
    public void permaLog(String caption, Object value) {
        permaLog.put(caption, value.toString());
    }

    public void update(Telemetry telemetry) {
        for (String key : permaLog.keySet()) {
            telemetry.addData(key, permaLog.get(key));
        }
    }

    public static Telemetry createMultiTelemetry(Telemetry opTelemetry) {
        return new MultipleTelemetry(opTelemetry, FtcDashboard.getInstance().getTelemetry());
    }

    /**
     * Tests a list of boolean conditions and runs an action if all conditions are true.
     * @param trigger The trigger that tells us the action should have run
     * @param action The action to run if all conditions are true
     * @param conditions The conditions to test
     */
    public void permaLogConditionTest(boolean trigger, Runnable action, boolean... conditions) {
        if (trigger) {
            permaLog("Condition Test",testConditions(trigger, action, conditions));
        }
    }

    public static void timeToEvent(BooleanSupplier event, Scheduler scheduler, AtomicLong save) {
        long startTime = System.nanoTime();
        scheduler.add(new Delay(event, () -> {
            long elapsedTime = System.nanoTime() - startTime;
            save.set(elapsedTime);
        }));
    }

    public void logTimeToEvent(BooleanSupplier event, Scheduler scheduler, String caption) {
        AtomicLong save = new AtomicLong();
        timeToEvent(event, scheduler, save);
        scheduler.add(new Delay(() -> save.get() != 0, () -> permaLog(caption, save.get())));
    }
}