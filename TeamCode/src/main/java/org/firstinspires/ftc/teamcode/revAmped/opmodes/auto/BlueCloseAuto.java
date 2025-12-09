package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.CloseAutoPaths;

import java.util.Arrays;

@Autonomous
public class BlueCloseAuto extends DecodeRevAmpedAutonomous {

    @Override
    public void setPaths() {
        paths = new CloseAutoPaths();
    }

    @Override
    public void buildAutoActions() {
        autoActions = Arrays.asList(DAM.shootPreloads(), DAM.intake(), DAM.shoot(),
                DAM.intake(), DAM.shoot(), DAM.intake(), DAM.shoot(), DAM.park());
    }

    @Override
    public void RevAmpedInitLoop() {

    }

    @Override
    public void setRobot() {
        DecodeLimelight.usingLimelight = true;
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO);

    }

    @Override
    public void RevAmpedStop() {
        super.RevAmpedStop();
        OpModeCommunication.turretPos = robot.turretMotor().getCurrentPosition();
    }
}
