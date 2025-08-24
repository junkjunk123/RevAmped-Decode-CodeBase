package revAmped.components.PedroEnhancements.Tree;

import java.util.function.BooleanSupplier;

import revAmped.components.MathUtil.MathUtil;

public class Failsafe {
    private Node action;
    private BooleanSupplier runCondition;
    private double millisToJump;
    private boolean terminated = false;
    private long jumpStartTime;
    private int jumpState = 0;
    private boolean jump = false;

    public Failsafe(BooleanSupplier run) {
        this.runCondition = run;
    }

    public Failsafe(double millis) {
        long initialTime = MathUtil.currentTimeMonotonicMillis();
        runCondition = () -> (MathUtil.currentTimeMonotonicMillis() - initialTime) >= millis;
    }

    public void addNode(Node action) {
        this.action = action;
    }

    public int getJumpState() {
        return jumpState;
    }

    public void setMillisToJump(int state, double millisToJump) {
        this.jumpState = state;
        this.millisToJump = millisToJump;
    }

    public boolean update(boolean stop) {
        if (stop) {
            terminated = true;
        }

        if (!terminated) {
            if (runCondition.getAsBoolean()) {
                terminated = true;

                if (action != null) {
                    action.run();

                    if (action.isComplete()) {
                        jumpStartTime = MathUtil.currentTimeMonotonicMillis();
                    }
                }
            }
        }

        if (!jump && MathUtil.currentTimeMonotonicMillis() - jumpStartTime >= millisToJump) {
            jump = true;
        }

        return terminated;
    }

    public boolean needToJump() {
        return jump;
    }

    public void setJumpState(int jumpState) {
        this.jumpState = jumpState;
    }
}
