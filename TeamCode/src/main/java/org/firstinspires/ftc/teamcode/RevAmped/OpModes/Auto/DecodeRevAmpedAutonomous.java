package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto;

import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped.Robot;

import java.util.List;

import revAmped.components.Architecture.AutonomousEnumeratedAction;
import revAmped.components.Pedro.FollowerManager;
import revAmped.components.Pedro.PathSupplier;

public abstract class DecodeRevAmpedAutonomous extends DecodeRevAmpedOpMode {
    protected Robot robot;
    private FollowerManager followerManager;
    private DecodeAutonomousActionExecutor aee;
    protected PathSupplier paths;
    protected List<AutonomousEnumeratedAction> autoActions;

    public abstract void setPaths();
    public abstract void buildAutoActions();
    public abstract void setRobot();

    public Robot.OpModeType getOpModeType() {
        if (robot == null) setRobot();
        return robot.getOpModeType();
    }

    public void RevAmpedInit() {
        setRobot();
        setPaths();

        followerManager = new FollowerManager(hardwareMap, paths);

        DecodeAutonomousActionExecutor.DecodeAutonomousParameters parameters = new DecodeAutonomousActionExecutor.DecodeAutonomousParameters(robot, followerManager);

        aee = new DecodeAutonomousActionExecutor(parameters) {
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
        aee.overallTimer.reset();
    }

    @Override
    public void RevAmpedStop() {
        scheduler.clear();
    }

    private void setAutoState(int autoState) {
        scheduler.clear();
        followerManager.followPath(autoState);
        aee.setAutoState(autoState);
    }

    private void setNextState() {
        int current = aee.getAutoState();
        int next = aee.getAutoStateAction(current).nextStateGenerator().nextState(current);
        setAutoState(next);
    }
}
