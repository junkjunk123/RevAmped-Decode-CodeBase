package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousEnumeratedAction;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServoV2;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.vision.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

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
    public abstract void setRobot(Runnable initFollower);

    TableServoV2 tableServo;
    Flywheel flywheel;

    public Robot.OpModeType getOpModeType() {
        if (robot == null) setRobot(() -> followerManager = new FollowerManager(hardwareMap, paths));
        return robot.getOpModeType();
    }

    public void RevAmpedInit() {
        Globals.randomizationState = null;
        setPaths();

        tableServo = new TableServoV2(hardwareMap, true, Types.TableState2.BALL2, new Encoder() {
            @Override
            public int getPosition() {
                return 0;
            }

            @Override
            public double getVelocity() {
                return 0;
            }
        });

        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);

        setRobot(() -> followerManager = new FollowerManager(hardwareMap, paths));

        if (DecodeLimelight.usingLimelight)
            DAM.limelight = new DecodeLimelight(hardwareMap);

        DecodeAutonomousActionExecutor.DecodeAutonomousParameters parameters =
                new DecodeAutonomousActionExecutor.DecodeAutonomousParameters(robot, followerManager);

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

            if (tableServo.getCurrentState() == Types.TableState2.BALL2)
                tableServo.setCurrentState(Types.TableState2.BALL2_END);
            else
                tableServo.resetToNeutral();
        }
    }

    @Override
    public void update() {
        if (Globals.randomizationState != null)
            telemetry.addData("motif", Globals.randomizationState);
        else telemetry.addData("motif detected", false);
        telemetry.addData("heading",followerManager.follower.getHeading());
        telemetry.update();
        followerManager.update();
        flywheel.update();
        //robot.update();
    }

    @Override
    public void RevAmpedStart() {
        setAutoState(0);
        aee.overallTimer.reset();
        flywheel.run(Flywheel.FLYWHEEL_FAR_VELOCITY + 75);
    }

    @Override
    public void RevAmpedStop() {
        scheduler.clear();
        TeleOpDrivetrainHandler.setInitialPose(followerManager.follower.getPose());
        DAM.close();
    }

    protected void setAutoState(int autoState) {
        scheduler.clear();
        followerManager.followPath(autoState);
        aee.setAutoState(autoState);
    }

    protected void setNextState() {
        int current = aee.getAutoState();
        int next = aee.getAutoStateAction(current).nextStateGenerator().nextState(current);

        if (next > aee.getSize()) {
            finished = true;
            return;
        }

        setAutoState(next);
    }
}
