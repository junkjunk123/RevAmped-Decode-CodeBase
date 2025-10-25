package org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands;

import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;

import java.util.function.BooleanSupplier;

public class Pause implements RunnableTask {
    protected boolean complete = false;
    private final BooleanSupplier isDone;

    public Pause(double millis) {
        isDone = MathUtil.booleanSupplierfromTimeDelay(millis);
    }

    public Pause(BooleanSupplier done) {
        isDone = done;
    }

    public Pause(BooleanSupplier done, double millis) {
        BooleanSupplier time = MathUtil.booleanSupplierfromTimeDelay(millis);
        isDone = () -> done.getAsBoolean() || time.getAsBoolean();
    }

    @Override
    public void reset() {
        complete = false;
    }

    @Override
    public boolean update() {
        if (!complete && isDone.getAsBoolean()) {
            complete = true;
        }
        return complete;
    }

    @Override
    public boolean isCompleted() {
        return complete;
    }

    public static class OffsetCanceledPause extends Pause {
        private final BooleanSupplier startCancel;
        private final double millis;
        private boolean startedCancel;
        private BooleanSupplier time;

        public OffsetCanceledPause(BooleanSupplier done, BooleanSupplier startCancel, double millis) {
            super(done);
            this.startCancel = startCancel;
            this.millis = millis;
        }

        @Override
        public boolean update() {
            if (isCompleted()) return true;

            if (!startedCancel) {
                if (startCancel.getAsBoolean()) {
                    startedCancel = true;
                    time = MathUtil.booleanSupplierfromTimeDelay(millis);
                }
            }

            if (time != null && time.getAsBoolean()) {
                complete = true;
                return true;
            }

            return super.update();
        }
    }
}
