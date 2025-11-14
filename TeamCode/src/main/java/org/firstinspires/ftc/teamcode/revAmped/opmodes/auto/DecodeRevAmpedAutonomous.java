package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousEnumeratedAction;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.List;

public abstract class DecodeRevAmpedAutonomous extends DecodeRevAmpedOpMode {
    protected Robot robot;
    protected FollowerManager followerManager;
    private DecodeAutonomousActionExecutor aee;
    protected PathSupplier paths;
    protected List<AutonomousEnumeratedAction> autoActions;
    protected boolean finished = false;

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

        if (DecodeLimelight.usingLimelight)
            DAM.limelight = new DecodeLimelight(hardwareMap);

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
        if (!finished && aee.update()) {
            setNextState();
        }
    }

    @Override
    public void update() {
        if (Globals.randomizationState != null)
            telemetry.addData("motif", Globals.randomizationState);
        else telemetry.addData("motif detected", false);
        telemetry.update();
        followerManager.update();
        robot.update();
    }

    @Override
    public void RevAmpedStart() {
        setAutoState(0);
        aee.overallTimer.reset();
    }

    @Override
    public void RevAmpedStop() {
        scheduler.clear();
        TeleOpDrivetrainHandler.setInitialPose(followerManager.follower.getPose());
        DAM.close();
    }

    private void setAutoState(int autoState) {
        scheduler.clear();
        followerManager.followPath(autoState);
        aee.setAutoState(autoState);
    }

    private void setNextState() {
        int current = aee.getAutoState();
        int next = aee.getAutoStateAction(current).nextStateGenerator().nextState(current);

        if (next > aee.getSize()) {
            finished = true;
            return;
        }

        setAutoState(next);
    }
}
