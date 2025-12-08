package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.ivy.ICommand;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Logger;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

public class OpModeCommand extends RevAmpedOpMode {
    private final Scheduler scheduler = new Scheduler();

    /**
     * Cancels all previous commands
     */
    public void reset() {
        com.pedropathing.ivy.Scheduler.getInstance().reset();
        scheduler.clear();
    }

    /**
     * Schedules objects to the scheduler
     */
    public void schedule(ICommand... commands) {
        com.pedropathing.ivy.Scheduler.getInstance().schedule(commands);
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
        com.pedropathing.ivy.Scheduler.getInstance().execute();
    }

    public void RevAmpedStop() {
        reset();
    }
}