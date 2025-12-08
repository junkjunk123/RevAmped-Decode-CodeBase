package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.ivy.ICommand;
import com.pedropathing.ivy.Scheduler;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Logger;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.DelayScheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

public class OpModeCommand extends RevAmpedOpMode {
    private final DelayScheduler scheduler = new DelayScheduler();

    /**
     * Cancels all previous commands
     */
    public void reset() {
        Scheduler.getInstance().reset();
        scheduler.clear();
    }

    /**
     * Schedules objects to the scheduler
     */
    public void schedule(ICommand... commands) {
        Scheduler.getInstance().schedule(commands);
    }

    @Override
    public void RevAmpedInit() {
        Globals.init(telemetry);
        Logger.reset();
    }

    @Override
    public void update() {}

    @Override
    public OpModeType getOpModeType() {
        return Robot.INSTANCE.getOpModeType();
    }

    @Override
    public void RevAmpedLoop() {
        scheduler.update();
        Scheduler.getInstance().execute();
    }

    public void RevAmpedStop() {
        reset();
    }
}