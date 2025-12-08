package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import java.util.List;

public class PathManager {
    private List<PathChain> chains;
    private Pose startingPose;
    private int index;

    public PathManager(PathSupplier supplier) {
        chains = supplier.buildPaths();
        startingPose = supplier.getStartPose();
        index = -1;
    }

    public PathChain next() {
        index++;
        return chains.get(index);
    }

    public Pose getStartingPose() {
        return startingPose;
    }
}
