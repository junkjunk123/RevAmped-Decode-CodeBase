package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Shooter;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.RevAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;

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
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) drivetrain.toggleSlowMode();

        if (gamepad_2.b.isPress()) {
            robot.turnTable.intake();
            robot.shooter.setCurrentState(Shooter.ShooterState.REST);
        }

        if (gamepad_2.left_bumper.isPress()) {
            if (robot.intakeMotor().getCurrentState() == IntakeMotor.IntakeState.INTAKE) {
                robot.intakeMotor().setCurrentState(IntakeMotor.IntakeState.OUTTAKE);
            } else {
                robot.intakeMotor().setCurrentState(IntakeMotor.IntakeState.INTAKE);
            }
        }

        if (gamepad_2.right_bumper.isPress()) {
            robot.intakeMotor().stop();
        }

        if (gamepad_2.y.isPress()) {
            robot.shooter.setCurrentState(Shooter.ShooterState.REST);
            robot.turnTable.pop();
            scheduler.add(new Delay(500, () -> robot.shooter.setCurrentState(Shooter.ShooterState.TRACKING)));
        }

        if (gamepad_2.x.isPress()) {
            if (robot.flywheel().getCurrentState() == Flywheel.FlywheelState.STOPPED) {
                robot.shooter.shoot();
            } else {
                robot.flywheel().stop();
            }
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
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        drivetrain.updateLocalization();
        drivetrain.arcadeDrive();
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}
