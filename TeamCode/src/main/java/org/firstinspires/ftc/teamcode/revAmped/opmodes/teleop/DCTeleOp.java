package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Disabled
@TeleOp(name = "DCTeleOpOld", group = "TeleOp")
public class DCTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private Prompter prompter;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        robot = new Robot (
                this,
                Globals.allianceColor,
                () -> drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build()
        );
        prompter = new Prompter(this, gamepad_1)
                .prompt("motif", new OptionPrompt<>("Select motif", Globals.RandomizationState.values()));
    }

    @Override
    public void RevAmpedInitLoop() {
        prompter.run();
    }

    @Override
    public void RevAmpedStart() {
        Globals.randomizationState = prompter.getOrDefault("motif", Globals.randomizationState);
        robot.teleopInitialize();
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) {
            robot.turretMotor().setCurrentState(Types.TurretState.HOLD);
        }

        if (gamepad_1.b.isPress()) {
            robot.turnTable.populateAndShoot();
        }

        if (gamepad_2.b.isPress()) {
            DecodeColorSensor.enableSensors();
            robot.turnTable.intake();
        }
        if (gamepad_2.right_trigger != 0){
            robot.intakeMotor().setCurrentState(Types.IntakeState.OUTTAKE);
        }

        if (gamepad_2.right_bumper.isPress()) {
            if (robot.turnTable.getCurrentState().getCycleState() == TurnTableInternals.TurnTableCycleState.INTAKE)
                robot.turnTable.manualTransfer();
            else
                robot.turnTable.incrementPopIndex();
        }

        if (gamepad_2.left_bumper.isPress()) {
            robot.turnTable.decrementPopIndex();
        }

        if (gamepad_2.a.isPress()) {
            robot.intakeMotor().setCurrentState(Types.IntakeState.STOP);
        }

        if (gamepad_2.x.isPress()) {
            robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
            robot.turnTable.switchStateToPop();
        }

        if (gamepad_2.y.isPress()) {
            //robot.shootAll();
        }

        if (gamepad_1.x.isPress()) {
            robot.turnTable.toggleMotifMode();
            gamepad_1.rumble(500);
            gamepad_2.rumble(500);
        }

        if (gamepad_2.dpad_right.isPress()) {
            robot.turnTable.prepareGreen();
        }

        if (gamepad_1.dpad_right.isPress()) {
            robot.shooter.toggleTurretTracking();
        }

        if (gamepad_2.dpad_left.isPress()) {
            robot.turnTable.preparePurple();
        }

        if (gamepad_2.dpad_up.isPress()) {
            //robot.turnTable.launch();
            robot.updateLock();
        }

        if (gamepad_1.y.isPress()) {
            robot.shooter.toggleState();
        }

        if (gamepad_1.dpad_up.isPress()) {
            robot.hoodServo().setCurrentState(Types.HoodState.FAR_PRESET);
            robot.flywheel().run(Flywheel.FLYWHEEL_FAR_VELOCITY);
        }

        if (gamepad_1.dpad_down.isPress()) {
            robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
            robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
        }

        if (gamepad_1.dpad_left.isPress()){
            robot.hoodServo().setCurrentState(Types.HoodState.MED_PRESET);
            robot.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
        }

        if (gamepad_1.left_trigger > 0.3) {
            robot.turretMotor().fineTuneTicks((int) (-gamepad_1.left_trigger * 20));
        }

        if (gamepad_1.right_trigger > 0.3) {
            robot.turretMotor().fineTuneTicks((int) (gamepad_1.right_trigger * 20));
        }

        if (gamepad_1.right_bumper.isPress()){
            robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS);
        }

        if (gamepad_1.left_bumper.isPress()){
            robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.TURRET_POSITIVE_LIMIT_TICKS);
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        if (robot.shooter.getCurrentState() == Types.ShooterState.TRACKING) drivetrain.updateLocalization();
        robot.update();
        drivetrain.arcadeDrive();
        //telemetry.update();
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
