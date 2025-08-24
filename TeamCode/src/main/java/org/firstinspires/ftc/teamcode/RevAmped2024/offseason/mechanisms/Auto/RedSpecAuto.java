package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto.ITDSpecAutonomousMethods.generateInitialSpecDepo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;
import java.util.List;
import revAmped.components.Misc.Globals;

public class RedSpecAuto extends ITDRevAmpedAutonomous {
    @Override
    public void setPaths() {
        paths = new SpecAutoPaths();
    }

    @Override
    public void buildAutoActions() {
        autoActions = List.of(generateInitialSpecDepo());
    }

    @Override
    public void setRobot() {
        robot = new Robot(this, Globals.AllianceColor.Red, Robot.OpModeType.AUTO);
    }
}
