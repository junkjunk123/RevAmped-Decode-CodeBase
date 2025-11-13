package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousEnumeratedAction;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Logger;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.FiniteFailsafeDelay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.util.LoopTimer;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import java.util.Arrays;
import java.util.List;

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
        robot = Globals.aae.parameters.getRobot();
        scheduler = Scheduler.getInstance();
        followerManager = Globals.aae.parameters.getFollowerManager();
        telemetry = Globals.telemetry;
        driveTimer = Globals.aae.preloadDriveTimer;
        overallTimer = Globals.aae.overallTimer;
    }

    private static boolean notBusy() {
        return !followerManager.follower.isBusy();
    }

    public static AutonomousEnumeratedAction shootPreloads() {
        return new AutonomousEnumeratedAction() {
            private final DecodeColorSensor.ArtifactColor[] artifactColors = new DecodeColorSensor.ArtifactColor[3];

            @Override
            protected void init() {
                robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
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
                                    updateColors();
                                    robot.tableServo().setCurrentState(computeTableRotation());
                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                    scheduler.add(new Delay(robot.tableServo()::reached, () -> {
                                        scheduler.add(new Delay(650, () -> scheduler.add(new Delay(() -> Math.abs(robot.turretMotor().getCurrentPosition() - TurretMotor.AUTO_PRELOADS) < 30,
                                                () -> robot.turnTable.shootAllAuto(() -> completed = true, 250)).setMaxTimeToRun(250))));
                                        scheduler.add(new Delay(150, () -> robot.popper().setCurrentState(Types.PopperState.POP)));
                                    }));
                                }).setCancellation(4000, () -> {
                                    robot.popper().setCurrentState(Types.PopperState.POP);
                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                    scheduler.add(new Delay(750, () -> robot.turnTable.shootAllAuto(() -> completed = true)));
                                }));
                    } else {
                        robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                        robot.popper().setCurrentState(Types.PopperState.POP);
                        scheduler.add(new Delay(1500, () -> robot.turnTable.shootAllAuto(() -> completed = true)));
                    }
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }

            private void updateColors() {
                ColorManager colorManager = robot.intakeColor();
                artifactColors[1] = colorManager.getCompartmentOneColor();
                artifactColors[2] = colorManager.getCompartmentTwoColor();

                if (artifactColors[1] == DecodeColorSensor.ArtifactColor.GREEN || artifactColors[2] == DecodeColorSensor.ArtifactColor.GREEN)
                    artifactColors[0] = DecodeColorSensor.ArtifactColor.PURPLE;
                else artifactColors[0] = DecodeColorSensor.ArtifactColor.GREEN;
            }

            private Types.TableState computeTableRotation() {
                List<Types.TableState> tableStates = List.of(Types.TableState.BALL1, Types.TableState.BALL2, Types.TableState.BALL3);
                greenIndex = Globals.randomizationState.getGreenIndex();
                int curGreenIndex = 0;
                for (int i = 0; i < 3; i++)
                    if (artifactColors[i] == DecodeColorSensor.ArtifactColor.GREEN)
                        curGreenIndex = i;
                int index = tableStates.indexOf(robot.tableServo().getCurrentState()) - greenIndex + curGreenIndex;
                index = (index + 3) % 3;
                return tableStates.get(index);
            }

            @Override
            public double millisToDrive() {
                return 5000;
            }
        };
    }

    public static AutonomousEnumeratedAction intake() {
        return new AutonomousEnumeratedAction() {
            private int currentIteration;

            @Override
            protected void init() {
                currentIteration = Globals.aae.currentIterationNumber();
                scheduler.add(new Delay(robot.tableServo()::reached,
                        () -> robot.popper().setCurrentState(Types.PopperState.NEUTRAL)).setMaxTimeToRun(1500));
                scheduler.add(new Delay(500, () -> robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 1)));
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.intakeMotor().setCurrentState(Types.IntakeState.SLOW_OUTTAKE);
                    boolean firstIteration = currentIteration == 0;
                    int outtakeTime = firstIteration ? 350 : 200;
                    scheduler.add(new Delay(outtakeTime, () -> {
                        scheduler.add(new Delay(100, () -> completed = true));
                        robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
                    }));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                if (currentIteration == 1)
                    return followerManager.follower.getVelocity().getMagnitude() < followerManager.getCurrentPath().getPathEndVelocityConstraint() &&
                            followerManager.distanceFromTarget() < 4;
                return notBusy();
            }

            @Override
            public double millisToDrive() {
                return currentIteration * 2500 + 4000;
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
        };
    }

    public static AutonomousEnumeratedAction shoot() {
        return new AutonomousEnumeratedAction() {
            private boolean updatedColors = false;
            private boolean popperIn = false;
            private final DecodeColorSensor.ArtifactColor[] artifactColors = new DecodeColorSensor.ArtifactColor[3];
            private int currentIteration;
            private LoopTimer loopTimer = new LoopTimer(50);
            private boolean runTimer = false;

            @Override
            protected void init() {
                currentIteration = Globals.aae.currentIterationNumber();
                boolean firstIteration = currentIteration == 0;
                if (!firstIteration)
                    robot.intakeMotor().setCurrentState(Types.IntakeState.SLOW_OUTTAKE);
                scheduler.addOptionalDelay(firstIteration, new Delay(650, () -> {
                    robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
                    scheduler.add(new Delay(() -> followerManager.follower.atParametricEnd(), () -> {
                        if (Globals.randomizationState != null) {
                            updateColors();
                            Scheduler.getInstance().add(new FiniteFailsafeDelay(() -> updatedColors,
                                    () -> {robot.tableServo().setCurrentState(computeTableRotation()); runTimer = true; loopTimer.begin();})
                                    .setFailsafe(50, t -> updateColors(), 6,
                                            () -> robot.tableServo().setCurrentState(computeTableRotation())));
                        }

                        scheduler.addOptionalDelay(Globals.randomizationState == null,
                                new Delay(robot.tableServo()::reached, () -> scheduler.add(new Delay(150, () -> {
                                    robot.popper().setCurrentState(Types.PopperState.POP);
                                    scheduler.add(new Delay(500, () -> popperIn = true));
                                    runTimer = false;
                                }))).setMaxTimeToRun(1500));
                        robot.shooter.hardware().turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_SET_1);
                    }));
                }));
            }

            @Override
            protected void loop() {
                if (runTimer)
                    loopTimer.update();
                telemetry.addData("colors", Arrays.toString(artifactColors));
                Logger.permaLog("avg loop time", loopTimer.getAverageLoopTime());

                if (finishedDriving()) {
                    scheduler.add(new Delay(() -> popperIn, () -> robot.turnTable.shootAllAuto(() -> completed = true, 250)));
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }

            private void updateColors() {
                ColorManager colorManager = robot.intakeColor();
                artifactColors[1] = colorManager.getCompartmentOneColor();
                artifactColors[2] = colorManager.getCompartmentTwoColor();

                if (!(artifactColors[1] == DecodeColorSensor.ArtifactColor.NONE || artifactColors[2] == DecodeColorSensor.ArtifactColor.NONE))
                    updatedColors = true;

                if (artifactColors[1] == DecodeColorSensor.ArtifactColor.GREEN || artifactColors[2] == DecodeColorSensor.ArtifactColor.GREEN)
                    artifactColors[0] = DecodeColorSensor.ArtifactColor.PURPLE;
                else artifactColors[0] = DecodeColorSensor.ArtifactColor.GREEN;
            }

            private Types.TableState computeTableRotation() {
                List<Types.TableState> tableStates = List.of(Types.TableState.BALL1, Types.TableState.BALL2, Types.TableState.BALL3);
                int curGreenIndex = 0;
                for (int i = 0; i < 3; i++)
                    if (artifactColors[i] == DecodeColorSensor.ArtifactColor.GREEN)
                        curGreenIndex = i;
                //int index = tableStates.indexOf(robot.tableServo().getCurrentState()) - greenIndex + curGreenIndex;
                int index = curGreenIndex - greenIndex;
                index = (index + 3) % 3;
                return tableStates.get(index);
            }

            @Override
            public double millisToDrive() {
                return currentIteration * 2500 + 4000;
            }
        };
    }

    public static AutonomousEnumeratedAction park() {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {

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
        };
    }
}
