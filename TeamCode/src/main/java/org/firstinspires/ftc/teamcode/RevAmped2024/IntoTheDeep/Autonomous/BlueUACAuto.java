package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateFirstPreloadDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateHPDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateHPIntake;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateInitialPreloadDeposit;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generatePartnerPreloadIntake;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSecondPreloadDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateThirdSampleDepo;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.UACAutoPaths;
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

@Autonomous(name = "BlueUACAuto",group = "Game")
public class BlueUACAuto extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private DepositionArm depositionArm;
    private IntakeFourbar intakeFourbar;
    private Follower follower;
    private FollowerManager followerManager;
    private ITDAutonomousActionExecutor actions;
    private UACAutoPaths pathSupplier;

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
        pathSupplier = new UACAutoPaths();
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Blue);
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
                actions = Arrays.asList(generateInitialPreloadDeposit(), generateFirstPreloadDepo(), generateSecondPreloadDepo(), generateThirdSampleDepo(),
                        generateHPIntake(), generateHPDepo(), generateHPIntake(), generateHPDepo(), generatePartnerPreloadIntake(), generateHPDepo());
            }
        };
        actions.build();
    }

    @Override
    public void RevAmpedLoop() {
        if (actions.update()) {
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
        actions.setAutoState(autoState);
    }

    private void setNextState() {
        int current = AutonomousActionExecutor.getAutoState();
        int next = actions.getAutoStateAction(current).nextStateGenerator().nextState(current);
        setAutoState(next);
    }
}