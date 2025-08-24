package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateFirstPreloadDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateInitialPreloadDeposit;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSecondPreloadDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSubCycleDepo;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateSubmersibleIntake;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods.generateThirdSampleDepo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SampleAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import java.util.Arrays;

@Autonomous(name = "RedAuto(JW=OP)",group = "Game")
public class RedAuto extends ITDRevAmpedAutonomous{
    @Override
    public void setPaths() {
        paths = new SampleAutoPaths();
    }

    @Override
    public void buildAutoActions() {
        autoActions = Arrays.asList(generateInitialPreloadDeposit(), generateFirstPreloadDepo(), generateSecondPreloadDepo(), generateThirdSampleDepo(),
                generateSubmersibleIntake(), generateSubCycleDepo(), generateSubmersibleIntake(), generateSubCycleDepo(),
                generateSubmersibleIntake(), generateSubCycleDepo(), generateSubmersibleIntake(), generateSubCycleDepo(), generateSubmersibleIntake());
    }

    @Override
    public void setRobot() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.OpModeType.SAMPLE_AUTO, RobotRevampedITD.AllianceColor.Red, SampleAutoPaths.startPose);
    }
}