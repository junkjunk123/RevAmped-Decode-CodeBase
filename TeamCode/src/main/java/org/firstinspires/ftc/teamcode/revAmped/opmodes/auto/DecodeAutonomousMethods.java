package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import static org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.ThreeBallPath.NEXT_POSE;
import static org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.ThreeBallPath.START_POSE;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Command;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.DelayChain;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Pause;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.function.DoubleSupplier;

public class DecodeAutonomousMethods {
    public static Robot robot;
    public static FollowerManager followerManager;
    public static Scheduler scheduler;
    public static Telemetry telemetry;
    public static ElapsedTime overallTimer;
    public static ElapsedTime driveTimer;

    public static void initialize() {
        robot = Globals.aae.parameters.getRobot();
        scheduler = Scheduler.getInstance();
        followerManager = Globals.aae.parameters.getFollowerManager();
        telemetry = Globals.telemetry;
        driveTimer = Globals.aae.preloadDriveTimer;
        overallTimer = Globals.aae.overallTimer;
    }

    public static void build(Robot robot, FollowerManager followerManager, boolean far) {
        new DelayChain("ian loves jasmine",
                //Shoot Preloads
                new DelayChain(
                        new Command(() -> {
                            followerManager.start();
                            robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
                            robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
                            if (robot.getAllianceColor() == Globals.AllianceColor.Red)
                                robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS);
                            else
                                robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.TURRET_POSITIVE_LIMIT_TICKS);
                        }),
                        new Delay(
                                () -> !followerManager.follower.isBusy() || (far &&
                                        followerManager.follower.getVelocity().getMagnitude() < PathConstraints.defaultConstraints.getVelocityConstraint()),
                                () -> {}
                        ).setMaxTimeToRun(!far? 3500 : 7500),
                        new Delay(
                                1500,
                                robot::shootAll
                        ),
                        new Pause(
                                () -> robot.turnTable.getNumberOfBalls() == 0,
                                8000
                        ),
                        new Delay(
                                3000,
                                () -> robot.intakeMotor().stop()
                        ),
                        new Delay(3000,
                                () -> {
                                    robot.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD);
                                }
                        ),
                        new Delay(
                                12000,
                                () -> followerManager.follower.followPath(
                                        followerManager.follower.pathBuilder()
                                                .addPath(new BezierLine(START_POSE, NEXT_POSE))
                                                .setConstantHeadingInterpolation(Math.toRadians(-90))
                                                .setNoDeceleration()
                                                .build()
                                )
                        )
                )

                /*
                //INTAKE_FIRST_SET
                new DelayChain(
                        new Command(
                                followerManager::start
                        ),
                        new Delay(
                                () -> followerManager.follower.getCurrentTValue() > 0.5,
                                () -> robot.turnTable.intake()
                        ).setMaxTimeToRun(1000),
                        new Pause.OffsetCanceledPause (
                                robot.turnTable::full,
                                () -> followerManager.finished(true),
                                1500
                        )
                ),
                //FLIP_GATE
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Pause(
                                () -> followerManager.finished(false)
                        ),
                        new Pause(1500)
                ),
                //SHOOT_FIRST_SET
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Delay(
                                () -> followerManager.finished(true),
                                () -> {
                                    //limelight.computeRandomizationState();
                                    Globals.randomizationState = Globals.RandomizationState.PPG;
                                    robot.turretMotor().limelightDetection();
                                }
                        ).setMaxTimeToRun(2500),
                        new Delay(
                                () -> Globals.randomizationState != null,
                                robot::shootAll
                        ).setMaxTimeToRun(1000),
                        new Pause(
                                () -> robot.turnTable.getNumberOfBalls() == 0
                        ),
                        new Pause(
                                1000
                        )
                ),
                //INTAKE_SECOND_SET
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Delay(
                                () -> followerManager.follower.getCurrentTValue() > 0.5,
                                () -> robot.turnTable.intake()
                        ).setMaxTimeToRun(1500),
                        new Pause.OffsetCanceledPause (
                                robot.turnTable::full,
                                () -> followerManager.finished(true),
                                1500
                        )
                ),
                //SHOOT_SECOND_SET
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Delay(
                                () -> followerManager.finished(true),
                                robot::shootAll
                        ).setMaxTimeToRun(3000),
                        new Pause(
                                () -> robot.turnTable.getNumberOfBalls() == 0
                        ),
                        new Pause(
                                1000
                        )
                ),
                //INTAKE_THIRD_SET
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Delay(
                                () -> followerManager.follower.getCurrentTValue() > 0.5,
                                () -> robot.turnTable.intake()
                        ).setMaxTimeToRun(1500),
                        new Pause.OffsetCanceledPause (
                                robot.turnTable::full,
                                () -> followerManager.finished(true),
                                1500
                        )
                ),
                //SHOOT_THIRD_SET
                new DelayChain(
                        new Command(
                                followerManager::followNextChain
                        ),
                        new Delay(
                                () -> followerManager.finished(true),
                                robot::shootAll
                        ).setMaxTimeToRun(3000),
                        new Pause(
                                () -> robot.turnTable.getNumberOfBalls() == 0
                        ),
                        new Pause(
                                1000
                        )
                ),
                new DelayChain(
                        new Command(followerManager::followNextChain)
                )

                 */
        );
    }
}
