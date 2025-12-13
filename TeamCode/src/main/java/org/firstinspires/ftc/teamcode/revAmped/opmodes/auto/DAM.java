package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousEnumeratedAction;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.List;
import java.util.function.BooleanSupplier;

public class DAM {
    public static Robot robot;
    public static FollowerManager followerManager;
    public static Scheduler scheduler;
    public static Telemetry telemetry;
    public static ElapsedTime overallTimer;
    public static ElapsedTime driveTimer;
    public static DecodeLimelight limelight;
    private static int greenIndex = -1;

    public static void initialize() {
        robot = Globals.aae.parameters.robot();
        scheduler = Scheduler.getInstance();
        followerManager = Globals.aae.parameters.followerManager();
        telemetry = Globals.telemetry;
        driveTimer = Globals.aae.preloadDriveTimer;
        overallTimer = Globals.aae.overallTimer;
    }

    public static boolean notBusy() {
        return !followerManager.follower.isBusy();
    }

    public static AutonomousEnumeratedAction shootPreloads() {
        return new AutonomousEnumeratedAction() {
            boolean thirdSlot = false;

            @Override
            protected void init() {
                robot.flywheel().run(Flywheel.FLYWHEEL_AUTO_VELOCITY);
                robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
            }

            @Override
            protected void loop() {
                if (limelight != null)
                    limelight.update();

                if (finishedDriving()) {
                    if (DecodeLimelight.usingLimelight) {
                        limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                        robot.turretMotor().limelightDetection();
                        scheduler.add(new Delay(() -> Globals.randomizationState != null,
                                () -> {
                                    robot.tableServo().setCurrentState(computeTableRotation());

                                    if (robot.tableServo().getCurrentState() == Types.TableState2.BALL3)
                                        thirdSlot = true;

                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                    scheduler.add(new Delay(robot.tableServo()::reached, () -> {
                                        scheduler.add(new Delay(800,
                                                () -> scheduler.add(new Delay(() -> Math.abs(robot.turretMotor().getCurrentPosition() - TurretMotor.AUTO_PRELOADS) < 30,
                                                () -> robot.turnTable.shootAllAuto(this::setComplete, 175, thirdSlot)).setMaxTimeToRun(250))));
                                        scheduler.add(new Delay(150, () -> robot.popper().setCurrentState(Types.PopperState.POP)));
                                    }).setMaxTimeToRun(1500));
                                }).setCancellation(4000, () -> {
                                    robot.popper().setCurrentState(Types.PopperState.POP);
                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                    scheduler.add(new Delay(500, () -> robot.turnTable.shootAllAuto(this::setComplete, thirdSlot)));
                                }));
                    } else {
                        robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                        robot.popper().setCurrentState(Types.PopperState.POP);
                        scheduler.add(new Delay(1500, () -> robot.turnTable.shootAllAuto(this::setComplete, thirdSlot)));
                    }
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return followerManager.velocityCondition(4);
            }

            @Override
            public double millisToDrive() {
                return 5000;
            }

            @Override
            public String identifier() {
                return "preloads";
            }

            private void setComplete() {
                scheduler.add(new Delay(1000, () -> completed = true));
            }
        };
    }

    private static Types.TableState2 computeTableRotation() {
        List<Types.TableState2> tableStates = List.of(Types.TableState2.BALL1, Types.TableState2.BALL2, Types.TableState2.BALL3);
        greenIndex = Globals.randomizationState.getGreenIndex();
        int curGreenIndex = 1;
        int index = curGreenIndex - greenIndex;
        index = (index + 3) % 3;
        return tableStates.get(index);
    }

    public static AutonomousEnumeratedAction intake() {
        return new AutonomousEnumeratedAction() {
            private int currentIteration;

            @Override
            protected void init() {
                currentIteration = Globals.aae.currentIterationNumber();
                switch (currentIteration) {
                    case 0 -> robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_SET_1);
                    case 1 -> robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_SET_2);
                    case 2 -> robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_SET_3);
                }
                robot.flywheel().setCurrentState(Types.FlywheelState.STOPPED);

                if (robot.popper().getCurrentState() != Types.PopperState.NEUTRAL)
                    scheduler.add(new Delay(robot.tableServo()::reached, () -> {
                        robot.popper().setCurrentState(Types.PopperState.NEUTRAL);
                    }).setMaxTimeToRun(800));
                else
                    scheduler.add(new Delay(250, () -> robot.tableServo().resetToNeutral()));
                scheduler.add(new Delay(500, () -> robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 1)));
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.intakeMotor().setCurrentState(Types.IntakeState.SLOW_OUTTAKE);
                    scheduler.add(new Delay(350, () -> {
                        scheduler.add(new Delay(100, () -> completed = true));
                        robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
                    }));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                if (currentIteration == 1)
                    return followerManager.velocityCondition() &&
                            followerManager.follower.getCurrentTValue() > 0.8;
                return notBusy();
            }

            @Override
            public double millisToDrive() {
                if (currentIteration == 1)
                    return 3000;
                if (currentIteration == 2)
                    return 5000;
                return 3000;
            }

            @Override
            public String identifier() {
                return "intake";
            }
        };
    }

    public static AutonomousEnumeratedAction openGate() {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {

            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    completed = true;
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }

            @Override
            public String identifier() {
                return "openGate";
            }
        };
    }

    public static AutonomousEnumeratedAction shoot() {
        return new AutonomousEnumeratedAction() {
            private boolean popperIn = false;
            private int currentIteration;
            private boolean thirdSlot = false;

            @Override
            protected void init() {
                currentIteration = Globals.aae.currentIterationNumber();

                boolean firstIteration = currentIteration == 0;

                if (!firstIteration) {
                    scheduler.add(new Delay(200, () -> robot.intakeMotor().setCurrentState(Types.IntakeState.SLOW_OUTTAKE)));
                }

                double offset = 0;
                if (currentIteration == 2)
                    offset = 10;

                robot.flywheel().run(Flywheel.FLYWHEEL_AUTO_VELOCITY + offset);

                scheduler.addOptionalDelay(firstIteration, new Delay(300, () -> {
                    robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
                    scheduler.add(new Delay(() -> followerManager.follower.getCurrentTValue() > 0.75, () -> {
                        if (Globals.randomizationState != null && currentIteration != 2) {
                            computeTableRotation();
                        }

                        scheduler.addOptionalDelay(Globals.randomizationState == null || currentIteration == 2,
                                new Delay(robot.tableServo()::reached, () -> scheduler.add(new Delay(150, () -> {
                                    robot.popper().setCurrentState(Types.PopperState.POP);
                                    scheduler.add(new Delay(250, () -> popperIn = true));
                                }))).setMaxTimeToRun(1500));
                    }).addRunCondition(() -> currentIteration == 2 && overallTimer.seconds() > 27.5 && overallTimer.seconds() < 28.7 && followerManager.distanceFromTarget() < 7));
                }).addRunCondition(() -> currentIteration == 2 && overallTimer.seconds() > 27));
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    if (currentIteration != 2 && Globals.randomizationState != null)
                        scheduler.add(new Delay(() -> popperIn, () -> robot.turnTable.shootAllAuto(() -> completed = true, 175, thirdSlot)).setCancellation(() -> overallTimer.seconds() > 28.7));
                    else
                        scheduler.add(new Delay(() -> popperIn, () -> robot.turnTable.shootAllAuto(() -> completed = true, thirdSlot)).setCancellation(() -> overallTimer.seconds() > 28.7));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                if (currentIteration == 1 || currentIteration == 2)
                    return followerManager.velocityCondition() && followerManager.follower.getCurrentTValue() > 0.8;

                return followerManager.follower.atParametricEnd() || followerManager.distanceFromTarget() < 1.5;
            }

            private int getGreenIndex() {
                if (currentIteration == 0)
                    return 1;
                else if (currentIteration == 1)
                    return 0;
                else
                    return 2;
            }

            private void computeTableRotation() {
                List<Types.TableState2> tableStates = List.of(Types.TableState2.BALL1, Types.TableState2.BALL2, Types.TableState2.BALL3);
                int curGreenIndex = getGreenIndex();
                int index = curGreenIndex - greenIndex;
                index = (index + 3) % 3;
                robot.tableServo().setCurrentState(tableStates.get(index));

                if (tableStates.get(index) == Types.TableState2.BALL3)
                    thirdSlot = true;
            }


            @Override
            public double millisToDrive() {
                if (currentIteration == 0)
                    return 3000;
                if (currentIteration == 1)
                    return 2700;
                return 3500;
            }

            @Override
            public String identifier() {
                return "shoot";
            }
        };
    }

    public static AutonomousEnumeratedAction park() {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                robot.turretMotor().setCurrentState(Types.TurretState.HOLD);
                robot.flywheel().stop();
                robot.intakeMotor().stop();

                if (robot.popper().getCurrentState() != Types.PopperState.NEUTRAL) {
                    robot.popper().setCurrentState(Types.PopperState.NEUTRAL);
                    scheduler.add(new Delay(250, () -> robot.tableServo().resetToNeutral()));
                } else {
                    robot.tableServo().resetToNeutral();
                }
            }

            @Override
            protected void loop() {
                if (finishedDriving())
                    completed = true;
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }

            @Override
            public String identifier() {
                return "park";
            }
        };
    }

    public static void close() {
        if (limelight != null)
            limelight.close();
    }

    public static AutonomousEnumeratedAction obeliskDetectionFar() {
        return new AutonomousEnumeratedAction() {
            boolean detect = false;
            BooleanSupplier failsafe;

            @Override
            protected void init() {
                robot.turretMotor().setCurrentState(Types.TurretState.HOLD, (int) (-TurretMotor.FAR_AUTO + 10 * Math.signum(TurretMotor.FAR_AUTO)));
                scheduler.add(new Delay(() -> detect && Globals.randomizationState != null, () -> {
                    completed = true;
                }).setCancellation(() -> failsafe != null && failsafe.getAsBoolean(), () -> completed = true));
            }

            @Override
            protected void loop() {
                if (!detect && followerManager.follower.getCurrentTValue() > 0.75) {
                    limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                    detect = true;
                }

                if (finishedDriving()) {
                    failsafe = MathUtil.booleanSupplierfromTimeDelay(3000);
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }
        };
    }

    public static AutonomousEnumeratedAction shootPreloadsFar() {
        return new AutonomousEnumeratedAction() {
            boolean popperIn = false;

            @Override
            protected void init() {
                robot.flywheel().run(Flywheel.FLYWHEEL_FAR_VELOCITY);
                robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.FAR_AUTO);

                if (Globals.randomizationState != null) {
                    robot.tableServo().setCurrentState(computeTableRotation());
                    scheduler.add(new Delay(robot.tableServo()::reached, () -> scheduler.add(new Delay(150, () -> {
                        robot.popper().setCurrentState(Types.PopperState.POP);
                        scheduler.add(new Delay(650, () -> popperIn = true));
                    }))).setMaxTimeToRun(1500));
                } else {
                    robot.popper().setCurrentState(Types.PopperState.POP);
                    scheduler.add(new Delay(1500, () -> popperIn = true));
                }
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    scheduler.add(new Delay(() -> popperIn, () -> {
                        if (Globals.randomizationState != null)
                            scheduler.add(new Delay(() -> popperIn, () -> robot.turnTable.shootAllAuto(() -> completed = true, 250, true)));
                        else
                            scheduler.add(new Delay(() -> popperIn, () -> robot.turnTable.shootAllAuto(() -> completed = true, 100, true)));
                    }));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                if (!DecodeLimelight.usingLimelight)
                    return true;

                return notBusy();
            }
        };
    }

    public static AutonomousEnumeratedAction driveToIntake() {
        return new AutonomousEnumeratedAction() {
            boolean popperOut;
            @Override
            protected void init() {
                robot.flywheel().setCurrentState(Types.FlywheelState.STOPPED);

                if (robot.popper().getCurrentState() != Types.PopperState.NEUTRAL)
                    scheduler.add(new Delay(robot.tableServo()::reached, () -> {
                        robot.popper().setCurrentState(Types.PopperState.NEUTRAL);
                        popperOut = true;
                    }).setMaxTimeToRun(800));
                else
                    scheduler.add(new Delay(250, () -> {
                        robot.tableServo().resetToNeutral();
                        scheduler.add(new Delay(robot.tableServo()::reached, () -> {
                            popperOut = true;
                        }).setMaxTimeToRun(700));
                    }));
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    scheduler.add(new Delay(() -> popperOut, () -> completed = true));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return followerManager.velocityCondition(4);
            }

            @Override
            public double millisToDrive() {
                return 3000;
            }
        };
    }

    public static AutonomousEnumeratedAction intakeFar() {
        return new AutonomousEnumeratedAction() {

            @Override
            protected void init() {
                Robot.INSTANCE.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    scheduler.add(new Delay(500, () -> completed = true));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy() || followerManager.velocityCondition(4);
            }
        };
    }

    public static AutonomousEnumeratedAction shootFar() {
        return new AutonomousEnumeratedAction() {
            boolean popperIn = false;

            @Override
            protected void init() {
                robot.flywheel().run(Flywheel.FLYWHEEL_FAR_VELOCITY);
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    /*
                    scheduler.add(new Delay(500, () -> {
                        if (Globals.randomizationState != null) {
                            computeTableRotation();
                        }

                        scheduler.addOptionalDelay(Globals.randomizationState == null,
                                new Delay(robot.tableServo()::reached, () -> scheduler.add(new Delay(150, () -> {
                                    robot.popper().setCurrentState(Types.PopperState.POP);
                                    scheduler.add(new Delay(650, () -> popperIn = true));
                                }))).setMaxTimeToRun(1500));
                        robot.shooter.hardware().turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.FAR_AUTO);
                    }));

                     */

                    scheduler.add(new Delay(500, () -> {
                        robot.popper().setCurrentState(Types.PopperState.POP);
                        scheduler.add(new Delay(650, () -> popperIn = true));
                        robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.FAR_AUTO);
                    }));
                }

                if (popperIn) {
                    popperIn = false;

                    if (Globals.randomizationState != null)
                        robot.turnTable.shootAllAuto(() -> completed = true, 250, true);
                    else
                        robot.turnTable.shootAllAuto(() -> completed = true, 100, true);
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return followerManager.velocityCondition(4);
            }

            @Override
            public double millisToDrive() {
                return 4000;
            }
        };
    }
}
