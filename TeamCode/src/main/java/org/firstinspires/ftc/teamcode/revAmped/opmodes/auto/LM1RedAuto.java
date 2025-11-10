package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.LM1AutoPaths;
import java.util.Arrays;

@Autonomous
public class LM1RedAuto extends DecodeRevAmpedAutonomous {
    @Override
    public void setPaths() {
        paths = new LM1AutoPaths();
    }

    @Override
    public void buildAutoActions() {
        autoActions = Arrays.asList(DAM.shootPreloads(), DAM.intake(), DAM.openGate(), DAM.shoot(),
                DAM.intake(), DAM.shoot(), DAM.intake(), DAM.shoot(), DAM.park());
    }

    @Override
    public void RevAmpedInitLoop() {
        telemetry.addData("curPose", followerManager.follower.getPose());
        telemetry.update();
    }

    @Override
    public void setRobot() {
        DecodeLimelight.usingLimelight = false;
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO);

    }

    @Override
    public void RevAmpedStop() {
        super.RevAmpedStop();
        OpModeCommunication.turntablePos = robot.tableServo().getCurrentPosition();
        OpModeCommunication.turretPos = robot.turretMotor().getCurrentPosition();
    }
}
