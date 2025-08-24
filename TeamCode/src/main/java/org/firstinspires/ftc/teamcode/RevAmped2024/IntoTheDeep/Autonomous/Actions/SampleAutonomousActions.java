package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions;

import androidx.annotation.NonNull;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.MathFunctions;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.FollowerManagerSampleAuto;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import java.util.HashMap;

import revAmped.components.PedroEnhancements.AutonomousEnumeratedAction;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.util.Range;

public class SampleAutonomousActions {
    private final RobotRevampedITD robot;
    private final FollowerManagerSampleAuto followerManager;
    private final Follower follower;
    private final Scheduler scheduler;
    private final IntakeFourbar intakeFourbar;
    private final DepositionArm depositionArm;
    private boolean driving = true;
    private boolean depo = true;
    private ElapsedTime overallTimer = new ElapsedTime();
    private ElapsedTime preloadDriveTimer = new ElapsedTime();
    private Telemetry telemetry;
    private HashMap<AutoState, AutonomousEnumeratedAction> actions = new HashMap<>();
    private final float TURRET_POS_PRELOAD = ITDConstants.DEPO_TURRET_AUTO_BASKET_1 + 10/255f;
    private final float TURRET_POS1 = (ITDConstants.DEPO_TURRET_AUTO_BASKET + ITDConstants.DEPO_TURRET_AUTO_BASKET_2) / 2;
    private final float TURRET_POS2 = (75/255f + ITDConstants.DEPO_TURRET_NEUTRAL)/2;
    private final float TURRET_POS3 = (ITDConstants.DEPO_TURRET_AUTO_BASKET + ITDConstants.DEPO_TURRET_AUTO_BASKET_2) / 2;
    private final float TURRET_POS_SUB = 10/255f+ITDConstants.DEPO_TURRET_AUTO_BASKET;

    public enum AutoState {
        DEPOSIT_PRELOAD,
        DEPOSIT_FIRST_SAMPLE,
        DEPOSIT_SECOND_SAMPLE,
        DEPOSIT_THIRD_SAMPLE,
        INTAKE_FROM_SUB_1,
        DEPOSIT_FROM_SUB_1,
        INTAKE_FROM_SUB_2,
        DEPOSIT_FROM_SUB_2,
        INTAKE_FROM_SUB_3,
        DEPOSIT_FROM_SUB_3,
        INTAKE_FROM_SUB_4,
        DEPOSIT_FROM_SUB_4,
        PARK
    }
    private AutoState autoState = AutoState.DEPOSIT_PRELOAD;

    public SampleAutonomousActions(RobotRevampedITD robot, FollowerManagerSampleAuto follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, Telemetry telemetry) {
        this.robot = robot;
        this.followerManager = follower;
        this.follower = follower.getFollower();
        this.scheduler = scheduler;
        this.intakeFourbar = intakeFourbar;
        this.depositionArm = depositionArm;
        this.telemetry = telemetry;

        actions.put(AutoState.DEPOSIT_PRELOAD, generateInitialPreloadDeposit());
        actions.put(AutoState.DEPOSIT_FIRST_SAMPLE, generateFirstPreloadDepo());
        actions.put(AutoState.DEPOSIT_SECOND_SAMPLE, generateSecondPreloadDepo());
        actions.put(AutoState.DEPOSIT_THIRD_SAMPLE, generateThirdSampleDepo());
        actions.put(AutoState.INTAKE_FROM_SUB_1, generateSubmersibleIntake());
        actions.put(AutoState.DEPOSIT_FROM_SUB_1, generateSubCycleDepo());
        actions.put(AutoState.INTAKE_FROM_SUB_2, generateSubmersibleIntake());
        actions.put(AutoState.DEPOSIT_FROM_SUB_2, generateSubCycleDepo());
        actions.put(AutoState.INTAKE_FROM_SUB_3, generateSubmersibleIntake());
        actions.put(AutoState.DEPOSIT_FROM_SUB_3, generateSubCycleDepo());
        actions.put(AutoState.INTAKE_FROM_SUB_4, generateSubmersibleIntake());
        actions.put(AutoState.DEPOSIT_FROM_SUB_4, generateSubCycleDepo());
        actions.put(AutoState.PARK, generateSubmersibleIntake());
    }

