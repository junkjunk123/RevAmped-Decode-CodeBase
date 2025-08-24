package revAmped.components.CommandScheduler;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import revAmped.components.MathUtil.MathUtil;

public class Delay {
    private final Runnable action;
    protected boolean completed;
    private final BooleanSupplier condition;
    public BooleanSupplier canceler;
    private Runnable cancelationAction;

    public Delay(BooleanSupplier condition, Runnable action) {
        this.action = action;
        this.condition = condition;
    }

    /**
     * @param wait millis
     */
    public Delay(double wait, Runnable action) {
        long initialTime = MathUtil.currentTimeMonotonicMillis();
        this.action = action;
        condition = () -> (MathUtil.currentTimeMonotonicMillis() - initialTime) >= wait;
    }

    public Delay setCancelation(BooleanSupplier canceler) {
        this.canceler = canceler;
        return this;
    }

    public Delay setCancelation(double activePeriod) {
        final long initialTime = MathUtil.currentTimeMonotonicMillis();
        canceler = () -> (MathUtil.currentTimeMonotonicMillis() - initialTime) >= activePeriod;
        return this;
    }

    public Delay setCancelation(double activePeriod, Runnable cancelationAction) {
        final long initialTime = MathUtil.currentTimeMonotonicMillis();
        canceler = () -> (MathUtil.currentTimeMonotonicMillis() - initialTime) >= activePeriod;
        this.cancelationAction = cancelationAction;
        return this;
    }

    public Delay setCancelation(BooleanSupplier canceler, Runnable cancelationAction) {
        this.canceler = canceler;
        this.cancelationAction = cancelationAction;
        return this;
    }

    public Delay setMaxTimeToRun(double millis) {
        return setCancelation(millis, action);
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void run() {
        completed = true;
        action.run();
    }

    public void update() {
        if (!completed) {
            if (canceler != null && canceler.getAsBoolean()) {
                completed = true;

                if (cancelationAction != null) {
                    cancelationAction.run();
                }

                return;
            }

            if (condition.getAsBoolean()) {
                completed = true;
                action.run();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delay)) return false;
        Delay delay2 = (Delay) o;
        return isCompleted() == delay2.isCompleted() && Objects.equals(getAction(), delay2.getAction()) && Objects.equals(getCondition(), delay2.getCondition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAction(), isCompleted(), getCondition());
    }
}
