package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.VslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.Hardware.HwSlide;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;
import revAmped.util.Range;

@Config
@TeleOp(name = "VslideTuner", group = "Test")
public class VslideTuner extends RevAmpedTestOpMode {
    private HwSlide vslides;
    private HwSlide vslides2;
    public static double K = 1.2f, F = 0.05f, D =  0.00195, C = 1.5, S = 0.5;
    public static int targetPosition = VslideMotor.VSLIDE_HIGH_BASKET_DEPO;
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
        vslides.update();
        vslides2.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

    @Override
    public void RevAmpedLoop() {
        int currentPos = vslides.getCurrentPosition();

        int error = targetPosition - currentPos;
        double seconds = timer.seconds();
        timer.reset();
        double deltaError = (double) (error - prevError)/seconds;
        prevError = error;

        double correction = deltaError * D;

        double sigmoid = Range.scale(1 - (double) Math.abs(error) / Constants.VSLIDE_HIGH_BASKET_DEPO, 0, 1, -6, 6) - C;
        sigmoid = 1 / (1 + Math.exp(-sigmoid));
        double power;
        power = K * Math.sqrt(Math.abs(error * (1 - sigmoid))) * Math.signum(error);

        double feedforward = Math.signum(targetPosition - currentPos) * F;

        if (targetPosition == 0) feedforward = 0;

        vslides.setPower(power + feedforward + correction);
        vslides2.setPower(power + feedforward + correction);

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("power",power);
        telemetry.addData("vel",vel);
        telemetry.addData("power",vslides.motor.getPower());
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.update();
    }

    @Override
    public void initialize(RevAmpedOpMode op) {
        vslides = new HwSlide(op.hardwareMap, "vslides", Constants.VSLIDE_DIRECTION);
        vslides2 = new HwSlide(op.hardwareMap, "vslides2", Constants.VSLIDE2_DIRECTION);
        vslides.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        vslides.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vslides2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        vslides2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}
