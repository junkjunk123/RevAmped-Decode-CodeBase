package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
public class ColorTest extends DecodeRevAmpedOpMode {
    private IntakeColor leftColor;
    private IntakeColor rightColor;
    private DualIntakeColor dualColor;
    private IntakeMotor intakeMotor;

    public static double offset = 10;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        dualColor = new DualIntakeColor(hardwareMap);
        dualColor.setOffset(offset);
        leftColor = new IntakeColor(hardwareMap,"color_left", 0d);
        rightColor = new IntakeColor(hardwareMap,"color_right",0d);
        intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
        intakeMotor.setVoltageCompensation();
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.bWasPressed()) intakeMotor.setPower(1);
        if (gamepad1.aWasPressed()) intakeMotor.setPower(0);
        if (gamepad1.leftBumperWasPressed()){
            dualColor.disable();
            dualColor.enableLeft();
        }
        if (gamepad1.rightBumperWasPressed()){
            dualColor.disable();
            dualColor.enableRight();
        }
        if (gamepad1.yWasPressed()){
            dualColor.enableRight();
            dualColor.enableLeft();
        }

        //leftColor.setOffset(offset);
        telemetry.addData("color_right Hue",rightColor.getHue());
        telemetry.addData("Distance",rightColor.getDistanceMM());
        telemetry.addData("=======================","");
        telemetry.addData("color_left Hue",leftColor.getHue());
        telemetry.addData("Distance",leftColor.getDistanceMM());
        telemetry.addData("=======================","");
        telemetry.addData("dual color",leftColor.getColor());
        /*
        telemetry.addData("Intake Power",intakeMotor.getPower());
        telemetry.addData("Intake Current",intakeMotor.getCurrentAmps());
        telemetry.addData("TurntablePosition", intakeMotor.getCurrentPosition());
         */
    }

    @Override
    public void update() {
        telemetry.update();
        leftColor.update();
        rightColor.update();
        dualColor.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
