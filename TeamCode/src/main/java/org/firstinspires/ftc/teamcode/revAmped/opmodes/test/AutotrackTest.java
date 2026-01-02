package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Util.Drivetrain;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Shooter;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
public class AutotrackTest extends DecodeRevAmpedOpMode {
    private Robot robot;
    private TeleOpDrivetrainHandler teleOpDrivetrainHandler;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Blue);
        Shooter.trackHood = true;
        Shooter.trackTurret = true;
        teleOpDrivetrainHandler = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(new RevAmpedGamepad(gamepad1))
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
    }

    @Override
    public void RevAmpedStart() {
        robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
        robot.flywheel().motor.setMotorDisable();
        robot.flywheel().motor2.motor.setMotorDisable();
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.aWasPressed()) {
            Shooter.velocityCompensation = !Shooter.velocityCompensation;
        }

        if (gamepad1.bWasPressed()) {
            Shooter.useDrag = !Shooter.useDrag;
        }
    }

    @Override
    public void update() {
        teleOpDrivetrainHandler.updateLocalization();
        robot.update();
        teleOpDrivetrainHandler.arcadeDrive();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
