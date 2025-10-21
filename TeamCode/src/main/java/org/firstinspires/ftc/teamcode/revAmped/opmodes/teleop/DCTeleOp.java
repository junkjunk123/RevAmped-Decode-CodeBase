package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.Arrays;

@TeleOp(name = "DCTeleOp", group = "TeleOp")
public class DCTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    //private HoodServo hoodServo;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;

    @Override
    public void RevAmpedInit() {
        //hoodServo = new HoodServo(hardwareMap,true);
        robot = Robot.getAutonomousInstance(this, Globals.allianceColor);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .invertControls()
                .build();
    }

    @Override
    public void RevAmpedStart() {
        robot.teleopInitialize();
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("color",robot.intakeColor().getColor());
        telemetry.addData("balls", Arrays.toString(robot.turnTable.getBallColors()));
        telemetry.addData("turretPos", robot.turretMotor().getCurrentPosition());
        telemetry.addData("Color",robot.intakeColor().getColor());

        if (gamepad_1.a.isPress()) drivetrain.toggleSlowMode();

        if (gamepad_2.b.isPress()) {
            DecodeColorSensor.enableSensors();
            robot.turnTable.intake();
            //robot.shooter.setCurrentState(Types.ShooterState.REST);
        }
        if (gamepad_2.right_trigger != 0){
            robot.intakeMotor().setCurrentState(Types.IntakeState.OUTTAKE);
        }

        if (gamepad_2.right_bumper.isPress()) {
            robot.intakeMotor().stop();
        }

        if (gamepad_2.a.isPress()) {
            robot.turnTable.manualTransfer();
        }

        if (gamepad_2.x.isPress()) {
            robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
            robot.turnTable.switchStateToPop();
        }

        if (gamepad_2.y.isPress()) {
            robot.shootAll();
        }

        if (gamepad_1.b.isPress()) {
            drivetrain.toggleFieldCentric();
        }

        if (gamepad_1.x.isPress()) {
            robot.turnTable.toggleMotifMode();
            gamepad_1.rumble(500);
            gamepad_2.rumble(500);
        }

        if (gamepad_2.dpad_right.isPress()) {
            robot.turnTable.prepareGreen();
        }

        if (gamepad_2.dpad_left.isPress()) {
            robot.turnTable.preparePurple();
        }

        if (gamepad_2.dpad_up.isPress()) {
            robot.turnTable.pop();
        }

        if (gamepad_1.y.isPress()) {
            robot.shooter.toggleState();
        }

        if (gamepad_1.dpad_up.isPress()) {
            try {
                robot.shooter.toggleHoodTracking();
            } catch (Exception e) {
                throw new UnsupportedOperationException("Hood tracking has not been implemented yet");
            }

            //Erics hood
            //hoodServo.setCurrentState((hoodServo.getCurrentState() == HoodServo.HoodState.CLOSE) ? HoodServo.HoodState.FAR : HoodServo.HoodState.CLOSE);
            //robot.flywheel().run((hoodServo.getCurrentState() == HoodServo.HoodState.CLOSE) ? Flywheel.FLYWHEEL_NEAR_VELOCITY : Flywheel.FLYWHEEL_FAR_VELOCITY);
        }

        if (gamepad_1.dpad_down.isPress()) {
            robot.shooter.toggleTurretTracking();
        }

        if (gamepad_2.left_stick_x > 0.3) {
            robot.tableServo().finetune((int) (gamepad_2.left_stick_x * 35));
        }

        if (gamepad_2.right_stick_button.isPress()) {
            robot.tableServo().resetPosition();
        }

        if (gamepad_1.left_trigger > 0.3) {
            robot.turretMotor().fineTuneTicks((int) (-gamepad_1.left_trigger * 20));
        }

        if (gamepad_1.right_trigger > 0.3) {
            robot.turretMotor().fineTuneTicks((int) (gamepad_1.right_trigger * 20));
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        if (robot.shooter.getCurrentState() == Types.ShooterState.TRACKING) drivetrain.updateLocalization();
        robot.update();
        drivetrain.arcadeDrive();
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
