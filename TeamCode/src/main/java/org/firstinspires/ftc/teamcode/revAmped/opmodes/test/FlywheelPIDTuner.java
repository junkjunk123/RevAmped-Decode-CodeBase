package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF.PSFLCoefficients;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
@Config
public class FlywheelPIDTuner extends DecodeRevAmpedOpMode {
    private Flywheel flywheel;
    public static int targetVelocity;
    private double totalError = 0;
    private int sampleAmount = 0;
    public static double P = 0.0001;
    public static double K_static = 0.03;
    public static double K_V = 0.0005;
    public static double L = 0.3;
    private RevAmpedGamepad gamepad_1;
    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void RevAmpedInit() {
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.RUNNING);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Globals.telemetry = telemetry;
        gamepad_1 = new RevAmpedGamepad(gamepad1);
    }

    @Override
    public void RevAmpedLoop() {
        flywheel.run(targetVelocity);
        double vel = flywheel.getVelocityImperial();;
        sampleAmount++;
        telemetry.addData("error", targetVelocity - vel);
        telemetry.addData("power",flywheel.motor.getPower());
        telemetry.addData("vel",vel);
        telemetry.addData("average Error", totalError/sampleAmount);
        //telemetry.addData("samples",sampleAmount);
    }

    @Override
    public void update() {
        flywheel.update();
        telemetry.update();
        gamepad_1.update();
    }

    @Override
    public void RevAmpedStart() {
        timer.reset();
    }

    @Override
    public void RevAmpedInitLoop() {
        if (gamepad1.aWasPressed()) {
            flywheel.setPower(1.0);
        }

        if (gamepad1.bWasPressed()) {
            flywheel.setPower(0.0);
        }

        telemetry.addData("vel", flywheel.getVelocityImperial());
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
