package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.LoopTimer;

@Config
@TeleOp
public class TurntablePIDTuner extends DecodeRevAmpedOpMode {
    private CRServoImplEx motor;
    private DcMotorEx intake;
    private int initialPos;
    private PIDFController controller;
    public static double P = 0.000244;
    public static double I;
    public static double D = 0.00001;
    public static double F = 0.025;
    public static int targetPosition = TableServo.BALL2;
    public static double targetLoopTimeMillis = 20;
    private LoopTimer loopTimer = new LoopTimer(10000000);
    private double prevSleep;

    @Override
    public void RevAmpedInit() {
        motor = hardwareMap.get(CRServoImplEx.class, "table");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        initialPos = intake.getCurrentPosition();
        controller = new PIDFController(new PIDFCoefficients(P, I, D, F));
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        motor.setDirection(TableServo.direction);
    }

    @Override
    public void RevAmpedInitLoop() {
        controller.updateConstants(new PIDFCoefficients(P, I, D, F));
        telemetry.addData("pos", getPos());
        telemetry.addData("error", getError());
        telemetry.update();
    }

    @Override
    public void RevAmpedStart() {
        loopTimer.begin();
    }

    private int getPos() {
        return -intake.getCurrentPosition() - initialPos;
    }

    private int getError() {
        if (TableServo.TICKS_PER_ROTATION == 0) return 0;
        return (int) HwCRServo2.getErrorInfiniteRotation(targetPosition, getPos(), TableServo.TICKS_PER_ROTATION);
    }

    @Override
    public void RevAmpedLoop() {
        controller.updateConstants(new PIDFCoefficients(P, I, D, F));

        int error = getError();
        double power = Range.clip(controller.runWithError(error), -1, 1);

        try {
            motor.setPower(power);
        } catch (Exception e) {
            telemetry.addData("power", power);
        }

        telemetry.addData("currentPos", getPos());
        telemetry.addData("error", error);
        telemetry.addData("power", power);

        loopTimer.update();
        prevSleep += targetLoopTimeMillis - loopTimer.getInterval();
        if (prevSleep >= 0)
            sleep((long) (prevSleep));
    }

    @Override
    public void update() {
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
