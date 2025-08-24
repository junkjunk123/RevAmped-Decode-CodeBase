package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;
import revAmped.util.LimeLightStream;

@TeleOp(name = "LimelightDashboard", group = "Test")
public class LimelightDashboard extends RevAmpedOpMode {
    private Limelight3A limelight;

    @Override
    public void RevAmpedInit() {
        initialize();
    }

    @Override
    public void RevAmpedStart() {
        LimeLightStream stream = new LimeLightStream();
        FtcDashboard.getInstance().startCameraStream(stream,30);
    }

    @Override
    public void RevAmpedLoop() {

    }

    @Override
    public void update() {

    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

    public void initialize() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
    }
}