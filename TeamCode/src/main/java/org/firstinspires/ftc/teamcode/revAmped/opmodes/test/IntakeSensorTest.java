package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorOne;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorTwo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
public class IntakeSensorTest extends DecodeRevAmpedOpMode {
    private ColorOne colorOne;
    private ColorTwo colorTwo;
    private IntakeUltrasonic intakeUltrasonic;
    private IntakeMotor intakeMotor;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        colorOne = new ColorOne(hardwareMap);
        colorTwo = new ColorTwo(hardwareMap);
        intakeUltrasonic = new IntakeUltrasonic(hardwareMap);
        intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
        intakeMotor.setVoltageCompensation();
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.bWasPressed()) intakeMotor.setPower(1);
        if (gamepad1.aWasPressed()) intakeMotor.setPower(0);

        telemetry.addData("compartmentTwo", colorOne.getColor());
        telemetry.addData("compartmentTwoHue", colorOne.getHue());
        telemetry.addData("compartmentTwoDistance", colorOne.getDistanceMM());
        telemetry.addData("compartmentThree", colorTwo.getColor());
        telemetry.addData("compartmentThreeHue", colorTwo.getHue());
        telemetry.addData("compartmentThreeDistance", colorTwo.getDistanceMM());
        telemetry.addData("compartmentOneHasSample", intakeUltrasonic.hasArtifact());
        telemetry.addData("compartmentOneVoltage (Ultrasonic)", intakeUltrasonic.getDistance());
    }

    @Override
    public void update() {
        telemetry.update();
        colorOne.update();
        colorTwo.update();
        intakeUltrasonic.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
