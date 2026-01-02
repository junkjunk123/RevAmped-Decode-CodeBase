package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Config
@TeleOp
public class FlywheelTuner extends OpMode {
    private Flywheel flywheel;
    public static float targetVelocity = 1000;
    private final ElapsedTime timer = new ElapsedTime();
    private double time = 0;
    private boolean measured = false;
    public static boolean track;
    public static boolean reset;
    private double minVel = Double.MAX_VALUE;

    @Override
    public void init() {
        Globals.constants.build();
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.RUNNING);
        flywheel.run(targetVelocity);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        flywheel.update();

        if (flywheel.getError() < 20 && !measured) {
            time = timer.milliseconds();
            measured = true;
        }

        if (targetVelocity != flywheel.getTargetVelocity()) {
            flywheel.run(targetVelocity);
            timer.reset();
        }

        if (track) {
            minVel = Math.min(minVel, flywheel.getFilteredVelocity());
        }

        if (reset) {
            reset = false;
            timer.reset();
            measured = false;
        }

        telemetry.addData("target", flywheel.getTargetVelocity());
        telemetry.addData("error", flywheel.getError());
        telemetry.addData("flywheelVel", flywheel.getVelocityImperial());
        telemetry.addData("flywheelFilteredVel", flywheel.getFilteredVelocity());
        telemetry.addData("minVel", minVel);
        telemetry.addData("time", time);
        telemetry.update();
    }
}
