package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.ftcontrol.panels.plugins.html.primitives.P;
import com.pedropathing.localization.Matrix;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Limelight;

import java.util.Arrays;
import java.util.List;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.MathUtil.Vector;
import revAmped.components.Misc.Globals;

@TeleOp(name = "OffSeasonTeleOp", group = "OffSeason")
public class OffSeasonTeleOp extends ITDRevAmpedOpMode {
    public Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    private boolean specMode = true;
    private Limelight limelight;

    public enum CycleState {
        INTAKE(new Vector(1, 1, 0, 0, 0)),
        NEUTRAL(new Vector(1, 1, 1, 1, 0)),
        BASKET_DEPO(new Vector(0, 1, 1, 0, 0)),
        SPEC_INTAKE(new Vector(1, 1, 0, 1, 1)),
        SPEC_DEPO(new Vector(1, 1, 0, 1, 1));

        private final Vector transitionStates;
        private static Matrix transitionMatrix;

        CycleState(Vector transitionStates) {
            this.transitionStates = transitionStates;
        }

        public Vector getTransitionStates() {
            return transitionStates;
        }

        public static Matrix getTransitionMatrix() {
            if (transitionMatrix == null) {
                transitionMatrix = new Matrix(new double[][] {
                        INTAKE.getTransitionStates().values(),
                        NEUTRAL.getTransitionStates().values(),
                        BASKET_DEPO.getTransitionStates().values(),
                        SPEC_INTAKE.getTransitionStates().values(),
                        SPEC_DEPO.getTransitionStates().values()
                });
            }

            return transitionMatrix;
        }
    }
    private CycleState currentState = CycleState.NEUTRAL;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.None);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        limelight = new Limelight(hardwareMap);
    }

    @Override
    public void RevAmpedLoop() {
        robot.drivetrain.arcadeDrive(gamepad_1, false, robot.intakeArm().getCurrentState() == IntakeArmServo.ArmState.NEUTRAL_LOWER && currentState == CycleState.INTAKE);

        if (gamepad_2.dpad_down.isPress()) {
            robot.toggleAllianceColor();
        }

        if (gamepad_2.dpad_up.isPress()) {
            specMode = !specMode;
        }

        if (gamepad_1.right_bumper.isPress()) {
            transition(() -> {
                if (currentState == CycleState.SPEC_DEPO) specDepoToSpecIntake();
                robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT, HslideMotor.HALF_EXTENSION);
                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
            }, CycleState.INTAKE);
        }

        if (gamepad_1.left_bumper.isPress()) {
            transition(() -> {
                if (currentState == CycleState.SPEC_DEPO) specDepoToSpecIntake();
                robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
            }, CycleState.INTAKE);
        }

        if (Math.abs(gamepad_2.left_stick_y) > 0.3f) {
            transition(() -> robot.hslides().finetune((int) (30 * -gamepad_2.left_stick_y)), CycleState.INTAKE);
        }

        if (Math.abs(gamepad_2.right_stick_y) > 0.3f) {
            transition(() -> robot.vslides().finetune((int) (30 * -gamepad_2.left_stick_y)), CycleState.BASKET_DEPO);
        }

        if (gamepad_1.y.isPress()) {
            switch (currentState) {
                case BASKET_DEPO -> transition(() -> {
                    robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                    robot.vslides().retract();
                }, CycleState.NEUTRAL);
                case NEUTRAL -> transition(() -> {
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
                    } else {
                        robot.vslides().retract();
                        robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER);
                    }
                }, CycleState.NEUTRAL);
                case INTAKE, SPEC_DEPO, SPEC_INTAKE -> {
                    if (robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.OUT) {
                        transition(() -> {
                            robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                            robot.vslides().retract();
                            if (robot.intake.getCurrentState() == Intake.IntakeState.NEUTRAL_HIGHER || robot.intake.getCurrentState() == Intake.IntakeState.TRANSFER) {
                                robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                            }
                        }, CycleState.INTAKE);
                    } else {
                        transition(() -> {
                            robot.hslides().retract();
                            robot.vslides().retract();
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER);
                            robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                        }, CycleState.NEUTRAL);
                    }
                }
            }
        }

        if (gamepad_1.a.isPress()) {
            switch (currentState) {
                case SPEC_INTAKE -> {
                    if (!specMode) {
                        if (robot.intakeClaw().getCurrentState() == IntakeClawServo.ClawState.CLOSE)
                            robot.intakeClaw().open();
                        else robot.depoClaw().close();
                    } else {
                        robot.intakeClaw().open();
                        robot.depoClaw().close();
                    }
                }
                case INTAKE -> transition(() -> {
                    robot.intake.setCurrentState(Intake.IntakeState.INTAKE);
                    scheduler.add(new Delay(200, robot.intake::clawGrab));
                    }, CycleState.NEUTRAL);
                case BASKET_DEPO, SPEC_DEPO -> robot.depoClaw().open();
                case NEUTRAL -> robot.depoClaw().toggle();
            }
        }

        if (currentState == CycleState.INTAKE) {
            if (gamepad_1.dpad_right.isPress()) robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_45);
            else if (gamepad_1.dpad_down.isPress()) robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_PERP);
            else if (gamepad_1.dpad_left.isPress()) robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_135);
            else if (gamepad_1.dpad_up.isPress()) robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE);
        } else if (gamepad_1.dpad_up.isPress()) {
            transition(() -> {
                robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
            }, CycleState.BASKET_DEPO);
        } else if (gamepad_1.dpad_right.isPress()) {
            transition(() -> {
                robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC, VslideMotor.VSLIDE_LOW_BASKET_DEPO);
                robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
            }, CycleState.BASKET_DEPO);
        } else if (gamepad_1.dpad_down.isPress()) {
            if (currentState == CycleState.NEUTRAL) {
                transition(() -> {
                    if (specMode) {
                        scheduler.add(new Delay(150, () -> robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC)));
                        robot.depo.setCurrentState(Depo.DepoState.SPEC_INTAKE);
                    }

                    robot.hslides().retract();
                    scheduler.add(new Delay(() -> robot.hslides().getCurrentPosition() < HslideMotor.HALF_EXTENSION, () -> robot.intake.setCurrentState(Intake.IntakeState.DROP_TO_HUMAN_PLAYER)));
                }, CycleState.SPEC_INTAKE);
            } else if (currentState == CycleState.SPEC_DEPO) {
                transition(this::specDepoToSpecIntake, CycleState.SPEC_INTAKE);
            }
        } else if (gamepad_1.dpad_left.isPress()) {
            transition(() -> {
                scheduler.add(new Delay(200, () -> robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC, VslideMotor.VSLIDE_SPEC_DEPO)));
                robot.depo.setCurrentState(Depo.DepoState.SPEC_DEPO);
            }, CycleState.SPEC_DEPO);
        }

        if (gamepad_1.x.isPress()) {
            transition(() -> {
                limelight.start();
                scheduler.add(new Delay(limelight::isDone, () -> {
                    robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE, limelight.wristAngle());
                    robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT, HslideMotor.inchesToTicks(limelight.hslidePosInches()));
                    scheduler.add(new Delay(350, () -> {
                        robot.intakeArm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_LOWER);
                        robot.intakeTilt().setCurrentState(IntakeTiltServo.TiltState.NEUTRAL);
                        robot.intakeTurret().intake(limelight.turretAngle());
                    }));
                    robot.intakeClaw().open();
                    robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER, false);
                }));
            }, CycleState.INTAKE);
        }
    }

    @Override
    public void update() {
        robot.update();
        gamepad_1.update();
        gamepad_2.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }

    private void transition(Runnable command, CycleState cycleState) {
        if (currentState.getTransitionStates().get(cycleState.ordinal()) > 0) {
            command.run();
            currentState = cycleState;
        }
    }

    private void specDepoToSpecIntake() {
        robot.vslides().setCurrentState(VslideMotor.VslideMotorState.SPEC);
        robot.depo.setCurrentState(Depo.DepoState.SPEC_INTAKE);
    }
}
