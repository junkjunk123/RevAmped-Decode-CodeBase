package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import static org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.DecodeAutonomousMethods.build;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.ValuePrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Command;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.DelayChain;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Pause;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.LM0AutoPaths;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.ThreeBallPath;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterState;

import java.util.function.DoubleSupplier;

@Autonomous
public class RedLM0Auto extends DecodeRevAmpedOpMode {
    private Robot robot;
    private FollowerManager followerManager;
    private Prompter prompter;
    private RevAmpedGamepad gamepad_1;
    private double delay;
    //private DecodeLimelight limelight;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Red);
        followerManager = new FollowerManager(hardwareMap, new ThreeBallPath());
        //limelight = new DecodeLimelight(hardwareMap);
        build(robot, () -> delay, followerManager);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        prompter = new Prompter(this, gamepad_1)
                .prompt("delay", new ValuePrompt("Choose delay in seconds", 18, 2));
        robot.turnTable.fillWithPurple();
    }

    @Override
    public void RevAmpedInitLoop() {
        prompter.run();
    }

    @Override
    public void RevAmpedStart() {
        delay = 1000 * prompter.getOrDefault("delay", 18);
        scheduler.run("ian loves jasmine");
    }

    @Override
    public void RevAmpedLoop() {}

    @Override
    public void update() {
        followerManager.update();
        robot.update();
        //limelight.update();
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
}