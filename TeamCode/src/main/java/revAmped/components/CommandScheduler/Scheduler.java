package revAmped.components.CommandScheduler;

import java.util.function.BooleanSupplier;

public class Scheduler {
    public final DelayManager delays;
    public final ActionManager actions;
    private static CommandBuilder commandBuilder;

    public Scheduler() {
        delays = new DelayManager();
        actions = new ActionManager();
        commandBuilder = new CommandBuilder(this);
    }

    public void clear() {
        delays.clear();
        actions.clear();
    }

    public void update() {
        delays.update();
    }

    public void add(Delay delay) {
        delays.add(delay);
    }

    public void addOptionalDelay(boolean runInstantly, Delay delay) {
        delays.addOptionalDelay(runInstantly, delay);
    }

    public void run(String identifier, Runnable action, int count) {
        actions.run(identifier, action, count);
    }

    public void run(Action action) {
        actions.run(action);
    }

    public void run(Runnable action) {
        actions.run(action);
    }

    public void run(Runnable action, int count) {
        actions.run(action, count);
    }

    public void run(Runnable action, BooleanSupplier forgetAction) {
        Action action1 = new Action(action);
        actions.run(action1);
        add(new Delay(forgetAction, () -> actions.remove(action1.getIdentifier())));
    }

    public void run(String identifier, Runnable action) {
        actions.run(identifier, action);
    }

    public static CommandBuilder commandBuilder() {
        return commandBuilder;
    }
}
