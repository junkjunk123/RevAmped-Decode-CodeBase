package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousEnumeratedAction;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public class DAM {
    public static Robot robot;
    public static FollowerManager followerManager;
    public static Scheduler scheduler;
    public static Telemetry telemetry;
    public static ElapsedTime overallTimer;
    public static ElapsedTime driveTimer;
    public static DecodeLimelight limelight;

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
            @Override
            protected void init() {
                robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
                robot.turretMotor().setCurrentState(Types.TurretState.HOLD);
            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.turnTable.shootAllAuto(() -> {
                        if (DecodeLimelight.usingLimelight) {
                            limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                            robot.turretMotor().limelightDetection();
                            scheduler.add(new Delay(() -> Globals.randomizationState != null, () -> completed = true));
                        } else {
                            completed = true;
                            robot.intakeMotor().stop();
                        }
                    });
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
            }
        };
    }

    public static AutonomousEnumeratedAction intake() {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                scheduler.add(new Delay(() -> followerManager.follower.getCurrentTValue() > 0.75,
                        () -> robot.intakeMotor().setCurrentState(Types.IntakeState.INTAKE)));
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
            @Override
            protected void init() {

            }

            @Override
            protected void loop() {
                if (finishedDriving()) {
                    robot.turnTable.shootAllAuto(() -> {
                        completed = true;
                        robot.intakeMotor().stop();
                    });
                }
            }

            @Override
            public boolean isFinishedDriving() {
                return notBusy();
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
