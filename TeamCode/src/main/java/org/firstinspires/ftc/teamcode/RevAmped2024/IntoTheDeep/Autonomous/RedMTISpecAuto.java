package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateIntakeSpecPreload;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateOuttakeSpecPreload;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generatePark;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generatePreloadSpecDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSpecDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSpecIntake;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SpecAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDAutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;
import java.util.Arrays;
import revAmped.components.PedroEnhancements.AutonomousActionExecutor;
import revAmped.components.PedroEnhancements.FollowerManager;

@Autonomous(name = "BigRedSp*cs",group = "Game")
public class RedMTISpecAuto extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    private Follower follower;
    private FollowerManager followerManager;
    private ITDAutonomousActionExecutor actions;
    private SpecAutoPaths pathSupplier;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.SPECIMEN_AUTO;
    }

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.Red);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        depositionArm = new DepositionArm(robot, scheduler.delays, false);
        intakeFourbar = new IntakeFourbar(robot, scheduler.delays);
        pathSupplier = new SpecAutoPaths();
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        robot.init();

        followerManager = new FollowerManager(follower) {
            @Override
            public void buildPaths() {
                pathChains = pathSupplier.buildPaths(follower);
            }

            @Override
            public void setStartingPose() {
                follower.setStartingPose(pathSupplier.getStartPose());
            }
        };

        ITDAutonomousActionExecutor.ITDAutonomousParameters parameters = new ITDAutonomousActionExecutor.ITDAutonomousParameters(robot, followerManager, scheduler, intakeFourbar, depositionArm, telemetry);

        actions = new ITDAutonomousActionExecutor(parameters) {
            @Override
            public void buildActions() {
                actions = Arrays.asList(generatePreloadSpecDepo(), generateIntakeSpecPreload(), generateOuttakeSpecPreload(), generateIntakeSpecPreload(),
                        generateOuttakeSpecPreload(), generateIntakeSpecPreload(), generateOuttakeSpecPreload(), generateSpecIntake(), generateSpecDepo(),
                        generateSpecIntake(), generateSpecDepo(), generateSpecIntake(), generateSpecDepo(), generateSpecIntake(), generateSpecDepo(), generatePark());
            }
        };
        actions.build();
    }

    @Override
    public void RevAmpedLoop() {
        boolean update = actions.update();
        if (update) {
            setNextState();
        }
        telemetry.addData("complete",update);
    }

    @Override
    public void update() {
        telemetry.addData("state", AutonomousActionExecutor.getAutoState());
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
        actions.setAutoState(autoState);
    }

    private void setNextState() {
        setAutoState(AutonomousActionExecutor.getAutoState() + 1);
    }
}