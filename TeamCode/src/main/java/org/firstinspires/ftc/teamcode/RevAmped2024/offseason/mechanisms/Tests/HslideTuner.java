package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.Hardware.HwSlide;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;
import revAmped.util.Range;

@Config
@TeleOp(name = "HslideTuner", group = "Test")
public class HslideTuner extends RevAmpedTestOpMode {
    private HwSlide hslides;
    public static double K = 0.6f, F = 0.05f, D =  0.001, C = 2;
    public static int targetPosition = HslideMotor.FULL_EXTENSION;
    private ElapsedTime timer = new ElapsedTime();
    private double vel = 0;
    private int prevError = 0;

    @Override
    public void RevAmpedInit() {

    }

    @Override
    public void RevAmpedStart() {
        timer.reset();
    }

    @Override
    public void update() {
        hslides.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

    @Override
    public void RevAmpedLoop() {
        int currentPos = hslides.getCurrentPosition();
        int error = targetPosition - currentPos;
        double seconds = timer.seconds();
        timer.reset();
        double deltaError = (double) (error - prevError)/seconds;
        prevError = error;

        double correction = deltaError * D;

        double sigmoid = Range.scale(1 - (double) Math.abs(error) / HslideMotor.FULL_EXTENSION, 0, 1, -6, 6) - C;
        sigmoid = 1 / (1 + Math.exp(-sigmoid));
        double power;
        power = K * Math.sqrt(Math.abs(error * (1 - sigmoid))) * Math.signum(error);

        double feedforward = Math.signum(targetPosition - currentPos) * F;

        if (targetPosition == 0) feedforward = 0;

        hslides.setPower(power + feedforward + correction);

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("power",power);
        telemetry.addData("vel",vel);
        telemetry.addData("power",hslides.motor.getPower());
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.update();
    }

    @Override
    public void initialize(RevAmpedOpMode op) {
        hslides = new HwSlide(op.hardwareMap, "hslides", Constants.HSLIDE_DIRECTION);
        hslides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hslides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}
