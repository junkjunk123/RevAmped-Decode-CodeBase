package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.DelayScheduler;

public abstract class DelayedAuto extends DecodeRevAmpedAutonomous {
    private boolean locked = false;
    private Double[] delays;
    protected Prompter prompter;
    protected RevAmpedGamepad gamepad;
    boolean doneWithGamepadInput = false;

    @Override
    protected void setAutoState(int autoState) {
        double delay = delays[autoState];

        if (delay == 0) {
            super.setAutoState(autoState);
            return;
        }

        locked = true;
        scheduler.clear();
        DelayScheduler.getInstance().add(new Delay(MathUtil.booleanSupplierfromTimeDelay(delay), () -> {
            locked = false;
            super.setAutoState(autoState);
        }));
    }

    @Override
    public void RevAmpedLoop() {
        if (!locked)
            super.RevAmpedLoop();
    }

    @Override
    public void RevAmpedInit() {
        gamepad = new RevAmpedGamepad(gamepad1);
        prompter = new Prompter(this, gamepad);
        initPrompter(delays);
    }

    @Override
    public void RevAmpedInitLoop() {
        if (doneWithGamepadInput)
            return;

        if (prompter.isCompleted()) {
            extractValuesFromPrompter(delays);
            doneWithGamepadInput = true;
            RevAmpedInit();
        }

        prompter.run();
    }

    public abstract void initPrompter(Double[] delays);
    public abstract void extractValuesFromPrompter(Double[] delays);
}
