package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.Arrays;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

@TeleOp(name = "DCTeleOp", group = "TeleOp")
public class DCTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.allianceColor);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad_1(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
    }

    @Override
    public void RevAmpedInitLoop() {
        telemetry.addData("pos",robot.tableServo().getCurrentPosition());
        telemetry.addData("state", robot.tableServo().getCurrentState());
        telemetry.addData("balls", Arrays.toString(robot.turnTable.getBallColors()));
        telemetry.update();
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("balls", Arrays.toString(robot.turnTable.getBallColors()));
        if (gamepad_1.a.isPress()) drivetrain.toggleSlowMode();

        if (gamepad_2.b.isPress()) {
            robot.turnTable.intake();
            robot.shooter.setCurrentState(Types.ShooterState.REST);
        }

        if (gamepad_2.right_bumper.isPress()) {
            robot.intakeMotor().stop();
        }

        if (gamepad_2.a.isPress()) {
            robot.turnTable.manualTransfer();
        }

        if (gamepad_2.y.isPress()) {
            robot.turnTable.pop();
            robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
        }

        if (gamepad_1.b.isPress()) {
            drivetrain.toggleFieldCentric();
        }

        if (gamepad_1.x.isPress()) {
            robot.turnTable.toggleMotifMode();
        }

        if (gamepad_2.dpad_right.isPress()) {
            robot.turnTable.prepareGreen();
        }

        if (gamepad_2.dpad_left.isPress()) {
            robot.turnTable.preparePurple();
        }

        if (gamepad_1.y.isPress()) {
            robot.shooter.toggleState();
        }

        if (gamepad_1.dpad_up.isPress()) {
            robot.shooter.toggleHoodTracking();
        }

        if (gamepad_1.dpad_down.isPress()) {
            robot.shooter.toggleTurretTracking();
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        drivetrain.updateLocalization();
        drivetrain.arcadeDrive();
        robot.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }

    public void setGamepad_2(RevAmpedGamepad gamepad_2) {
        this.gamepad_2 = gamepad_2;
    }

    public void setGamepad_1(RevAmpedGamepad gamepad_1) {
        this.gamepad_1 = gamepad_1;
    }
}
