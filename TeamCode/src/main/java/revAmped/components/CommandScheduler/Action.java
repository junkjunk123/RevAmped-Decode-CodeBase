package revAmped.components.CommandScheduler;

import java.util.Objects;

public class Action {
    private final Runnable action;
    private final String identifier;
    private int count = 1;
    private int timesPerformed = 0;

    public Action(String identifier, Runnable action) {
        this.identifier = identifier;
        this.action = action;
    }

    public Action(String identifier, Runnable action, int count) {
        this(identifier, action);
        this.count = count;
    }

    public Action(Runnable action) {
        this(action.toString(), action);
    }

    public void run() {
        if (timesPerformed < count) {
            action.run();
            timesPerformed++;
        }
    }

    public boolean isCompleted() {
        return timesPerformed >= count;
    }

    public Runnable getAction() {
        return action;
    }

    public int getCount() {
        return count;
    }

    public int getTimesPerformed() {
        return timesPerformed;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void complete() {
        timesPerformed = count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTimesPerformed(int timesPerformed) {
        this.timesPerformed = timesPerformed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action action = (Action) o;
        return Objects.equals(getIdentifier(), action.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier());
    }
}
