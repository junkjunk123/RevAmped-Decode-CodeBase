package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import revAmped.components.Hardware.HwSlide;
import revAmped.util.Range;

@Config
@TeleOp(name = "ControllerTunerVslides", group = "Test")
public class ControllerTunerVslides extends OpMode {
    private HwSlide left;
    private HwSlide right;
    public static double K = 0.65f, P = 0.045f, A = 3, F = 0.05f, D =  0.001, C = 2, S = 0.05;
    public static int targetPosition = 820;
    private ElapsedTime timer = new ElapsedTime();
    private double vel = 0;
    public static boolean tanh = false;
    private int prevError = 0;

    @Override
    public void init() {
        left = new HwSlide(hardwareMap, "vslideMotorLeft", DcMotorSimple.Direction.REVERSE);
        right = new HwSlide(hardwareMap, "vslideMotorRight", DcMotorSimple.Direction.FORWARD);
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void loop() {
        left.update();
        right.update();

        int currentPos = left.getCurrentPosition();
        int error = targetPosition - currentPos;
        double seconds = timer.seconds();
        timer.reset();
        double deltaError = (double) (error - prevError)/seconds;
        prevError = error;

        double K = ControllerTunerVslides.K + (- 4.5 + 4.5 * (double) targetPosition / ITDConstants.VSLIDE_HIGH_BASKET) * S;
        double correction = deltaError * D;

        double sigmoid = Range.scale(1 - (double) Math.abs(error) / targetPosition, 0, 1, -6, 6) - C;
        sigmoid = 1 / (1 + Math.exp(-sigmoid));
        double power;
        if (tanh) power = K * Math.sqrt(Math.abs(error * (1 - sigmoid) + sigmoid * P * Math.tanh(A * (error) / ITDConstants.VSLIDE_HIGH_BASKET))) * Math.signum(error);
        else power = K * Math.sqrt(Math.abs(error * (1 - sigmoid))) * Math.signum(error);

        double feedforward = Math.signum(targetPosition - currentPos) * F;

        if (targetPosition == 0) feedforward = 0;

        left.setPower(power + feedforward + correction);
        right.setPower(power + feedforward + correction);

        telemetry.addData("slidePos",currentPos);
        telemetry.addData("target",targetPosition);
        telemetry.addData("power",power);
        telemetry.addData("vel",vel);
        telemetry.addData("powerLeft",left.motor.getPower());
        telemetry.addData("powerRight",right.motor.getPower());
        telemetry.addData("error",targetPosition - currentPos);
        telemetry.update();
    }
}
