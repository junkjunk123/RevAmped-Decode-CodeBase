package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwSlide;
import revAmped.components.MathUtil.SquID.SquIDController;

//@Disabled
@Config
@TeleOp(name = "HslideSquIDTuner", group = "Test")
public class SquIDTunerHslides extends OpMode {
    private HwSlide slides;
    private Servo hslideLock;
    public static float P = 0.025f, L = 0f, F = 0;
    private SquIDController controller;
    public static int targetPosition = ITDConstants.HSLIDES_MAX;
    private boolean homed = false;
    public static double homedCurrent = 0;

    @Override
    public void init() {
        slides = new HwSlide(hardwareMap, "hslides", DcMotorSimple.Direction.FORWARD);
        hslideLock = hardwareMap.get(Servo.class, "hlock");
        controller = new SquIDController(P, L, F, 10);
        slides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        slides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        controller.runToPosition(targetPosition, 2);
        hslideLock.setPosition(ITDConstants.HSLIDE_LOCK_UP);
    }

    @Override
    public void loop() {
        slides.update();
        controller.updateConstants(P, L, F);

        if (targetPosition != controller.getTargetPosition() && targetPosition > 5) hslideLock.setPosition(ITDConstants.HSLIDE_LOCK_UP);
        int currentPos = slides.getCurrentPosition();
        controller.setTargetPosition(targetPosition);
        slides.setPower(controller.run(currentPos));

        if (targetPosition < 5 && slides.getVelocity() <= 1) {
            slides.setPower(0f);
            hslideLock.close();
            homed = true;
        }

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("power",slides.motor.getPower());
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.addData("homed",homed);
        telemetry.addData("current",slides.motor.getCurrent(CurrentUnit.AMPS));
        telemetry.update();
    }
}
