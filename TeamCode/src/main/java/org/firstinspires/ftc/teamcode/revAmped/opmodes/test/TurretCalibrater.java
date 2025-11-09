package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp
public class TurretCalibrater extends DecodeRevAmpedOpMode {
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private TeleOpDrivetrainHandler drivetrain;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.None);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void RevAmpedStart() {
        robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
        robot.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("turretPos", robot.turretMotor().getCurrentPosition());
        telemetry.addData("turretState", robot.turretMotor().getCurrentState());
        telemetry.addData("turretSwitch", robot.turretMotor().limitSwitch.isTouch());

        if (gamepad_1.a.isPress()) {
            robot.shooter.toggleTurretTracking();
        }

        if (gamepad_1.b.isPress()) {
            robot.turretMotor().setCurrentState(Types.TurretState.IDLE);
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        if (robot.shooter.getCurrentState() == Types.ShooterState.TRACKING)
            drivetrain.updateLocalization();
        robot.update();
        drivetrain.arcadeDrive();
        telemetry.addData("turretPos", robot.turretMotor().getCurrentPosition());
        telemetry.addData("limSwitch", robot.turretMotor().limitSwitch.isTouch());
        telemetry.addData("turretRawPos", robot.turretMotor().motor.getCurrentPosition());
        telemetry.addData("robotPos", drivetrain.getPose());
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
