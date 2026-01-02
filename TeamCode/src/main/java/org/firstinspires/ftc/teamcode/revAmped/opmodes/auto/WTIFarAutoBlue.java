package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.vision.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.FarAutoPathsV2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.ArrayList;

@Autonomous
public class WTIFarAutoBlue extends DecodeRevAmpedAutonomous {
    @Override
    public void setPaths() {
        paths = new FarAutoPathsV2();
    }

    @Override
    public void buildAutoActions() {
        autoActions = new ArrayList<>();
        autoActions.add(DAM.preloadsWTIFar());

        for (int i = 0; i < 20; i++) {
            autoActions.add(DAM.intakeWTIFar());
            autoActions.add(DAM.shootWTIFar());
            autoActions.add(DAM.intakeWTIFar());
            autoActions.add(DAM.shootWTIFar());
        }

        autoActions.add(DAM.park()); //index 81
    }

    @Override
    public void setRobot(Runnable initFollower) {
        DecodeLimelight.usingLimelight = false;
        robot = new Robot(this, Globals.AllianceColor.Red, Robot.OpModeType.AUTO, initFollower);
    }

    @Override
    public void RevAmpedInit() {
        super.RevAmpedInit();
        robot.hoodServo().setCurrentState(Types.HoodState.FAR_PRESET);
    }
}
