package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
public class ColorTest extends DecodeRevAmpedOpMode {
    private IntakeColor leftColor;
    private IntakeColor rightColor;
    private IntakeMotor intakeMotor;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        //leftColor = new IntakeColor(hardwareMap,"color_left",0d);
        rightColor = new IntakeColor(hardwareMap,"color_right",0d);
        intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP);
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.bWasPressed()) intakeMotor.setPower(1);
        if (gamepad1.aWasPressed()) intakeMotor.setPower(0);

        //telemetry.addData("Left",leftColor.getColor());
        //telemetry.addData("Left",leftColor.getHSVValues());
        telemetry.addData("color_right",rightColor.getColor());
        telemetry.addData("color_right",rightColor.getHSVValues());
        telemetry.addData("Distance",rightColor.getDistanceMM());
        telemetry.addData("Intake Power",intakeMotor.getPower());
        telemetry.addData("Intake Current",intakeMotor.getCurrentAmps());
        telemetry.addData("TurntablePosition", intakeMotor.getCurrentPosition());
    }

    @Override
    public void update() {
        telemetry.update();
        //leftColor.update();
        rightColor.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
