package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;

@Config
@TeleOp
public class TurretPIDTuner extends DecodeRevAmpedOpMode {
    private DcMotorEx turret;
    public static int targetPosition;
    private int startPos;
    public static double P = 0;
    public static double I = 0;
    public static double D = 0;
    public static double F = 0;
    private PIDFController controller;

    @Override
    public void RevAmpedInit() {
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        startPos = turret.getCurrentPosition();
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        controller = new PIDFController(new PIDFCoefficients(P, I, D, F));
    }

    private int pos() {
        return turret.getCurrentPosition() - startPos;
    }

    @Override
    public void RevAmpedLoop() {
        controller.updateConstants(new PIDFCoefficients(P, I, D, F));
        int error = targetPosition - pos();
        double power = controller.runWithError(error);
        turret.setPower(power);
        telemetry.addData("power",power);
    }

    @Override
    public void update() {
        telemetry.addData("currentPos",pos());
        telemetry.addData("error",targetPosition - pos());
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
