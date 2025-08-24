package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.util.CustomPIDFCoefficients;
import com.pedropathing.util.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import revAmped.components.Hardware.HwSlide;

@Config
@TeleOp(name = "PIDFTunerVslides", group = "Test")
public class PIDFTunerVslides extends OpMode {
    private HwSlide left;
    private HwSlide right;
    public static float P = 0f, I = 0f, D = 0f, F = 0f;
    private PIDFController controller;
    public static int targetPosition = 500;
    public ElapsedTime timer = new ElapsedTime();
    private boolean updateTimer = false;
    private double time = 1000000;

    @Override
    public void init() {
        left = new HwSlide(hardwareMap, "vslideMotorLeft", DcMotorSimple.Direction.REVERSE);
        right = new HwSlide(hardwareMap, "vslideMotorRight", DcMotorSimple.Direction.FORWARD);
        controller = new PIDFController(new CustomPIDFCoefficients(P, I, D, F));
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        controller.setTargetPosition(targetPosition);
    }

    @Override
    public void loop() {
        left.update();
        right.update();

        controller.setCoefficients(new CustomPIDFCoefficients(P, I, D, F));

        if (targetPosition != controller.getTargetPosition()) {
            timer.reset();
            controller.setTargetPosition(targetPosition);
            updateTimer = true;
        }

        int currentPos = left.getCurrentPosition();
        controller.updateFeedForwardInput(currentPos * currentPos * Math.signum(targetPosition - currentPos));
        controller.updatePosition(currentPos);

        if (controller.getError() < 10 && updateTimer) {
            updateTimer = false;
            time = timer.milliseconds();
        }

        double power = controller.runPIDF();
        left.setPower(power);
        right.setPower(power);

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("power",power);
        telemetry.addData("powerLeft",left.motor.getPower());
        telemetry.addData("powerRight",right.motor.getPower());
        telemetry.addData("timeToExtend",time);
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.update();
    }
}
