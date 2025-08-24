package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import revAmped.components.Hardware.HwSlide;
import revAmped.components.MathUtil.SquID.SquIDController;

@Config
@TeleOp(name = "SquIDTunerVslides", group = "Test")
public class SquIDTunerVslides extends OpMode {
    private HwSlide left;
    private HwSlide right;
    public static float P = 0.06f, L = 0f, F = 0f;
    private SquIDController controller;
    public static int targetPosition = 500;
    public static int deadZoneTolerance = 0;
    private ElapsedTime timer = new ElapsedTime();
    private boolean updateTimer = false;
    private double millis = 10000000;

    @Override
    public void init() {
        left = new HwSlide(hardwareMap, "vslideMotorLeft", DcMotorSimple.Direction.REVERSE);
        right = new HwSlide(hardwareMap, "vslideMotorRight", DcMotorSimple.Direction.FORWARD);
        controller = new SquIDController(P, L, F, 20);
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        controller.runToPosition(targetPosition, deadZoneTolerance);
    }

    @Override
    public void loop() {
        left.update();
        right.update();

        controller.updateConstants(P, L, F);

        int currentPos = left.getCurrentPosition();
        controller.runToPosition(targetPosition, deadZoneTolerance);
        double power = controller.run(currentPos, true);
        left.setPower(power);
        right.setPower(power);

        if (targetPosition != controller.getTargetPosition()) {
            timer.reset();
            controller.setTargetPosition(targetPosition);
            updateTimer = true;
        }

        if (controller.getError() < 10 && updateTimer) {
            updateTimer = false;
            millis = timer.milliseconds();
        }

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("powerLeft",left.motor.getPower());
        telemetry.addData("powerRight",right.motor.getPower());
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.addData("millis",millis);
        telemetry.update();
    }
}
