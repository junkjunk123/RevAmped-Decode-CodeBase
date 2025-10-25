package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Command;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.DelayChain;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Pause;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.LM0AutoPaths;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterState;

@Autonomous
public class RedLM0Auto extends DecodeRevAmpedOpMode {
    private Robot robot;
    private FollowerManager followerManager;
    private DecodeLimelight limelight;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Red);
        followerManager = new FollowerManager(hardwareMap, new LM0AutoPaths());
        limelight = new DecodeLimelight(hardwareMap);
        build();
    }

    @Override
    public void RevAmpedStart() {
        scheduler.run("ian loves jasmine");
    }

    @Override
    public void RevAmpedLoop() {}

    @Override
    public void update() {
        followerManager.update();
        robot.update();
        limelight.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.AUTO;
    }

    @Override
    public void RevAmpedStop() {
        scheduler.clear();
        TeleOpDrivetrainHandler.setInitialPose(followerManager.follower.getPose());
        Robot.AUTONOMOUS_INSTANCE = robot;
    }

    private void build() {
        new DelayChain("ian loves jasmine",
                //Shoot Preloads
                new DelayChain(
                        new Command(robot::shootAll),
                        new Delay(
                                Popper.POP_TIME_MILLIS,
                                () -> robot.shooter.setCurrentState(Types.ShooterState.REST)
                        )
                ),
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
                                600
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
                        new Delay(
                                Popper.POP_TIME_MILLIS,
                                () -> robot.shooter.setCurrentState(ShooterState.REST)
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
                                600
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
                        new Delay(
                                Popper.POP_TIME_MILLIS,
                                () -> robot.shooter.setCurrentState(ShooterState.REST)
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
                                600
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
                        new Delay(
                                Popper.POP_TIME_MILLIS,
                                () -> robot.shooter.setCurrentState(ShooterState.REST)
                        )
                ),
                new DelayChain(
                        new Command(followerManager::followNextChain)
                )
        );
    }
}