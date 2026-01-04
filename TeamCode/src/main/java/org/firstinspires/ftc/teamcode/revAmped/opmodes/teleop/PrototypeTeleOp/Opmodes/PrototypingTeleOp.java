package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.Octocanum;

import java.util.Arrays;
/*
Eric Zhao
10/20/2025
Prototyping Teleop
Used for redesign testing
 */
@TeleOp(name = "Redesign Drivetrain", group = "TeleOp")
@Config
public class PrototypingTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private Octocanum octocanum;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.REDESIGN_DRIVE_DIRECTIONS)
                .build();
        octocanum = new Octocanum(hardwareMap);
    }

    /*
    b - intake/outtake
    y - stop intake

     */

    @Override
    public void RevAmpedLoop() {
        //Drivetrain
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        if(gamepad_1.a.isPress()){
            octocanum.toggle();
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        telemetry.update();
        drivetrain.arcadeDrive();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}