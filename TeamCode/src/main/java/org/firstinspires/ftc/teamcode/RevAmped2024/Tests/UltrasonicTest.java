package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.MathUtil.MovingAverage;
import revAmped.components.Misc.RevAmpedOpMode;

@Config
@TeleOp(name = "UltrasonicTester", group = "Test")
@Disabled
public class UltrasonicTest extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this);
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("distance", robot.ultrasonic.getDistance());
        telemetry.update();
    }

    @Override
    public void update() {
        robot.update();
    }

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }
}
