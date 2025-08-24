package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import revAmped.util.LimeLightStream;

@TeleOp(name = "LimelightDashboard", group = "Test")
public class LimelightDashboard extends LinearOpMode {
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.start();
        waitForStart();

        LimeLightStream stream = new LimeLightStream();
        FtcDashboard.getInstance().startCameraStream(stream,30);

        while(opModeIsActive()){
            idle();
        }
    }
}