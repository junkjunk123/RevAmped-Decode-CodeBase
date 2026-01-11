package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.vision.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.CloseAutoPaths;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.ParkAutoPaths;

import java.util.Arrays;

@Autonomous
public class BlueParkAuto extends DecodeRevAmpedAutonomous {

    @Override
    public void setPaths() {
        paths = new ParkAutoPaths();
    }

    @Override
    public void buildAutoActions() {
    }

    @Override
    public void RevAmpedInitLoop() {

    }

    @Override
    public void setRobot(Runnable initFollower) {
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO, initFollower);

    }

    @Override
    public void RevAmpedStop() {
        super.RevAmpedStop();
        OpModeCommunication.turretPos = robot.turretMotor().getCurrentPosition();
    }
}
