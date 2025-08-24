package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Future;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.FollowerManagerSampleAuto;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.SampleAutonomousActions;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import revAmped.components.PedroEnhancements.Tree.CommandTree;
import revAmped.components.PedroEnhancements.Tree.CommandTreeBuilder;

@Disabled
@Autonomous(name = "TreeSampleAuto",group = "Game")
public class TreeSampleAuto extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    private CommandTreeBuilder builder;
    private CommandTree tree;
    private Follower follower;
    private FollowerManagerSampleAuto followerManager;
    private ElapsedTime overallTimer = new ElapsedTime();

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.SAMPLE_AUTO;
    }

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.Blue);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        depositionArm = new DepositionArm(robot, scheduler.delays, false);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Blue);
        followerManager = new FollowerManagerSampleAuto(follower);
        robot.init();
        tree = buildTree();
    }

    @Override
    public void RevAmpedLoop() {
        tree.update();
    }

    @Override
    public void update() {
        telemetry.update();
        robot.update();
        followerManager.update();
    }

    @Override
    public void RevAmpedStart() {
        followerManager.followPath(SampleAutonomousActions.AutoState.DEPOSIT_PRELOAD);
        overallTimer.reset();
    }

    @Override
    public void RevAmpedStop() {
        AutoTeleopCommunication.setStartPose(follower.getPose());
        scheduler.clear();
        robot.close();
    }

    public CommandTree buildTree() {
        builder = new CommandTreeBuilder(scheduler, follower);
        return builder.build();
    }
}