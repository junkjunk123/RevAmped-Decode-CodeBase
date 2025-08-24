package revAmped.components.Misc;

import java.util.function.Consumer;

public abstract class TeleOpAction {
    public abstract void run();
    protected boolean isCompleted = false;

    public boolean isCompleted() {
        return isCompleted;
    }

    public abstract String getIdentifier();

    public static TeleOpAction delaylessAction(Runnable action, String identifier) {
        return new TeleOpAction() {
            @Override
            public void run() {
                action.run();
                isCompleted = true;
            }

            @Override
            public String getIdentifier() {
                return identifier;
            }
        };
    }

    public static TeleOpAction createAction(Consumer<Runnable> action, String identifier) {
        return new TeleOpAction() {
            @Override
            public void run() {
                action.accept(() -> isCompleted = false);
            }

            @Override
            public String getIdentifier() {
                return identifier;
            }
        };
    }
}
