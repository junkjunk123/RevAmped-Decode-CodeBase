package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.SampleAutonomousActions;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.FollowerManagerSampleAuto;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ITDWebcamNew;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

@Disabled
@Autonomous(name = "RedMTISampleAuto",group = "Game")
public class RedMTIAuto extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    private Follower follower;
    private ITDWebcamNew limelight;
    private SampleAutonomousActions pathActions;
    private FollowerManagerSampleAuto followerManager;
    private ElapsedTime overallTimer = new ElapsedTime();

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.SAMPLE_AUTO;
    }

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.Red);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        limelight = new ITDWebcamNew(hardwareMap, "limelight",telemetry);
        depositionArm = new DepositionArm(robot, scheduler.delays, false);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        followerManager = new FollowerManagerSampleAuto(follower);
        pathActions = new SampleAutonomousActions(robot, followerManager, scheduler, intakeFourbar, depositionArm, telemetry);
        robot.init();
    }

    @Override
    public void RevAmpedLoop() {
        if (pathActions.update(overallTimer)) {
            setNextState();
        }
    }

    @Override
    public void update() {
        telemetry.addData("tvalue",follower.getCurrentTValue());
        telemetry.addData("headingError",follower.getHeadingError());
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
        limelight.pause();
        AutoTeleopCommunication.setStartPose(follower.getPose());
        scheduler.clear();
        robot.close();
    }

    private void setAutoState(SampleAutonomousActions.AutoState autoState) {
        scheduler.clear();
        followerManager.followPath(autoState);
        pathActions.setAutoState(autoState);
    }

    private void setNextState() {
        setAutoState(pathActions.getNextState());
    }
}