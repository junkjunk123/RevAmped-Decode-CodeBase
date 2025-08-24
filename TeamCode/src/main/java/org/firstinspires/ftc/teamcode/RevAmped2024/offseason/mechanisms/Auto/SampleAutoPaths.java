package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;

import java.util.Collections;
import java.util.List;

import revAmped.components.Pedro.PathSupplier;

public class SampleAutoPaths implements PathSupplier {
    private Pose startPos = new Pose();
    private Pose firstPose = new Pose();
    private Pose secondPose = new Pose();

    @Override
    public Pose getStartPose() {
        return startPos;
    }

    @Override
    public List<PathChain> buildPaths() {


        return Collections.emptyList();
    }
}
