package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AEABuilder;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.FarAutoPaths;

import java.util.List;

@Autonomous
public class RedFarAutoUndelayed extends DecodeRevAmpedAutonomous{
    @Override
    public void setPaths() {
        paths = new FarAutoPaths(false);
    }

    @Override
    public void buildAutoActions() {
        autoActions = List.of(DAM.shootPreloadsFar(), AEABuilder.drive(f -> DAM.notBusy()),
                AEABuilder.drive(f -> f.velocityCondition(4)),
                DAM.intakeFar(), DAM.shootFar(), DAM.park());
    }

    @Override
    public void setRobot() {
        DecodeLimelight.usingLimelight = true;
        robot = new Robot(this, Globals.AllianceColor.Red, Robot.OpModeType.AUTO);
    }
}
