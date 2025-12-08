package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AEABuilder;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.ValuePrompt;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.paths.FarAutoPaths;
import java.util.Collections;

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
        if (useLimelight)
            autoActions.add(DAM.obeliskDetectionFar());
        Collections.addAll(autoActions, DAM.obeliskDetectionFar(), DAM.shootPreloadsFar(),
                AEABuilder.drive(f -> DAM.notBusy()),
                AEABuilder.drive(f -> f.velocityCondition(4)),
                DAM.intakeFar(), DAM.shootFar(), DAM.park());
    }

    @Override
    public void setRobot() {
        DecodeLimelight.usingLimelight = useLimelight;
        robot = new Robot(this, Globals.AllianceColor.Blue, Robot.OpModeType.AUTO);
    }

    @Override
    public void initPrompter(Double[] delays) {
        prompter.prompt("useLimelight", new OptionPrompt<>("useLimelight", useLimelight));

        for (String path : paths)
            prompter.prompt(path, new ValuePrompt(path, 0, 0.5));

        prompter.thenDisplay("completed with manual input");
    }

    @Override
    public void extractValuesFromPrompter(Double[] delays) {
        useLimelight = prompter.getOrDefault("useLimelight", false);

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
