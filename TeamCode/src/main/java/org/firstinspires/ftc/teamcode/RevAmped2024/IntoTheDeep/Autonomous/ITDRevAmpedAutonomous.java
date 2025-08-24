package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous;
import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDAutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import java.util.List;

import revAmped.components.PedroEnhancements.AutonomousActionExecutor;
import revAmped.components.PedroEnhancements.AutonomousEnumeratedAction;
import revAmped.components.PedroEnhancements.FollowerManager;
import revAmped.components.PedroEnhancements.PathSupplier;

public abstract class ITDRevAmpedAutonomous extends ITDRevAmpedOpMode {
    protected RobotRevampedITD robot;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    protected Follower follower;
    private FollowerManager followerManager;
    private ITDAutonomousActionExecutor aee;
    protected PathSupplier paths;
    protected List<AutonomousEnumeratedAction> autoActions;

    public abstract void setPaths();
    public abstract void buildAutoActions();
    public abstract void setRobot();

    public RobotRevampedITD.OpModeType getOpModeType() {
        if (robot == null) setRobot();
        return robot.getOpModeType();
    }

    public void RevAmpedInit() {
        setRobot();
        setPaths();
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        depositionArm = new DepositionArm(robot, scheduler.delays, false);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        robot.init();

        followerManager = new FollowerManager(follower) {
            @Override
            public void buildPaths() {
                pathChains = paths.buildPaths(follower);
            }

            @Override
            public void setStartingPose() {
                follower.setStartingPose(paths.getStartPose());
            }
        };

        ITDAutonomousActionExecutor.ITDAutonomousParameters parameters = new ITDAutonomousActionExecutor.ITDAutonomousParameters(robot, followerManager, scheduler, intakeFourbar, depositionArm, telemetry);

        aee = new ITDAutonomousActionExecutor(parameters) {
            @Override
            public void buildActions() {
                actions = autoActions;
            }
        };
        buildAutoActions();
        aee.build();
    }

    @Override
    public void RevAmpedLoop() {
        if (aee.update()) {
            setNextState();
        }
    }

    @Override
    public void update() {
        telemetry.update();
        robot.update();
        followerManager.update();
    }

    @Override
    public void RevAmpedStart() {
        setAutoState(0);
        AutonomousActionExecutor.overallTimer.reset();
    }

    @Override
    public void RevAmpedStop() {
        AutoTeleopCommunication.setStartPose(follower.getPose());
        scheduler.clear();
        robot.close();
    }

    private void setAutoState(int autoState) {
        scheduler.clear();
        followerManager.followPath(autoState);
        aee.setAutoState(autoState);
    }

    private void setNextState() {
        setAutoState(AutonomousActionExecutor.getAutoState() + 1);
    }
}
