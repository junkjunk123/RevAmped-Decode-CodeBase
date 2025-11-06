package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.architecture.TeleOpStateHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.math.FiniteFieldVector;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.Collections;
import java.util.List;

@TeleOp(name = "DCTeleOpRedesign", group = "TeleOp")
public class DCTeleOpV2 extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private Prompter prompter;
    private TeleOpStateHandler<CycleState> tsh;

    public enum CycleState implements TeleOpStateHandler.State {
        INTAKING,
        DRIVING_TO_SHOOTING,
        SHOOTING;

        @Override
        public FiniteFieldVector<FiniteFieldVector.BinaryVector> getTransitionVector() {
            switch (this) {
                case INTAKING -> {
                    return FiniteFieldVector.binaryVectorOf(1, 1, 0);
                }
                case SHOOTING -> {
                    return FiniteFieldVector.binaryVectorOf(1, 0, 1);
                }
                default -> {
                    return FiniteFieldVector.binaryVectorOf(1, 1, 1);
                }
            }
        }
    }

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.allianceColor);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
        prompter = new Prompter(this, gamepad_1)
                .prompt("motif", new OptionPrompt<>("Select motif", Globals.RandomizationState.values()));
        tsh = new TeleOpStateHandler<>(CycleState.INTAKING, scheduler);

        new RevAmpedGamepad.CustomButtonCreator("right_trigger_button", () -> gamepad_2.right_trigger)
                .greaterThan(0.3f)
                .build(gamepad_2);
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
            tsh.task(() -> {
                if (robot.turretMotor().getCurrentState() == Types.TurretState.TRACKING)
                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD);
                else robot.turretMotor().setCurrentState(Types.TurretState.TRACKING);
            }, FiniteFieldVector.binaryVectorOf(0, 1, 1));
        }

        if (gamepad_1.b.isPress()) {
            tsh.task(robot.turnTable::populateAndShoot, FiniteFieldVector.binaryVectorOf(1, 0, 0));
        }

        if (gamepad_2.b.isPress()) {
            tsh.runTransition(t -> {
                robot.turnTable.intake();
                DecodeColorSensor.enableSensors();
                }, CycleState.INTAKING
            );
        }

        if (gamepad_2.customButtons.get(0).isPress()) {
            tsh.task(() -> {
                if (robot.intakeMotor().getCurrentState() == Types.IntakeState.INTAKE) robot.intakeMotor().setCurrentState(Types.IntakeState.OUTTAKE);
                else robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
            }, FiniteFieldVector.binaryVectorOf(1, 0, 0));
        }

        if (gamepad_2.right_bumper.isPress()) {
            if (robot.turnTable.getCurrentState().getCycleState() == TurnTableInternals.TurnTableCycleState.INTAKE)
                tsh.task(robot.turnTable::manualTransfer, CycleState.INTAKING);
            else tsh.task(robot.turnTable::incrementPopIndex, FiniteFieldVector.binaryVectorOf(0, 1, 1));
        }

        if (gamepad_2.left_bumper.isPress()) {
            tsh.task(robot.turnTable::decrementPopIndex, FiniteFieldVector.binaryVectorOf(0, 1, 1));
        }

        if (gamepad_2.a.isPress()) {
            //no tsh needed
            robot.intakeMotor().setCurrentState(Types.IntakeState.STOP);
        }

        if (gamepad_2.x.isPress()) {
            tsh.runTransition(t -> {
                robot.shooter.turntableAutomationControls(Types.ShooterState.TRACKING);
                robot.turnTable.switchStateToPop();
            }, CycleState.DRIVING_TO_SHOOTING);
        }

        if (gamepad_2.y.isPress()) {
            //robot.shootAll();
        }

        if (gamepad_1.x.isPress()) {
            robot.turnTable.toggleMotifMode();
            gamepad_1.rumble(500);
            gamepad_2.rumble(500);
        }

        if (gamepad_1.dpad_right.isPress()) {
            robot.shooter.toggleTurretTracking();
        }

        if (gamepad_2.dpad_up.isPress()) {
            robot.turnTable.launch(true, true, tsh);
        }

        if (gamepad_2.dpad_down.isPress()) {
            robot.turnTable.launch(false, true, tsh);
        }

        if (gamepad_2.dpad_right.isPress()) {
            robot.turnTable.launch(false, true, tsh);
        }

        if (gamepad_2.dpad_left.isPress()) {
            robot.turnTable.launch(false, false, tsh);
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

        if (gamepad_2.left_stick_x > 0.3) {
            Scheduler.getInstance().add(new Delay(() -> tsh.currentElement() instanceof CycleState,
                    () -> robot.tableServo().finetune((int) (gamepad_2.left_stick_x * 35))));
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

        if (gamepad_1.right_bumper.isPress()) {
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
