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
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp(name = "DCTeleOp", group = "TeleOp")
public class DCTeleOpV2 extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private Prompter prompter;
    private TeleOpStateHandler<CycleState> tsh;

    public enum CycleState implements TeleOpStateHandler.State {
        INTAKE,
        DRIVING_TO_SHOOTING,
        SHOOTING;

        @Override
        public FiniteFieldVector<FiniteFieldVector.BinaryVector> getTransitionVector() {
            switch (this) {
                case INTAKE -> {
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
                .prompt("alliance", new OptionPrompt<>("Select alliance color", Globals.AllianceColor.values()))
                .prompt("motif", new OptionPrompt<>("Select motif", Globals.RandomizationState.values()));

        tsh = new TeleOpStateHandler<>(CycleState.INTAKE, scheduler);

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
        Globals.allianceColor = prompter.getOrDefault("alliance", Globals.allianceColor);
        robot.teleopInitialize();
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) {
            tsh.task(() -> robot.shooter.toggleTurretTracking(), FiniteFieldVector.binaryVectorOf(0, 1, 1));
        }

        if (gamepad_2.b.isPress()) {
            if (tsh.currentState() == CycleState.DRIVING_TO_SHOOTING || robot.intakeMotor().getCurrentState() != Types.IntakeState.INTAKE) {
                tsh.runTransition(t -> {
                            if (robot.turnTable.getCurrentCycleState() == TurnTableInternals.TurnTableCycleState.INTAKE)
                                robot.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
                            robot.turnTable.intake();
                            DecodeColorSensor.enableSensors();
                            t.accept(true);
                        }, CycleState.INTAKE
                );
            }
        }

        if (gamepad_2.dpad_right.isPress()) {
            robot.intakeMotor().setCurrentState(Types.IntakeState.OUTTAKE);
        }

        if (gamepad_1.y.isPress()) {
            drivetrain.toggleZeroPowerBehavior();
        }

        if (gamepad_2.customButtons.get(0).isPress()) {
            tsh.task(() -> {
                if (robot.intakeMotor().getCurrentState() == Types.IntakeState.INTAKE)
                    robot.intakeMotor().setCurrentState(Types.IntakeState.OUTTAKE);
                else robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
            }, FiniteFieldVector.binaryVectorOf(1, 0, 0));
        }

        if (gamepad_2.right_bumper.isPress()) {
            tsh.task(robot.turnTable::incrementPopIndex, FiniteFieldVector.binaryVectorOf(1,0,0));
        }

        if (gamepad_2.left_bumper.isPress()) {
            tsh.task(robot.turnTable::decrementPopIndex, FiniteFieldVector.binaryVectorOf(1,0,0));
        }

        if (gamepad_2.a.isPress()) {
            robot.intakeMotor().setCurrentState(Types.IntakeState.STOP);
        }

        if (gamepad_2.x.isPress()) {
            if (tsh.currentState() == CycleState.INTAKE && robot.popper().getCurrentState() == Types.PopperState.NEUTRAL) {
                robot.turnTable.updateColors();
                scheduler.add(new Delay(20, () -> tsh.runTransition(t -> {
                    robot.turnTable.switchStateToPop();
                    robot.intakeMotor().stop();
                    robot.shooter.setCurrentState(Types.ShooterState.TRACKING);
                    scheduler.add(new Delay(() -> robot.popper().getCurrentState() == Types.PopperState.POP,
                            () -> scheduler.add(new Delay(300, () -> t.accept(true)))));
                }, CycleState.DRIVING_TO_SHOOTING)));
            }
        }

        if (gamepad_2.dpad_up.isPress()) {
            drivetrain.startHoldPose();
            robot.turnTable.launch(0, tsh, drivetrain::stopHoldPose);
        }

        if (gamepad_2.dpad_down.isPress()) {
            drivetrain.startHoldPose();
            robot.turnTable.launch(250, tsh, drivetrain::stopHoldPose);
        }

        if (gamepad_1.dpad_up.isPress()) {
            tsh.setting(() -> {
                robot.hoodServo().setCurrentState(Types.HoodState.FAR_PRESET);
                robot.flywheel().run(Flywheel.FLYWHEEL_FAR_VELOCITY);
            });
        }

        if (gamepad_1.dpad_down.isPress()) {
            tsh.setting(() -> {
                robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
                robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
            });
        }

        if (gamepad_1.dpad_left.isPress()) {
            tsh.setting(() -> {
                robot.hoodServo().setCurrentState(Types.HoodState.MED_PRESET);
                robot.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
            });
        }

        if (gamepad_1.dpad_right.isPress()) {
            drivetrain.toggleHoldPose();
        }

        if (gamepad_1.left_trigger > 0.3) {
            if (tsh.nextState() != CycleState.INTAKE)
                tsh.setting(() -> robot.turretMotor().fineTuneTicks((int) (-gamepad_1.left_trigger * 20)));
        }

        if (gamepad_1.right_trigger > 0.3) {
            if (tsh.nextState() != CycleState.INTAKE)
                tsh.setting(() -> robot.turretMotor().fineTuneTicks((int) (gamepad_1.right_trigger * 20)));
        }

        if (gamepad_1.right_bumper.isPress()) {
            if (robot.hoodServo().getCurrentState() == Types.HoodState.FAR_PRESET){
                if (robot.getAllianceColor() == Globals.AllianceColor.Blue) {
                    robot.turretMotor().toggleFarPresetBlue();
                } else if (robot.getAllianceColor() == Globals.AllianceColor.Red) {
                    robot.turretMotor().toggleFarPresetRed();
                }
            }else {
                robot.turretMotor().nextPresetState();
            }
        }

        if (gamepad_1.left_bumper.isPress()) {
            if (robot.hoodServo().getCurrentState() == Types.HoodState.FAR_PRESET) {
                if (robot.getAllianceColor() == Globals.AllianceColor.Blue) {
                    robot.turretMotor().toggleFarPresetBlue();
                } else if (robot.getAllianceColor() == Globals.AllianceColor.Red) {
                    robot.turretMotor().toggleFarPresetRed();
                }
            }else {
                robot.turretMotor().previousPresetState();
            }
        }

        if (gamepad_1.b.isPress()) {
            scheduler.add(new Delay(robot.turretMotor().limitSwitch::isTouch, robot.turretMotor()::resetPosition));
        }

        if (gamepad_2.y.isPress()) {
            robot.turnTable.compartments.sortForMotifs();
        }

        if (gamepad_1.x.isPress()) {
            tsh.abortTransition();
            robot.popper().setCurrentState(Types.PopperState.NEUTRAL);
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        if (robot.shooter.getCurrentState() == Types.ShooterState.TRACKING)
            drivetrain.updateLocalization();
        robot.update();
        drivetrain.arcadeDrive();
        telemetry.addData("pose",drivetrain.getPose());

        telemetry.update();
    }

    @Override
    public void RevAmpedStop() {
        super.RevAmpedStop();
        OpModeCommunication.turretPos = 0;
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
