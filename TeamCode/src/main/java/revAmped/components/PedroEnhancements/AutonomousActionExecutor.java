package revAmped.components.PedroEnhancements;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AutonomousActionExecutor {
    public static ElapsedTime overallTimer;
    public static ElapsedTime preloadDriveTimer;
    protected List<AutonomousEnumeratedAction> actions;
    private static ArrayList<String> identifiers;
    private static int autoState;
    public static boolean driving;
    private static int size;

    public AutonomousActionExecutor() {
        overallTimer = new ElapsedTime();
        preloadDriveTimer = new ElapsedTime();
        actions = Collections.emptyList();
        identifiers = new ArrayList<>();
        size = 0;
        autoState = 0;
        driving = false;
    }

    public void setAutoState(int state) {
        if (state < actions.size()) {
            autoState = state;
            driving = true;
        }
    }

    protected abstract void buildActions();

    public void build() {
        buildActions();
        size = actions.size();

        for (AutonomousEnumeratedAction action : actions) {
            identifiers.add(action.identifier());
        }
    }

    public AutonomousEnumeratedAction getAutoStateAction(int autoState) {
        return actions.get(autoState);
    }

    public boolean update() {
        return actions.get(autoState).update();
    }

    public static int getAutoState() {
        return autoState;
    }

    public static boolean finalState() {
        return autoState == size - 1;
    }

    public static List<String> getIdentifiers() {
        return identifiers;
    }

    public static int firstIndex(int action) {
        return identifiers.indexOf(identifiers.get(action));
    }

    public static int firstIndex(String identifier) {
        return identifiers.indexOf(identifier);
    }

    public static int firstIndexOfCurrent() {
        return firstIndex(autoState);
    }

    public static int iterationNumber(int action) {
        int count = 0;
        String identifier = identifiers.get(action);
        for (int i = 0; i < action; i++) {
            if (identifiers.get(i).equals(identifier)) {
                count++;
            }
        }
        return count;
    }

    public static int currentIterationNumber() {
        return iterationNumber(autoState);
    }

    public static boolean finalIteration() {
        String identifier = identifiers.get(autoState);
        for (int i = autoState; i < identifiers.size(); i++) {
            if (identifiers.get(i).equals(identifier)) {
                return false;
            }
        }

        return true;
    }

    public static int nextIndex(String identifier) {
        for (int i = autoState; i < identifiers.size(); i++) {
            if (identifiers.get(i).equals(identifier)) {
                return i;
            }
        }

        throw new RuntimeException("AutonomousActionExecutor.nextIndex(): Identifier not found after specified action");
    }
}
