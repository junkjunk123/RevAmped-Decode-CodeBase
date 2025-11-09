package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.LM1AutoPaths;
import java.util.List;

public class LM1RedAuto extends DecodeRevAmpedAutonomous {
    @Override
    public void setPaths() {
        paths = new LM1AutoPaths();
    }

    @Override
    public void buildAutoActions() {
        autoActions = List.of(DAM.shootPreloads(), DAM.intake(), DAM.openGate(), DAM.shoot(),
                DAM.intake(), DAM.shoot(), DAM.intake(), DAM.shoot(), DAM.park());
    }

    @Override
    public void setRobot() {
        robot = new Robot(this, Globals.AllianceColor.Red);
    }

    @Override
    public void RevAmpedStop() {
        super.RevAmpedStop();
        OpModeCommunication.turntablePos = robot.tableServo().getCurrentPosition();
        OpModeCommunication.turretPos = robot.turretMotor().getCurrentPosition();
    }
}
