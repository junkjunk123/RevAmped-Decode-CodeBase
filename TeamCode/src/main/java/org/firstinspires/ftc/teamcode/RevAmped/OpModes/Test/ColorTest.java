package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
@TeleOp
public class ColorTest extends DecodeRevAmpedOpMode {
    private IntakeColor leftColor;
    private IntakeColor rightColor;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        //leftColor = new IntakeColor(hardwareMap,"color_left",0d);
        rightColor = new IntakeColor(hardwareMap,"color_right",0d);
    }

    @Override
    public void RevAmpedLoop() {
        //telemetry.addData("Left",leftColor.getColor());
        //telemetry.addData("Left",leftColor.getHSVValues());
        telemetry.addData("color_right",rightColor.getColor());
        telemetry.addData("color_right",rightColor.getHue());
        telemetry.addData("Distance",rightColor.getDistanceMM());
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
