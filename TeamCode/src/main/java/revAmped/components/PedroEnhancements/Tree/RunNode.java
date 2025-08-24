package revAmped.components.PedroEnhancements.Tree;

import java.util.function.BooleanSupplier;

public class RunNode implements Node {
    private Runnable action;
    private BooleanSupplier isComplete;
    public Failsafe failsafe;
    private boolean complete = false;
    private boolean started = false;

    public RunNode(Runnable runnable) {
        action = runnable;
    }

    public void setComplete(BooleanSupplier isComplete) {
        this.isComplete = isComplete;
    }

    public void setFailsafe(Failsafe failsafe) {
        this.failsafe = failsafe;
    }

    @Override
    public boolean isComplete() {
        if (!complete && isComplete != null) {
            complete = isComplete.getAsBoolean();
        }

        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public void run() {
        if (!complete) {
            started = true;
            action.run();
        }
    }

    @Override
    public void update() {
        boolean completed = complete;

        if (started) {
            isComplete();
        }

        if (failsafe != null && !complete) {
            if (failsafe.update(false)) {
                complete = true;
            }
        } else if (!completed && complete) {
            failsafe.update(true);
        }
    }

    @Override
    public int nextState(int state) {
        if (failsafe != null && failsafe.needToJump()) {
            return failsafe.getJumpState();
        }

        return state+1;
    }
}
