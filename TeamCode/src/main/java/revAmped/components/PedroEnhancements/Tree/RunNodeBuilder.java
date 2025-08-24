package revAmped.components.PedroEnhancements.Tree;

import java.util.function.BooleanSupplier;

public class RunNodeBuilder {
    private Runnable action;
    private BooleanSupplier isComplete;
    public Failsafe failsafe;

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setCompleteCondition(BooleanSupplier complete) {
        isComplete = complete;
    }

    public void setFailsafe(Failsafe failsafe1) {
        failsafe = failsafe1;
    }

    public RunNode build() {
        RunNode runNode = new RunNode(action);
        runNode.setComplete(isComplete);
        runNode.setFailsafe(failsafe);
        action = null;
        isComplete = null;
        failsafe = null;
        return runNode;
    }
}
