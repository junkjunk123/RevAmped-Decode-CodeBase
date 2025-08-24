package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.POCTeleOp.CycleState.BASKET_DEPO;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.POCTeleOp.CycleState.INTAKE;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.POCTeleOp.CycleState.NEUTRAL;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.POCTeleOp.CycleState.SPEC_DEPO;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.POCTeleOp.CycleState.SPEC_INTAKE;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;

import java.util.List;

import revAmped.components.Architecture.TeleOpStateHandler;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.MathUtil.FiniteFieldVector;
import revAmped.components.MathUtil.Vector;
import revAmped.components.Misc.Globals;

@TeleOp(name = "POCTeleOp", group = "OffSeason")
public class POCTeleOp extends ITDRevAmpedOpMode {
    public Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private boolean specMode = true;
    public TeleOpStateHandler<CycleState> tsh;
    private TeleOpDrivetrainHandler drivetrain;

    public enum CycleState implements TeleOpStateHandler.State {
        INTAKE(Intake.IntakeState.NEUTRAL_HIGHER, Depo.DepoState.TRANSFER, new Vector(1, 1, 0, 0, 0)),
        NEUTRAL(Intake.IntakeState.INTAKE, Depo.DepoState.TRANSFER, new Vector(1, 1, 1, 1, 0)),
        BASKET_DEPO(Intake.IntakeState.NEUTRAL_HIGHER, Depo.DepoState.BASKET_DEPO, new Vector(0, 1, 1, 0, 0)),
        SPEC_INTAKE(Intake.IntakeState.NEUTRAL_HIGHER, Depo.DepoState.SPEC_INTAKE, new Vector(1, 1, 0, 1, 1)),
        SPEC_DEPO(Intake.IntakeState.NEUTRAL_HIGHER, Depo.DepoState.SPEC_DEPO, new Vector(1, 1, 0, 1, 1));

        CycleState(Intake.IntakeState intakeState, Depo.DepoState depoState, Vector transitionVector) {
            this.intakeState = intakeState;
            this.depoState = depoState;
            this.transitionVector = new FiniteFieldVector<>(FiniteFieldVector.binaryVector(), transitionVector.values());
        }

        private final FiniteFieldVector<FiniteFieldVector.BinaryVector> transitionVector;
        private final Intake.IntakeState intakeState;
        private final Depo.DepoState depoState;

        @Override
        public List<Enum<?>> componentStates() {
            return List.of(intakeState, depoState);
        }

