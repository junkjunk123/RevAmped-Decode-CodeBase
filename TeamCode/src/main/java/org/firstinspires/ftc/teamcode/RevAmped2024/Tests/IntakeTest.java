package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.TrapdoorServo;

import java.util.ArrayList;
import java.util.List;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwMotor;
import revAmped.components.MathUtil.Data;
import revAmped.components.MathUtil.MovingAverage;
import revAmped.components.Misc.RevAmpedOpMode;

@Config
@TeleOp(name = "IntakeTest", group = "Test")
public class IntakeTest extends OpMode {
    private HwMotor intakeMotor;
    public static double power = 1f;
    public static float threshold = 2.3f;
    private IntakeFourbarArmServo armServo;
    private IntakeFourbarTiltServo tiltServo;
    private Scheduler scheduler = new Scheduler();
    private TrapdoorServo trapdoor;
    private boolean stop = false;
    private double nominalAmperage = 0;
    private boolean spiked = false;
    private double maxPeak = 0;
    public static double delay = 400;
    public static double normalThreshold = 1.85;
    private double finalVoltage = 0;
    public static double finalThresholdAddend = 0.25;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        intakeMotor = new HwMotor(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE);
        armServo = new IntakeFourbarArmServo(hardwareMap, "intakeArm", true, IntakeFourbarArmServo.IntakeArmState.INTAKE,
                new HwAnalogEncoder(hardwareMap, "intakeArmVoltage"));
        tiltServo = new IntakeFourbarTiltServo(hardwareMap, "intakeTilt",true, IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
        trapdoor = new TrapdoorServo(hardwareMap, "intakeTrapdoor", true, TrapdoorServo.TrapdoorState.CLOSE, scheduler.delays);
    }

    public void start() {
        scheduler.add(new Delay(() -> intakeMotor.getCurrentAmps() > threshold,
                () -> {scheduler.add(new Delay(() -> intakeMotor.getCurrentAmps() < normalThreshold,
                        () -> {spiked = true; scheduler.add(new Delay(() -> intakeMotor.getCurrentAmps() > threshold,
                                () -> {nominalAmperage = intakeMotor.getCurrentAmps(); scheduler.add(new Delay(delay,
                                        () -> {finalVoltage = intakeMotor.getCurrentAmps(); if (intakeMotor.getCurrentAmps() > normalThreshold + finalThresholdAddend) {armServo.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER); scheduler.add(new Delay(200,
                                                () -> tiltServo.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER)));
                                                        stop = true;}}));}));}));}));

        scheduler.add(new Delay(() -> power != 0, () -> nominalAmperage = intakeMotor.motor.getCurrent(CurrentUnit.AMPS)));
        intakeMotor.setPower(1f);
    }

    @Override
    public void loop() {
        scheduler.update();
        double amps = intakeMotor.getCurrentAmps();

        if (!stop) {
            intakeMotor.setPower(power);
        } else {
            intakeMotor.setPower(0f);
        }

        if (spiked && amps > maxPeak) {
            maxPeak = amps;
        }

        armServo.update();
        tiltServo.update();
        trapdoor.update();
        intakeMotor.update();
        telemetry.addData("Intake Current", intakeMotor.getCurrentAmps());
        telemetry.addData("nominalAmperage", nominalAmperage);
        telemetry.addData("maxPeak",maxPeak);
        telemetry.addData("finalVoltage",finalVoltage);
        telemetry.update();
    }
}
