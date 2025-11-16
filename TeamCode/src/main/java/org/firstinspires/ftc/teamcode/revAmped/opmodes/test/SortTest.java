package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Disabled
@Config
@TeleOp
public class SortTest extends DecodeRevAmpedOpMode {
    private DecodeColorSensor.ArtifactColor[] artifactColors = new DecodeColorSensor.ArtifactColor[3];
    private Robot robot;
    public static Globals.RandomizationState motif = Globals.RandomizationState.PPG;
    private int index = -1;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this);
    }

    @Override
    public void RevAmpedStart() {
        robot.update();
        List<Types.TableState2> tableStates = List.of(Types.TableState2.BALL1, Types.TableState2.BALL2, Types.TableState2.BALL3);
        ColorManager colorManager = robot.intakeColor();
        artifactColors[1] = colorManager.getCompartmentOneColor();
        artifactColors[2] = colorManager.getCompartmentTwoColor();

        if (artifactColors[1] == DecodeColorSensor.ArtifactColor.GREEN || artifactColors[2] == DecodeColorSensor.ArtifactColor.GREEN)
            artifactColors[0] = DecodeColorSensor.ArtifactColor.PURPLE;
        else artifactColors[0] = DecodeColorSensor.ArtifactColor.GREEN;

        int greenIndex = motif.getGreenIndex();
        int curGreenIndex = 0;
        for (int i = 0; i < 3; i++)
            if (artifactColors[i] == DecodeColorSensor.ArtifactColor.GREEN)
                curGreenIndex = i;
        index = tableStates.indexOf(robot.tableServo().getCurrentState()) - greenIndex + curGreenIndex;
        index = (index + 3) % 3;

        robot.tableServo().setCurrentState(tableStates.get(index));
    }

    @Override
    public void RevAmpedLoop() {
    }

    @Override
    public void update() {
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