        @Override
        public FiniteFieldVector<FiniteFieldVector.BinaryVector> getTransitionVector() {
            return transitionVector;
        }
    }

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.None);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        tsh = new TeleOpStateHandler<>(CycleState.NEUTRAL, scheduler);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setDirections(Constants.DRIVE_DIRECTIONS)
                .setGamepad_1(gamepad_1)
                //.setLocalizer(new PinpointLocalizer(hardwareMap))
                .build();
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("State", tsh.currentState());
        telemetry.addData("Intake State", robot.intake.getCurrentState());
        telemetry.addData("Depo State", robot.depo.getCurrentState());

        drivetrain.setSlowMode(robot.intakeArm().getCurrentState() == IntakeArmServo.ArmState.NEUTRAL_LOWER && tsh.currentState() == INTAKE);
        drivetrain.arcadeDrive();

        if (gamepad_2.dpad_down.isPress()) {
            tsh.setting(() -> robot.toggleAllianceColor(), List.of(INTAKE));
        }

        if (gamepad_2.dpad_up.isPress()) {
            tsh.setting(() -> specMode = !specMode, List.of());
        }

        if (gamepad_1.right_bumper.isPress()) {
            tsh.runTransition(c -> {
                if (tsh.currentState() == SPEC_DEPO) specDepoToSpecIntake();
                robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT, HslideMotor.HALF_EXTENSION);
                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                scheduler.add(new Delay(() -> robot.hslides().closeEnough(5), () -> c.accept(true)));
            }, INTAKE, 1000);
        }

        if (gamepad_1.left_bumper.isPress()) {
            tsh.runTransition(c -> {
                if (tsh.currentState() == SPEC_DEPO) specDepoToSpecIntake();
                robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                scheduler.add(new Delay(() -> robot.hslides().closeEnough(5), () -> c.accept(true)));
            }, INTAKE,1000);
        }

        if (Math.abs(gamepad_2.left_stick_y) > 0.3f) {
            tsh.runTransition(c -> {
                robot.hslides().finetune((int) (30 * -gamepad_2.left_stick_y));
                c.accept(true);
            }, INTAKE,50);
        }

        if (Math.abs(gamepad_2.right_stick_y) > 0.3f) {
            tsh.runTransition(c -> {
                robot.vslides().finetune((int) (30 * -gamepad_2.left_stick_y));
                c.accept(true);
            }, BASKET_DEPO,50);
        }

        if (gamepad_1.y.isPress()) {
            switch (tsh.currentState()) {
                case BASKET_DEPO -> tsh.runTransition(c -> {
                    robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                    robot.vslides().retract();
                    scheduler.add(new Delay(() -> robot.vslides().closeEnough(5), () -> c.accept(true)));
                }, NEUTRAL, 1000);
                case NEUTRAL -> tsh.runTransition(c -> {
                    robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                    if (robot.hslides().getCurrentState() != HslideMotor.HslideMotorState.IN) {
                        robot.hslides().retract();
                        scheduler.add(new Delay(() -> robot.hslides().getCurrentPosition() < HslideMotor.HALF_EXTENSION, () -> robot.intake.setCurrentState(Intake.IntakeState.TRANSFER)));
                        scheduler.add(new Delay(
                                () -> robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.IN,
                                () -> {
                                    robot.depoClaw().close();
                                    scheduler.add(new Delay(200, () -> {
                                        robot.intakeClaw().open();
                                        scheduler.add(new Delay(600, () -> robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER)));
                                    }));
                                }
                        ));
                        scheduler.add(new Delay(() -> robot.hslides().closeEnough(5),() -> c.accept(true)));
                    } else {
                        robot.vslides().retract();
                        robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER);
                        scheduler.add(new Delay(() -> robot.vslides().closeEnough(5), () -> c.accept(true)));
                    }
                }, NEUTRAL,1000);
                case INTAKE, SPEC_DEPO, SPEC_INTAKE -> {
                    if (robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.OUT) {
                        tsh.runTransition(c -> {
                            robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                            robot.vslides().retract();
                            if (robot.intake.getCurrentState() == Intake.IntakeState.NEUTRAL_HIGHER || robot.intake.getCurrentState() == Intake.IntakeState.TRANSFER) {
                                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                            }
                            scheduler.add(new Delay(() -> robot.vslides().getCurrentState() == VslideMotor.VslideMotorState.IN, () -> c.accept(true)));
                        }, INTAKE,1000);
                    } else {
                        tsh.runTransition(c -> {
                            robot.hslides().retract();
                            robot.vslides().retract();
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER);
                            robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                            scheduler.add(new Delay(() -> robot.hslides().closeEnough(5) && robot.vslides().closeEnough(5),
                                    () -> c.accept(true)));
                        }, NEUTRAL,1000);
                    }
                }
            }
        }

        if (gamepad_1.a.isPress()) {
            switch (tsh.currentState()) {
                case SPEC_INTAKE -> tsh.task(() -> {
                        if (!specMode) {
                        if (robot.intakeClaw().getCurrentState() == IntakeClawServo.ClawState.CLOSE)
                            robot.intakeClaw().open();
                        else robot.depoClaw().close();
                    } else {
                        robot.intakeClaw().open();
                        robot.depoClaw().close();
                    }
                    }, new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),0,0,0,1,0));

                case INTAKE -> tsh.runTransition(c -> {
                    robot.intake.setCurrentState(Intake.IntakeState.INTAKE);
                    scheduler.add(new Delay(200, robot.intake::clawGrab));
                    c.accept(true);
                }, NEUTRAL,500);
                case BASKET_DEPO, SPEC_DEPO -> tsh.task(() -> robot.depoClaw().open(), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),0,0,1,0,1));
                case NEUTRAL -> tsh.task(() -> robot.depoClaw().toggle(), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),0,1,0,0,0));
            }
        }

        if (tsh.currentState() == INTAKE) {
            if (gamepad_1.dpad_right.isPress()) tsh.task(() -> robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_45), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),1,0,0,0,0));
            else if (gamepad_1.dpad_down.isPress()) tsh.task(() -> robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_PERP), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),1,0,0,0,0));
            else if (gamepad_1.dpad_left.isPress()) tsh.task(() -> robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_135), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),1,0,0,0,0));
            else if (gamepad_1.dpad_up.isPress()) tsh.task(() -> robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE), new FiniteFieldVector<>(FiniteFieldVector.binaryVector(),1,0,0,0,0));
        } else if (gamepad_1.dpad_up.isPress()) {
            tsh.runTransition(c -> {
                robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                scheduler.add(new Delay(() -> robot.vslides().closeEnough(5), () -> c.accept(true)));
            }, BASKET_DEPO,1000);
        } else if (gamepad_1.dpad_right.isPress()) {
            tsh.runTransition(c -> {
                robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC, VslideMotor.VSLIDE_LOW_BASKET_DEPO);
                robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                scheduler.add(new Delay(() -> robot.vslides().closeEnough(5), () -> c.accept(true)));
            }, BASKET_DEPO,1000);
        } else if (gamepad_1.dpad_down.isPress()) {
            if (tsh.currentState() == NEUTRAL) {
                tsh.runTransition(c -> {
                    if (specMode) {
                        scheduler.add(new Delay(150, () -> robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC)));
                        robot.depo.setCurrentState(Depo.DepoState.SPEC_INTAKE);
                    }
                    robot.hslides().retract();
                    scheduler.add(new Delay(() -> robot.hslides().getCurrentPosition() < HslideMotor.HALF_EXTENSION,
                            () -> robot.intake.setCurrentState(Intake.IntakeState.DROP_TO_HUMAN_PLAYER)));
                    scheduler.add(new Delay(() -> robot.hslides().closeEnough(5), () -> c.accept(true)));
                }, SPEC_INTAKE,1000);
            } else if (tsh.currentState() == SPEC_DEPO) {
                tsh.runTransition(c -> {
                    this.specDepoToSpecIntake();
                    c.accept(true);
            },SPEC_INTAKE,1000);
            }
        } else if (gamepad_1.dpad_left.isPress()) {
            tsh.runTransition(c -> {
                scheduler.add(new Delay(200, () -> robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC, VslideMotor.VSLIDE_SPEC_DEPO)));
                robot.depo.setCurrentState(Depo.DepoState.SPEC_DEPO);
                scheduler.add(new Delay(() -> robot.vslides().closeEnough(5), () -> c.accept(true)));
            }, SPEC_DEPO,1000);
        }
        if (gamepad_2.y.isPress()){
            tsh.runTransition(c -> {
                robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER);
                robot.vslides().retract();
                robot.hslides().retract();
                c.accept(true);
            },NEUTRAL,1000,true);
        }
    }

    @Override
    public void update() {
        robot.update();
        gamepad_1.update();
        gamepad_2.update();
        telemetry.update();
        drivetrain.updateLocalization();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
    private void specDepoToSpecIntake() {
        robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC);
        robot.depo.setCurrentState(Depo.DepoState.SPEC_INTAKE);
    }
}