    public AutonomousEnumeratedAction generateSubmersibleIntake() {
        return new AutonomousEnumeratedAction() {
            private enum SubIntakeState {
                WAIT,
                DRIVING,
                FAILSAFE_INITIAL,
                FAILSAFE_UPDATE,
                RE_INTAKE,
                PARK,
                INTAKE,
                COMPLETE;
            }
            private SubIntakeState subIntakeState = SubIntakeState.DRIVING;
            private ElapsedTime intakeTimer = new ElapsedTime();

            private boolean colorSensorEnabled = true;
            private boolean aborted = false;
            private boolean initialExtension = false;
            private PathChain pathChain;
            private ElapsedTime derivativeTimer = new ElapsedTime();
            private boolean correctForward = false;

            @Override
            protected void init() {
                robot.hslideLock.close();

                robot.vslides.fullRetract();
                depositionArm.resetDepoArmAuto();

                pathChain = followerManager.getPathChain(autoState);
                robot.intakeTrapdoor.close();
                robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
            }

            @Override
            protected void loop() {
                telemetry.addData("substate",subIntakeState);
                telemetry.addData("color",robot.colorSensor.getColor());
                if (overallTimer.milliseconds() > 29000 && !aborted) {
                    if (robot.intake.getPower() != 0) {
                        robot.intake.setPower(-0.4f);
                    }
                    robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
                    robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
                    subIntakeState = SubIntakeState.WAIT;
                    aborted = true;
                } else {
                    switch (subIntakeState) {
                        case DRIVING:
                            if (finishedDriving()) {
                                robot.intake.setPower(-0.6f);
                                scheduler.add(new Delay(500, () -> subIntakeState = SubIntakeState.RE_INTAKE));
                            } else if (!initialExtension && follower.getCurrentTValue() > 0.8) {
                                robot.depoClaw.open();
                                intakeFourbar.edgeSampleClearing();
                            }

                            break;
                        case FAILSAFE_INITIAL:
                            intakeFourbar.transfer();
                            robot.intake.stop();
                            robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX / 8, true);
                            if (!correctForward) {
                                follower.followPath(strafeFailsafe(), true);
                            } else {
                                correctForward = false;
                                double currentDistFromSub = robot.ultrasonic.getDistance();
                                Pose currentPose = follower.getPose();
                                Path path = new Path(new BezierLine(currentPose, new Pose(currentPose.getX(), currentPose.getY() - currentDistFromSub + 1)));
                                path.setConstantHeadingInterpolation(Math.toRadians(270));
                                follower.followPath(path);
                            }
                            subIntakeState = SubIntakeState.FAILSAFE_UPDATE;
                            break;
                        case FAILSAFE_UPDATE:
                            if (robot.hslides.getCurrentPosition() < ITDConstants.HSLIDES_MAX + 5) {
                                subIntakeState = SubIntakeState.WAIT;
                                scheduler.add(new Delay(200, () -> subIntakeState = SubIntakeState.RE_INTAKE));
                            }

                            break;
                        case RE_INTAKE:
                            subIntakeState = SubIntakeState.WAIT;
                            intakeFourbar.intake();
                            robot.intake.setPower(1f);
                            scheduler.add(new Delay(200, () -> {
                                robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX, true);
                                subIntakeState = SubIntakeState.INTAKE;
                                derivativeTimer.reset();
                                intakeTimer.reset();
                            }));
                            break;
                        case INTAKE:
                            ITDColor.DetectionColor color = robot.colorSensor.getColor();

                            if (robot.correctColor(color)) {
                                colorSensorEnabled = false;
                                transferInitialization();
                                subIntakeState = SubIntakeState.COMPLETE;
                            } else {
                                if (color != ITDColor.DetectionColor.None) {
                                    intakeFourbar.middlePosition();
                                    robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX / 8, true);
                                    follower.followPath(strafeFailsafe(), true);
                                    intakeTimer.reset();
                                    derivativeTimer.reset();
                                    robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, () -> {
                                        subIntakeState = SubIntakeState.FAILSAFE_UPDATE;
                                    }, -0.7);
                                } else if (intakeTimer.milliseconds() > 1500) {
                                    subIntakeState = SubIntakeState.FAILSAFE_INITIAL;
                                } else if (derivativeTimer.milliseconds() > 800 && ((robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT
                                        && robot.hslides.getCurrentPosition() < hSlideMotor.MAX_ETENSION * 3/8)
                                        || (robot.ultrasonic.getDistance() > 4))) {
                                    derivativeTimer.reset();
                                    correctForward = true;
                                    subIntakeState = SubIntakeState.FAILSAFE_INITIAL;
                                }
                            }

                            break;
                        case WAIT, PARK:
                            break;
                        case COMPLETE:
                            scheduler.add(new Delay(150, () -> completed = true));
                            subIntakeState = SubIntakeState.WAIT;
                            break;
                    }
                }
            }

            @NonNull
            private Path strafeFailsafe() {
                Pose currentPos = follower.getPose();
                Path advance;

                if (pathChain.getPath(pathChain.size() - 1).getLastControlPoint().getX() < 64) {
                    if (currentPos.getX() < 67) {
                        advance = new Path(new BezierPoint(new Point(Range.clip(currentPos.getX() + 3, 0, 69), currentPos.getY())));
                    } else {
                        advance = new Path(new BezierPoint(new Point(Range.clip(currentPos.getX() - 3, 0, 69), currentPos.getY())));
                    }
                } else {
                    if (currentPos.getX() < 60) {
                        advance = new Path(new BezierPoint(new Point(Range.clip(currentPos.getX() + 3, 0, 69), currentPos.getY())));
                    } else {
                        advance = new Path(new BezierPoint(new Point(Range.clip(currentPos.getX() - 3, 0, 69), currentPos.getY())));
                    }
                }
                advance.setConstantHeadingInterpolation(Math.toRadians(270));
                return advance;
            }

            @Override
            public boolean finishedDriving() {
                boolean result = ((follower.getHeadingError() < FollowerConstants.pathEndHeadingConstraint && follower.atParametricEnd() || (follower.getPose().getY() <= getLastPoint(pathChain).getY() && follower.getPose().getX() >= getLastPoint(pathChain).getX())) || (MathFunctions.distance(follower.getPose(), follower.getCurrentPath().getLastControlPoint()) < 4 && follower.getVelocity().getMagnitude() < FollowerConstants.pathEndVelocityConstraint)) && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }
        };
    }

    public AutonomousEnumeratedAction generateInitialPreloadDeposit() {
        return new AutonomousEnumeratedAction() {
            private boolean depositionComplete = false;
            private boolean transfer = false;
            private boolean turretMovementComplete = false;

            @Override
            protected void init() {
                preloadDriveTimer.reset();
                scheduler.delays.add(new Delay(450, () -> {
                    if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.OUT) {
                        robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX / 2, false);
                    }

                    intakeFourbar.middlePosition();
                }));

                scheduler.add(new Delay(() -> follower.getHeadingError() <= 0.25, () -> robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO)));
                scheduler.add(new Delay(100, () -> depositionArm.autoBasketDepo(TURRET_POS_PRELOAD, () -> turretMovementComplete = true)));
                robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
            }

            @Override
            protected void loop() {
                if (driving) {
                    finishedDriving();
                } else {
                    if (!depositionComplete) {
                        if ((turretMovementComplete && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) || preloadDriveTimer.milliseconds() > 1500) {
                            turretMovementComplete = false;
                            robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                            robot.depoTilt.setCurrentState(DepoTiltServo.TiltState.BASKET_INITIAL, (ITDConstants.DEPO_TILT_BASKET_AUTO_FINAL - ITDConstants.DEPO_TILT_BASKET_AUTO_INITIAL) * 2 + ITDConstants.DEPO_TILT_BASKET_AUTO_INITIAL);
                            scheduler.add(new Delay(200, () -> {
                                robot.depoClaw.open();

                                scheduler.add(new Delay(450, () -> {
                                    setDepositionComplete();
                                    robot.vslides.startTransfer();
                                    depositionArm.resetDepoArmAuto();
                                }));
                            }));
                        }
                    } else if (transfer) {
                        transfer = false;
                        robot.intakeTrapdoor.close();
                        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
                        robot.vslides.startTransfer();
                        intakeFourbar.transfer();
                        robot.hslides.retract(() -> scheduler.add(new Delay(150, () -> clawGrabSample(() -> completed = true))));
                    }
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = (!follower.isBusy() || preloadDriveTimer.milliseconds() > 2500) && driving;

                if (result) {
                    driving = false;
                    preloadDriveTimer.reset();
                }

                return result;
            }

            public void setDepositionComplete() {
                depositionComplete = true;
                intakeFourbar.intakePreloadAuto();
                robot.hslides.runToPositionAuto(hSlideMotor.MAX_ETENSION, false);
                robot.intake.setPower(1f);
                robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                scheduler.add(new Delay(() -> robot.colorSensor.getColor() == ITDColor.DetectionColor.Yellow, () -> transfer = true)
                        .setCancelation(2000, () -> {depo = false; robot.hslides.retract(() -> completed = true);}));
            }
        };
    }

    public AutonomousEnumeratedAction generateFirstPreloadDepo() {
        return new AutonomousEnumeratedAction() {
            private boolean depositionComplete = !depo;
            private boolean transfer = false;
            private boolean turretMovementComplete = false;

            @Override
            protected void init() {
                depo = true;
                preloadDriveTimer.reset();
                if (!depositionComplete) {
                    scheduler.delays.add(new Delay(450, () -> {
                        if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.OUT) {
                            robot.hslides.runToPositionAuto((ITDConstants.HSLIDES_MAX - 25) / 2, false);
                        }

                        intakeFourbar.middlePosition();
                    }));

                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    scheduler.add(new Delay(100, () -> depositionArm.autoBasketDepo(TURRET_POS1, () -> turretMovementComplete = true)));
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                } else {
                    setDepositionComplete();
                }
            }

            @Override
            protected void loop() {
                if (driving) {
                    finishedDriving();
                } else {
                    if (!depositionComplete) {
                        if ((turretMovementComplete && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) || preloadDriveTimer.milliseconds() > 1000) {
                            turretMovementComplete = false;

                            scheduler.add(new Delay(200, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(125, () -> {
                                    robot.depoClaw.open();

                                    scheduler.add(new Delay(450, () -> {
                                        setDepositionComplete();
                                        robot.vslides.startTransfer();
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            }));
                        }
                    } else if (transfer) {
                        transfer = false;
                        robot.intakeTrapdoor.close();
                        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
                        robot.vslides.startTransfer();
                        intakeFourbar.transfer();
                        robot.hslides.retract(() -> scheduler.add(new Delay(150, () -> clawGrabSample(() -> completed = true))));
                    }
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = (!follower.isBusy() || preloadDriveTimer.milliseconds() > 1500) && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            public void setDepositionComplete() {
                depositionComplete = true;
                intakeFourbar.intakePreloadAuto();
                robot.hslides.runToPositionAuto(hSlideMotor.MAX_ETENSION - 25, false);
                robot.intake.setPower(1f);
                robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                scheduler.add(new Delay(() -> robot.colorSensor.getColor() == ITDColor.DetectionColor.Yellow, () -> transfer = true)
                        .setCancelation(2000, () -> {depo = false; robot.hslides.retract(() -> completed = true);}));
            }
        };
    }

    public AutonomousEnumeratedAction generateSecondPreloadDepo() {
        return new AutonomousEnumeratedAction() {
            private boolean depositionComplete = !depo;
            private boolean transfer = false;
            private boolean turretMovementComplete = false;

            @Override
            protected void init() {
                depo = true;
                preloadDriveTimer.reset();
                if (!depositionComplete) {
                    scheduler.delays.add(new Delay(200, () -> {
                        if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.OUT) {
                            robot.hslides.runToPositionAuto((ITDConstants.HSLIDES_MAX - 25) / 2, false);
                        }

                        intakeFourbar.middlePosition();
                    }));

                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    scheduler.add(new Delay(100, () -> depositionArm.autoBasketDepo(TURRET_POS2, () -> turretMovementComplete = true)));
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                } else {
                    setDepositionComplete();
                }
            }

            @Override
            protected void loop() {
                if (driving) {
                    finishedDriving();
                } else {
                    if (!depositionComplete) {
                        if ((turretMovementComplete && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) || preloadDriveTimer.milliseconds() > 1000) {
                            turretMovementComplete = false;

                            scheduler.add(new Delay(200, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(75, () -> {
                                    robot.depoClaw.open();

                                    scheduler.add(new Delay(450, () -> {
                                        setDepositionComplete();
                                        robot.vslides.startTransfer();
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            }));
                        }
                    } else if (transfer) {
                        transfer = false;
                        robot.intakeTrapdoor.close();
                        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
                        robot.vslides.startTransfer();
                        intakeFourbar.transfer();
                        robot.hslides.retract(() -> scheduler.add(new Delay(150, () -> clawGrabSample(() -> completed = true))));
                    }
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = (follower.getHeadingError() < 0.025 || preloadDriveTimer.milliseconds() > 1500) && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            public void setDepositionComplete() {
                depositionComplete = true;
                intakeFourbar.intakePreloadAuto();
                robot.hslides.runToPositionAuto(hSlideMotor.MAX_ETENSION - 25, false);
                robot.intake.setPower(1f);
                robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                scheduler.add(new Delay(() -> robot.colorSensor.getColor() == ITDColor.DetectionColor.Yellow, () -> transfer = true)
                        .setCancelation(2000, () -> {
                            depo = false;
                            robot.hslides.retract(() -> completed = true);
                        }));
            }
        };
    }

    public AutonomousEnumeratedAction generateThirdSampleDepo() {
        return new AutonomousEnumeratedAction() {
            private boolean depositionComplete = !depo;
            private boolean turretMovementComplete = false;

            @Override
            protected void init() {
                depo = true;
                preloadDriveTimer.reset();
                if (!depositionComplete) {
                    scheduler.delays.add(new Delay(200, intakeFourbar::middlePosition));

                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    scheduler.add(new Delay(100, () -> depositionArm.autoBasketDepo(TURRET_POS3, () -> turretMovementComplete = true)));
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                } else {
                    setDepositionComplete();
                }
            }

            @Override
            protected void loop() {
                if (driving) {
                    finishedDriving();
                } else {
                    if (!depositionComplete) {
                        if ((turretMovementComplete && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) || preloadDriveTimer.milliseconds() > 1000) {
                            turretMovementComplete = false;

                            scheduler.add(new Delay(200, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(75, () -> {
                                    robot.depoClaw.open();

                                    scheduler.add(new Delay(450, () -> {
                                        setDepositionComplete();
                                        robot.vslides.fullRetract();
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            }));
                        }
                    }
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = (follower.getHeadingError() <= 0.02 || preloadDriveTimer.milliseconds() > 1500) && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            public void setDepositionComplete() {
                depositionComplete = true;
                completed = true;
            }
        };
    }

    public AutonomousEnumeratedAction generateSubCycleDepo() {
        return new AutonomousEnumeratedAction() {
            private boolean deposition = false;

            @Override
            protected void init() {
                transferCompletion(new Delay(() -> follower.getCurrentTValue() >= 0.45 && followerManager.getCurrentChainIndex() == autoState.ordinal(), () -> {
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    depositionArm.autoBasketDepoSub(TURRET_POS_SUB, () -> deposition = true);
                }));
            }

            @Override
            protected void loop() {
                telemetry.addData("path",followerManager.getCurrentChainIndex());
                telemetry.addData("ordinal",autoState.ordinal());

                if (deposition && finishedDriving() && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) {
                    scheduler.add(new Delay(100, () -> {
                        robot.depoClaw.open();
                        deposition = false;
                        scheduler.add(new Delay(100, () -> {
                            completed = true;
                        }));
                    }));
                }
            }

            @Override
            public boolean finishedDriving() {
                return follower.getCurrentTValue() >= 0.92 && followerManager.getCurrentChainIndex() == autoState.ordinal() && followerManager.distanceFromTarget() <= 4 && driving;
            }
        };
    }

    private Point getLastPoint(PathChain pathChain) {
        return pathChain.getPath(pathChain.size() - 1).getLastControlPoint();
    }

    public boolean update(ElapsedTime overallTimer) {
        this.overallTimer = overallTimer;
        return actions.get(autoState).update();
    }

    public AutoState getAutoState() {
        return autoState;
    }

    public void setAutoState(AutoState state) {
        this.autoState = state;
        driving = true;
    }

    public AutonomousEnumeratedAction getAutoStateAction(AutoState state) {
        return actions.get(state);
    }

    public AutoState getNextState() {
        int currentState = autoState.ordinal();
        int nextState = (currentState + 1) % AutoState.values().length;
        return AutoState.values()[nextState];
    }

    private void transferInitialization() {
        robot.vslides.startTransfer();
        robot.depoClaw.open();
        robot.intakeTrapdoor.close();
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
    }

    private boolean transferInitComplete() {
        return robot.intakeArm.reached(IntakeFourbarArmServo.IntakeVoltageState.PAST_MIDDLE_UP);
    }

    private void transferCompletion(Delay raise) {
        scheduler.add(new Delay(200, () -> {
            robot.intake.setPower(-1f);
            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);

            int delay;
            if (robot.hslides.getCurrentPosition() > ITDConstants.HSLIDES_MAX/2) {
                delay = 200;
            } else {
                delay = 400;
            }

            robot.hslides.retract(() -> scheduler.add(new Delay(delay, () -> clawGrabSample(raise))));
        }));
    }

    private void clawGrabSample(Delay raise) {
        clawGrabSample(() -> scheduler.add(raise));
    }

    private void clawGrabSample(Runnable action) {
        robot.depoClaw.looseClose();
        scheduler.add(new Delay(150, () -> {
            robot.intake.stop();
            telemetry.addData("state1","reached");
            action.run();
        }));
        scheduler.add(new Delay(75, robot.intakeTrapdoor::open));
    }

    public boolean isDriving() {
        return driving;
    }

    public boolean isDepo() {
        return depo;
    }
}
