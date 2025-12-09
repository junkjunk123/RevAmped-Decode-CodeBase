package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import static org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.DAM.computeTableRotation;
import static org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.commands.ContinuationUtil.infinite;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.command;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.exec;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.jumpScope;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.loop;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.parallel;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.race;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.sequence;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.waitSeconds;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.waitUntil;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.CloseAutoPaths;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import dev.frozenmilk.dairy.mercurial.ftc.Mercurial;

@SuppressWarnings("unused")
public class MercurialAuto {
    public static final Mercurial.RegisterableProgram myFirstMercurialOpMode = Mercurial.autonomous(op -> {
        Scheduler delays = new Scheduler();
        Robot robot = new Robot(op.hardwareMap(), delays, Globals.AllianceColor.Red, Robot.OpModeType.AUTO);
        PathManager paths = new PathManager(new CloseAutoPaths());
        Follower follower = Constants.createFollower(op.hardwareMap());
        DecodeLimelight limelight = new DecodeLimelight(op.hardwareMap());

        op.scheduler().schedule(
                parallel(
                        infinite(op, () -> {
                                    follower.update();
                                    robot.update();
                                    delays.update();
                                }
                        ),
                        parallel(
                                race(
                                        exec(() -> follower.followPath(paths.next())),
                                        waitSeconds(5)
                                ),
                                jumpScope(jumpHandle ->
                                        sequence(
                                                race(
                                                        command()
                                                                .setInit(() -> {
                                                                    robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
                                                                    robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
                                                                    limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                                                                })
                                                                .setExecute(limelight::update)
                                                                .setEnd(() -> {
                                                                    robot.tableServo().setCurrentState(computeTableRotation());
                                                                    robot.turretMotor().setCurrentState(Types.TurretState.HOLD,
                                                                            TurretMotor.AUTO_PRELOADS);
                                                                })
                                                                .setFinished(() -> Globals.randomizationState != null),
                                                        sequence(
                                                                waitSeconds(3),
                                                                jumpHandle.jump()
                                                        )
                                                ),
                                                race(
                                                        waitUntil(robot.tableServo()::reached),
                                                        waitSeconds(0.65)
                                                ),
                                                waitSeconds(0.15),
                                                exec(() -> robot.popper().setCurrentState(Types.PopperState.POP)),
                                                waitSeconds(650),
                                                robot.turnTable.shootAllAuto()
                                        )
                                )
                        )
                )
                .close()
        );

        op.dropToScheduler();
    });
}
