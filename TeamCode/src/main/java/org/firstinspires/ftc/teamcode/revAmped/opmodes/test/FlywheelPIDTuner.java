package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@Config
public class FlywheelPIDTuner extends DecodeRevAmpedOpMode {
    private DcMotorEx flywheel1;
    private DcMotorEx flywheel2;
    public static int targetVelocity;
    private int prevPos;
    public static double P = 0.003;
    public static double I = 0;
    public static double D = 0.00002;
    public static double KV = 0;
    public static double KStatic = 0;
    private PIDFController controller;
    private double totalError = 0;
    private int sampleAmount = 0;
    private RevAmpedGamepad gamepad_1;
    private final ElapsedTime timer = new ElapsedTime();

    @Override
    public void RevAmpedInit() {
        flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel_left");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel_right");
        prevPos = flywheel2.getCurrentPosition();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        controller = new PIDFController(new PIDFCoefficients(P, I, D, KV));
        flywheel1.setDirection(DcMotorSimple.Direction.REVERSE);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
    }

    private int pos() {
        return flywheel2.getCurrentPosition();
    }

    @Override
    public void RevAmpedLoop() {
        controller.updateConstants(new PIDFCoefficients(P, I, D, KV));
        sampleAmount +=1;
        double deltaTime = timer.nanoseconds() / Math.pow(10, 9);
        double deltaPos = pos() - prevPos;
        double vel = deltaPos / deltaTime;
        vel = vel / Flywheel.ticksPerInch();
        double error = targetVelocity - vel;
        timer.reset();
        prevPos = pos();
        controller.updateFeedForwardInput(targetVelocity * Math.signum(error));
        controller.updateError(error);
        double power = controller.runPIDF() + KStatic * Math.signum(error);
        totalError +=error;
        if(gamepad_1.a.isPress()){
            totalError =0;
            sampleAmount=0;
        }
        flywheel1.setPower(power);
        flywheel2.setPower(power);
        telemetry.addData("error", error);
        telemetry.addData("power",power);
        telemetry.addData("vel",vel);
        telemetry.addData("average Error", totalError/sampleAmount);
        telemetry.addData("samples",sampleAmount);
    }

    @Override
    public void update() {
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
            flywheel1.setPower(1.0);
            flywheel2.setPower(1.0);
        }

        if (gamepad1.bWasPressed()) {
            flywheel1.setPower(0.0);
            flywheel2.setPower(0.0);
        }

        double deltaTime = timer.nanoseconds() / Math.pow(10, 9);
        double deltaPos = pos() - prevPos;
        double vel = deltaPos / deltaTime;
        timer.reset();
        prevPos = pos();

        telemetry.addData("vel", vel / Flywheel.ticksPerInch());
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
