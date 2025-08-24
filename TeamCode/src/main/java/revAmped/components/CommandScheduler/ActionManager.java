package revAmped.components.CommandScheduler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager {
    private final ConcurrentHashMap<String, Action> actions = new ConcurrentHashMap<>();

    public ActionManager() {}

    public void run(String identifier, Runnable action, int count) {
        actions.compute(identifier, (key, existingAction) -> {
            if (existingAction == null) {
                // No action exists, create a new one
                Action newAction = new Action(identifier, action, count);
                newAction.run(); // Execute it once
                return newAction; // Add the new action
            } else if (!existingAction.isCompleted()) {
                // Action exists but is not completed
                existingAction.run();
                return existingAction; // Keep it in the map
            } else {
                // Action is already completed, do nothing
                return existingAction;
            }
        });
    }

    public void run(Action action) {
        run(action.getIdentifier(), action.getAction(), action.getCount());
    }

    public void run(Runnable action) {
        int size = actions.size();
        run(String.valueOf(size), action);
    }

    public void run(Runnable action, int count) {
        int size = actions.size();
        run(String.valueOf(size), action, count);
    }

    public void run(String identifier, Runnable action) {
        run(identifier, action, 1);
    }

    public void remove(String identifier) {
        actions.remove(identifier);
    }

    public void clear() {
        actions.clear();
    }

    public Set<Action> getActions() {
        return Set.copyOf(actions.values());
    }

    public Set<String> getIdentifiers() {
        return Set.copyOf(actions.keySet());
    }

    public Action getAction(String identifier) {
        return actions.get(identifier);
    }
}