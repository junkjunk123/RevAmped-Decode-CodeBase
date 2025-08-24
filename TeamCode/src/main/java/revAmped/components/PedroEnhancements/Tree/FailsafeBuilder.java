package revAmped.components.PedroEnhancements.Tree;

import java.util.function.BooleanSupplier;

public class FailsafeBuilder {
    private Node action;
    private BooleanSupplier runCondition;
    private double millisToJump;
    private long jumpStartTime;
    private int jumpState = 0;

    public FailsafeBuilder setAction(Node action) {
        this.action = action;
        return this;
    }

    public FailsafeBuilder setJumpStartTime(long jumpStartTime) {
        this.jumpStartTime = jumpStartTime;
        return this;
    }

    public FailsafeBuilder setJumpState(int jumpState) {
        this.jumpState = jumpState;
        return this;
    }

    public FailsafeBuilder setMillisToJump(double millisToJump) {
        this.millisToJump = millisToJump;
        return this;
    }

    public FailsafeBuilder setRunCondition(BooleanSupplier runCondition) {
        this.runCondition = runCondition;
        return this;
    }

    public Failsafe build() {
        Failsafe failsafe = new Failsafe(runCondition);
        failsafe.addNode(action);
        failsafe.setMillisToJump(jumpState, millisToJump);
        millisToJump = 0;
        jumpState = 0;
        action = null;
        return failsafe;
    }
}
