package revAmped.components.PedroEnhancements;

import com.pedropathing.follower.Follower;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class AutonomousEnumeratedAction {
    protected boolean completed = false;
    protected boolean initCompleted = false;
    protected abstract void init();
    protected abstract void loop();
    public abstract boolean finishedDriving();
    public String identifier() {return "";}
    public NextStateGenerator nextStateGenerator() {return i -> i+1;}

    public boolean update() {
        if (!initCompleted) {
            init();
            initCompleted = true;
        } else if (!completed) {
            loop();
        }

        return completed;
    }

    public interface NextStateGenerator {
        int nextState(int current);
    }

    public static BooleanSupplier followerBusy(Follower follower) {
        return () -> !follower.isBusy();
    }
}
