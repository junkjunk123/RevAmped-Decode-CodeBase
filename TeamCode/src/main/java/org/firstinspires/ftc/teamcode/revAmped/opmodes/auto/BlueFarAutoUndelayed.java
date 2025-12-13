package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AEABuilder;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.FarAutoPaths;

import java.util.List;

@Autonomous
public class BlueFarAutoUndelayed extends DecodeRevAmpedAutonomous{
    @Override
    public void setPaths() {
        paths = new FarAutoPaths(false);
    }

    @Override
    public void buildAutoActions() {
        autoActions = List.of(DAM.shootPreloadsFar(), DAM.driveToIntake(), DAM.intakeFar().setMillisToDrive(3000),
                AEABuilder.drive(f -> f.velocityCondition(4)).setMillisToDrive(1500),
                AEABuilder.drive(f -> f.velocityCondition(4)).setMillisToDrive(1500),
                DAM.shootFar().setMillisToDrive(2000), DAM.park());
    }

    @Override
    public void setRobot() {
        DecodeLimelight.usingLimelight = false;
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO);
    }
}
