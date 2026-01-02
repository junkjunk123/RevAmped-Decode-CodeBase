package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AEABuilder;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.ValuePrompt;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.vision.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths.FarAutoPaths;

import java.util.List;

@Autonomous
public class BlueFarAuto extends DelayedAuto {
    private boolean useLimelight;
    String[] paths = new String[] {"ObeliskDetection, ShootPreloads, CornerOne, CornerTwo, Intake, Shoot, Park"};

    @Override
    public void setPaths() {
        super.paths = new FarAutoPaths(useLimelight);
    }

    @Override
    public void buildAutoActions() {
        autoActions = List.of(DAM.shootPreloadsFar(), AEABuilder.drive(f -> DAM.notBusy()),
                AEABuilder.drive(f -> f.velocityCondition(4)),
                DAM.intakeFar(), DAM.shootFar(), DAM.park());
    }

    @Override
    public void setRobot(Runnable initFollower) {
        DecodeLimelight.usingLimelight = useLimelight;
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO, initFollower);
    }

    @Override
    public void initPrompter(Double[] delays) {
        prompter.prompt("useLimelight", new OptionPrompt<>("useLimelight", useLimelight, false, true));

        for (String path : paths)
            prompter.prompt(path, new ValuePrompt(path, 0, 0.5));

        prompter.thenDisplay("completed with manual input");
    }

    @Override
    public void extractValuesFromPrompter(Double[] delays) {
        useLimelight = prompter.getOrDefault("useLimelight", true);

        if (useLimelight)
            delays = new Double[paths.length];
        else
            delays = new Double[paths.length - 2];

        for (int i = 0; i < delays.length; i++) {
            if (useLimelight)
                delays[i] = prompter.getOrDefault(paths[i], 0.0);
            else
                delays[i] = prompter.getOrDefault(paths[i+2], 0.0);
        }
    }
}
