package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions;

import static revAmped.components.PedroEnhancements.AutonomousActionExecutor.currentIterationNumber;
import static revAmped.components.PedroEnhancements.AutonomousActionExecutor.driving;

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
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD.CycleState;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDAutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.PedroEnhancements.AutonomousActionExecutor;
import revAmped.components.PedroEnhancements.AutonomousEnumeratedAction;
import revAmped.components.PedroEnhancements.FollowerManager;
import revAmped.util.Range;

public class ITDAutonomousMethods {
    public static RobotRevampedITD robot;
    public static Scheduler scheduler;
    public static Follower follower;
    public static IntakeFourbar intakeFourbar;
    public static FollowerManager followerManager;
    public static DepositionArm depositionArm;
    public static Telemetry telemetry;
    public static ElapsedTime preloadDriveTimer;
    public static ElapsedTime overallTimer;
    private static final float TURRET_POS_PRELOAD = ITDConstants.DEPO_TURRET_AUTO_BASKET_1 + 10/255f;
    private static final float TURRET_POS1 = (ITDConstants.DEPO_TURRET_AUTO_BASKET + ITDConstants.DEPO_TURRET_AUTO_BASKET_2) / 2;
    private static final float TURRET_POS2 = (75/255f + ITDConstants.DEPO_TURRET_NEUTRAL)/2;
    private static final float TURRET_POS3 = (ITDConstants.DEPO_TURRET_AUTO_BASKET + ITDConstants.DEPO_TURRET_AUTO_BASKET_2) / 2;
    private static final float TURRET_POS_SUB = -20/255f+ITDConstants.DEPO_TURRET_AUTO_BASKET;
    private static final float TURRET_POS_UAC_DEPO = ITDConstants.DEPO_TURRET_AUTO_UAC;
    private static boolean depo = true;

    public static void initialize() {
        robot = ITDAutonomousActionExecutor.parameters.getRobot();
        scheduler = ITDAutonomousActionExecutor.parameters.getScheduler();
        follower = ITDAutonomousActionExecutor.parameters.getFollower();
        intakeFourbar = ITDAutonomousActionExecutor.parameters.getIntakeFourbar();
        followerManager = ITDAutonomousActionExecutor.parameters.getFollowerManager();
        depositionArm = ITDAutonomousActionExecutor.parameters.getDepositionArm();
        telemetry = ITDAutonomousActionExecutor.parameters.getTelemetry();
        preloadDriveTimer = ITDAutonomousActionExecutor.preloadDriveTimer;
        overallTimer = ITDAutonomousActionExecutor.overallTimer;
        depo = true;
    }

    public static AutonomousEnumeratedAction generateSubmersibleIntake() {
        return generateSubmersibleIntake(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateSubmersibleIntake(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            private enum SubIntakeState {
                WAIT,
                DRIVING,
                DRIVING_NO_CLEARING,
                FAILSAFE_INITIAL,
                FAILSAFE_UPDATE,
                RE_INTAKE,
                PARK,
                INTAKE,
                COMPLETE
            }
            private SubIntakeState subIntakeState = SubIntakeState.DRIVING_NO_CLEARING;
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
                scheduler.add(new Delay(200, robot.vslides::fullRetract));
                depositionArm.resetDepoArmAuto();
                pathChain = followerManager.getPathChain(AutonomousActionExecutor.getAutoState());
                robot.intakeTrapdoor.close();
                robot.setCycleState(CycleState.SAMPLE_INTAKE);

                int iteration = AutonomousActionExecutor.currentIterationNumber();
                if (iteration == 0 || iteration == 2) subIntakeState = SubIntakeState.DRIVING;
            }

            @Override
            protected void loop() {
                if (overallTimer.milliseconds() > 29000 && !aborted) {
                    scheduler.clear();
                    if (robot.intake.getPower() != 0) {
                        robot.intake.setPower(-0.4f);
                    }
                    robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
                    robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
                    subIntakeState = SubIntakeState.WAIT;
                    aborted = true;
                } else if (!aborted) {
                    switch (subIntakeState) {
                        case DRIVING:
                            if (finishedDriving()) {
                                robot.intake.setPower(-0.6f);
                                scheduler.add(new Delay(500, () -> {
                                    if (overallTimer.milliseconds() < 29000) subIntakeState = SubIntakeState.RE_INTAKE;
                                }));
                            } else if (!initialExtension && follower.getCurrentTValue() > 0.7) {
                                robot.depoClaw.open();
                                intakeFourbar.edgeSampleClearing();
                            }

                            break;
                        case DRIVING_NO_CLEARING:
                            if (finishedDriving()) {
                                if (overallTimer.milliseconds() < 29000) subIntakeState = SubIntakeState.RE_INTAKE;
                            } else if (!initialExtension && follower.getCurrentTValue() > 0.7) {
                                robot.depoClaw.open();
                                intakeFourbar.middlePosition();
                            }
                            break;
                        case FAILSAFE_INITIAL:
                            colorSensorEnabled = false;
                            intakeFourbar.transfer();
                            robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, () -> colorSensorEnabled = true, true);
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
                            if (overallTimer.milliseconds() < 29000) {
                                intakeFourbar.intakeSubAuto();
                                scheduler.add(new Delay(200, () -> {
                                    subIntakeState = SubIntakeState.WAIT;
                                    robot.intake.setPower(1f);
                                    robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX, true);
                                    subIntakeState = SubIntakeState.INTAKE;
                                    derivativeTimer.reset();
                                    intakeTimer.reset();
                                }));
                            }
                            break;
                        case INTAKE:
                            if (colorSensorEnabled) {
                                ITDColor.DetectionColor color = robot.colorSensor.getColor();

                                if (robot.correctColor(color)) {
                                    colorSensorEnabled = false;
                                    transferInitialization(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
                                    subIntakeState = SubIntakeState.COMPLETE;
                                } else {
                                    if (color != ITDColor.DetectionColor.None) {
                                        subIntakeState = SubIntakeState.WAIT;
                                        intakeFourbar.middlePosition();
                                        robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX / 8, true);
                                        intakeTimer.reset();
                                        derivativeTimer.reset();
                                        robot.intakeTrapdoor.unlockSample();
                                        robot.intake.stop();
                                        scheduler.add(new Delay(100, () -> {
                                            robot.intake.setPower(-0.8f);
                                            scheduler.add(new Delay(() -> robot.colorSensor.getColor() == ITDColor.DetectionColor.None, () -> scheduler.add(new Delay(450, () -> {
                                                scheduler.add(new Delay(() -> robot.hslides.getCurrentPosition() < ITDConstants.HSLIDES_MAX / 4, () -> {
                                                    robot.intakeTrapdoor.close();
                                                    robot.intake.stop();
                                                    subIntakeState = SubIntakeState.FAILSAFE_UPDATE;
                                                    follower.followPath(strafeFailsafe(), true);
                                                }));
                                            }))).setMaxTimeToRun(400));
                                        }));
                                    } else if (intakeTimer.milliseconds() > 1200) {
                                        subIntakeState = SubIntakeState.FAILSAFE_INITIAL;
                                    } else if (derivativeTimer.milliseconds() > 800 && ((robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT
                                            && robot.hslides.getCurrentPosition() < hSlideMotor.MAX_ETENSION * 3/8)
                                            || (robot.ultrasonic.getDistance() > 4))) {
                                        derivativeTimer.reset();
                                        correctForward = true;
                                        subIntakeState = SubIntakeState.FAILSAFE_INITIAL;
                                    }
                                }
                            }

                            break;
                        case WAIT, PARK:
                            break;
                        case COMPLETE:
                            scheduler.add(new Delay(300, () -> completed = true));
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
                boolean result = ((follower.getHeadingError() < FollowerConstants.pathEndHeadingConstraint && follower.getCurrentTValue() >= 0.8 || (follower.getPose().getY() <= followerManager.getLastPoint(pathChain).getY() && follower.getPose().getX() >= followerManager.getLastPoint(pathChain).getX())) || (MathFunctions.distance(follower.getPose(), follower.getCurrentPath().getLastControlPoint()) < 4 && follower.getVelocity().getMagnitude() < FollowerConstants.pathEndVelocityConstraint)) && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            @Override
            public String identifier() {
                return "subIntake";
            }
        };
    }

    public static AutonomousEnumeratedAction generateInitialPreloadDeposit() {
        return generateInitialPreloadDeposit(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateInitialPreloadDeposit(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
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
                                scheduler.add(new Delay(150, this::setDepositionComplete));
                                scheduler.add(new Delay(450, () -> {
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

    public static AutonomousEnumeratedAction generateFirstPreloadDepo() {
        return generateFirstPreloadDepo(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateFirstPreloadDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
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
                            //scheduler.add(new Delay(0, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(175, () -> {
                                    robot.depoClaw.open();
                                    scheduler.add(new Delay(150, this::setDepositionComplete));
                                    scheduler.add(new Delay(450, () -> {
                                        scheduler.add(new Delay(100, robot.vslides::fullRetract));
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            //}));
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

    public static AutonomousEnumeratedAction generateSecondPreloadDepo() {
        return generateSecondPreloadDepo(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateSecondPreloadDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
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

                            //scheduler.add(new Delay(200, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(75, () -> {
                                    robot.depoClaw.open();
                                    scheduler.add(new Delay(150, this::setDepositionComplete));
                                    scheduler.add(new Delay(350, () -> {
                                        scheduler.add(new Delay(100, robot.vslides::fullRetract));
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            //}));
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

    public static AutonomousEnumeratedAction generateThirdSampleDepo() {
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

                            //scheduler.add(new Delay(200, () -> {
                                robot.depoArm.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO, DepoArmServo.ArmState.HIGH_BASKET_AUTO.getPosition() + 30/255f);
                                robot.depoTilt.sampleDepoAutoDownFinal();
                                scheduler.add(new Delay(75, () -> {
                                    robot.depoClaw.open();
                                    scheduler.add(new Delay(150, this::setDepositionComplete));
                                    scheduler.add(new Delay(350, () -> {
                                        scheduler.add(new Delay(100, robot.vslides::fullRetract));
                                        depositionArm.resetDepoArmAuto();
                                    }));
                                }));
                            //}));
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

    public static AutonomousEnumeratedAction generateSubCycleDepo() {
        return generateSubCycleDepo(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateSubCycleDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            private boolean deposition = false;

            @Override
            protected void init() {
                transferCompletion(new Delay(() -> follower.getCurrentTValue() >= 0.45 && followerManager.getCurrentChainIndex() == getAutoState(), () -> {
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    depositionArm.autoBasketDepoSub(TURRET_POS_SUB, () -> deposition = true);
                }));
            }

            @Override
            protected void loop() {
                if (deposition && (!driving || finishedDriving()) && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 35) {
                    deposition = false;
                    //scheduler.add(new Delay(100, () -> {
                        robot.depoClaw.open();
                        scheduler.add(new Delay(100, () -> completed = true));
                    //}));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = follower.getCurrentTValue() >= 0.95 && followerManager.distanceFromTarget() <= 4 && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            @Override
            public String identifier() {
                return "subDepo";
            }
        };
    }

    public static AutonomousEnumeratedAction generateHPIntake() {
        return generateHPIntake(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateHPIntake(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            private boolean transfer = false;

            @Override
            protected void init() {
                robot.hslideLock.close();
                scheduler.add(new Delay(150, robot.vslides::fullRetract));
                depositionArm.resetDepoArmAuto();
                robot.intakeTrapdoor.close();
                robot.setCycleState(CycleState.SAMPLE_INTAKE);

                scheduler.add(new Delay(() -> follower.getCurrentTValue() >= 0.8, () -> {
                    robot.hslides.runToPosition(hSlideMotor.MAX_ETENSION);
                    intakeFourbar.intakePreloadAuto();
                    robot.intake.setPower(1f);
                    robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                    scheduler.add(new Delay(robot::correctColor, () -> transfer = true));
                }));
            }

            @Override
            protected void loop() {
                if (transfer) {
                    transferInitialization(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
                    scheduler.add(new Delay(150, () -> completed = true));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = !follower.isBusy() && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }
        };
    }

    public static AutonomousEnumeratedAction generatePartnerPreloadIntake() {
        return generatePartnerPreloadIntake(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generatePartnerPreloadIntake(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            private boolean transfer = false;

            @Override
            protected void init() {
                robot.hslideLock.close();
                robot.vslides.fullRetract();
                depositionArm.resetDepoArmAuto();
                robot.intakeTrapdoor.close();
                robot.setCycleState(CycleState.SAMPLE_INTAKE);

                scheduler.add(new Delay(() -> follower.getChainIndex() == follower.getCurrentPathChain().size() - 1, () -> {
                    robot.hslides.runToPosition(hSlideMotor.MAX_ETENSION);
                    intakeFourbar.intakePreloadAuto();
                    robot.intake.setPower(1f);
                    robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                    scheduler.add(new Delay(robot::correctColor, () -> transfer = true));
                }));
            }

            @Override
            protected void loop() {
                if (transfer) {
                    transferInitialization(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
                    scheduler.add(new Delay(150, () -> completed = true));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = !follower.isBusy() && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }
        };
    }

    public static AutonomousEnumeratedAction generateHPDepo() {
        return generateHPDepo(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generateHPDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            private boolean deposition = false;

            @Override
            protected void init() {
                transferCompletion(new Delay(() -> follower.getCurrentTValue() >= 0.65 && followerManager.getCurrentChainIndex() == getAutoState() && follower.getChainIndex() == follower.getCurrentPathChain().size() - 1, () -> {
                    robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    depositionArm.autoBasketDepoSub(TURRET_POS_UAC_DEPO, () -> deposition = true);
                }));
            }

            @Override
            protected void loop() {
                if (deposition && (!driving || finishedDriving()) && robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 30) {
                    deposition = false;
                    scheduler.add(new Delay(100, () -> {
                        robot.depoClaw.open();
                        scheduler.add(new Delay(100, () -> {
                            completed = true;

                            if (AutonomousActionExecutor.finalState()) {
                                robot.vslides.fullRetract();
                                depositionArm.resetDepoArmAuto();
                            }
                        }));
                    }));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = follower.getCurrentTValue() >= 0.92 && followerManager.getCurrentChainIndex() == getAutoState() && followerManager.distanceFromTarget() <= 4 && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }
        };
    }

    private static void transferInitialization(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        robot.vslides.startTransfer();
        robot.depoClaw.open();
        robot.intakeTrapdoor.close();
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        scheduler.add(new Delay(300, () -> {
            robot.intake.setPower(-0.7f);
            robot.hslides.retract();
        }));
    }

    private static boolean transferInitComplete() {
        return robot.intakeArm.reached(IntakeFourbarArmServo.IntakeVoltageState.PAST_MIDDLE_UP);
    }

    private static void transferCompletion(Delay raise) {
        scheduler.add(new Delay(250, () -> {
            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);

            int delay;
            if (robot.hslides.getCurrentPosition() > ITDConstants.HSLIDES_MAX/2) {
                delay = 200;
            } else {
                delay = 400;
            }

            if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.RETRACTION) robot.hslides.retract();

            scheduler.add(new Delay(() -> robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN,
                    ()-> scheduler.add(new Delay(delay, () -> clawGrabSample(raise)))));
        }));
    }

    private static void clawGrabSample(Delay raise) {
        clawGrabSample(() -> scheduler.add(raise));
    }

    private static void clawGrabSample(Runnable action) {
        robot.depoClaw.looseClose();
        scheduler.add(new Delay(150, () -> {
            robot.intake.stop();
            action.run();
        }));
        scheduler.add(new Delay(75, robot.intakeTrapdoor::open));
    }

    private static int getAutoState() {
        return AutonomousActionExecutor.getAutoState();
    }

    public static AutonomousEnumeratedAction generatePreloadSpecDepo() {
        return generatePreloadSpecDepo(robot, followerManager, follower, scheduler, intakeFourbar, depositionArm, overallTimer);
    }

    public static AutonomousEnumeratedAction generatePreloadSpecDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime overallTimer) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                robot.depoClaw.looseClose();
                robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG);
                robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP);
                depositionArm.specimenDepositUpClipAuto(false);
                robot.hslideLock.close();
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_UP_CLIP_FINAL);
                    scheduler.add(new Delay(350, () -> {
                        robot.depoClaw.open();
                        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
                        robot.intakeTrapdoor.close();
                        intakeFourbar.middlePosition();
                        completed = true;
                    }));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result = !follower.isBusy() && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }
        };
    }

    public static AutonomousEnumeratedAction generateIntakeSpecPreload() {
        return generateIntakeSpecPreload(robot, followerManager, follower, intakeFourbar, depositionArm, overallTimer, scheduler);
    }

    public static AutonomousEnumeratedAction generateIntakeSpecPreload(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime elapsedTime, Scheduler scheduler) {
        return new AutonomousEnumeratedAction() {
            int iteration;
            @Override
            protected void init() {
                iteration = currentIterationNumber();
                if (iteration == 0) {
                    scheduler.add(new Delay(300, () -> {
                        depositionArm.specimenIntakeAuto();
                        robot.vslides.fullRetract();
                    }));
                }
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    intakeFourbar.intakePreloadAuto();
                    robot.hslides.runToPositionAuto(hSlideMotor.MAX_ETENSION, true);
                    robot.intake.setPower(1f);
                    robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
                    scheduler.add(new Delay(robot::correctColor, () -> {
                        if (iteration == 2) robot.hslides.runToPosition(hSlideMotor.HALF_EXTENSION);
                        completed = true;
                        robot.intake.stop();
                    }).setMaxTimeToRun(2000));
                }
            }

            @Override
            public boolean finishedDriving() {
                boolean result;

                if (iteration == 0) result = Math.abs(follower.getPose().getHeading() - followerManager.getCurrentPath().getHeadingGoal(1)) < 0.008 && driving;
                else result = !follower.isBusy() && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            @Override
            public String identifier() {
                return "intakeSpecPreload";
            }
        };
    }

    public static AutonomousEnumeratedAction generateOuttakeSpecPreload() {
        return generateOuttakeSpecPreload(robot, followerManager, follower, intakeFourbar, depositionArm, overallTimer, scheduler);
    }

    public static AutonomousEnumeratedAction generateOuttakeSpecPreload(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime elapsedTime, Scheduler scheduler) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                if (currentIterationNumber() == 2) robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    Runnable nextState = () -> {
                        completed = true;
                        robot.intake.setPower(0);
                        robot.hslides.runToPosition(ITDConstants.HSLIDES_MAX / 2);
                    };

                    intakeFourbar.middlePosition();
                    robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, nextState, 0.7f);
                }
            }

            @Override
            public boolean finishedDriving() {
                double headingThreshold = currentIterationNumber() * 0.0025 + 0.025;
                boolean result = follower.getPose().getHeading() - followerManager.getCurrentPath().getHeadingGoal(1) < headingThreshold && driving;

                if (result) {
                    driving = false;
                }

                return result;
            }

            @Override
            public String identifier() {
                return "outtakeSpecPreload";
            }
        };
    }

    public static AutonomousEnumeratedAction generateSpecIntake() {
        return generateSpecIntake(robot, followerManager, follower, intakeFourbar, depositionArm, overallTimer, scheduler);
    }

    public static AutonomousEnumeratedAction generateSpecIntake(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime elapsedTime, Scheduler scheduler) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                if (currentIterationNumber() != 0) scheduler.add(new Delay(200, () -> {depositionArm.specimenIntakeAuto(); robot.vslides.fullRetract();}));
                else robot.hslides.retract();
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.depoClaw.setCurrentState(DepoClawServo.ClawState.LOOSE_CLOSED, ITDConstants.DEPO_CLAW_STUPID_THILAN_POSITION);
                    scheduler.add(new Delay(300, () -> {
                        robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG);
                        depositionArm.specimenDepositUpClipAuto(true);
                        completed = true;
                    }));
                }
            }

            @Override
            public boolean finishedDriving() {
                return isBusyComplete(follower);
            }

            @Override
            public String identifier() {
                return "specIntake";
            }
        };
    }

    public static AutonomousEnumeratedAction generateSpecDepo() {
        return generateSpecDepo(robot, followerManager, follower, intakeFourbar, depositionArm, overallTimer, scheduler);
    }

    public static AutonomousEnumeratedAction generateSpecDepo(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime elapsedTime, Scheduler scheduler) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {}

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.depoClaw.close();
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_UP_CLIP_FINAL);
                    scheduler.add(new Delay(350, () -> {
                        robot.depoClaw.open();
                        completed = true;
                    }));
                }
            }

            @Override
            public boolean finishedDriving() {
                return isBusyComplete(follower);
            }

            @Override
            public String identifier() {
                return "specDepo";
            }
        };
    }

    private static boolean isBusyComplete(Follower follower) {
        boolean result = (!follower.isBusy() || (followerManager.distanceFromTarget() < 3 && follower.getVelocity().getMagnitude() < FollowerConstants.pathEndVelocityConstraint)) && driving;

        if (result) {
            driving = false;
        }

        return result;
    }

    public static AutonomousEnumeratedAction generatePark() {
        return generatePark(robot, followerManager, follower, intakeFourbar, depositionArm, overallTimer, scheduler);
    }

    public static AutonomousEnumeratedAction generatePark(RobotRevampedITD robot, FollowerManager followerManager, Follower follower, IntakeFourbar intakeFourbar, DepositionArm depositionArm, ElapsedTime elapsedTime, Scheduler scheduler) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                scheduler.add(new Delay(200, () -> {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
                    robot.depoClaw.open();
                    robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
                    scheduler.add(new Delay(350, () -> {
                        robot.depoArm.setCurrentState(DepoArmServo.ArmState.NEUTRAL);
                        robot.depoTilt.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
                        completed = true;
                    }));
                    robot.vslides.fullRetract();
                }));
            }

            @Override
            protected void loop() {

            }

            @Override
            public boolean finishedDriving() {
                return isBusyComplete(follower);
            }
        };
    }
}
