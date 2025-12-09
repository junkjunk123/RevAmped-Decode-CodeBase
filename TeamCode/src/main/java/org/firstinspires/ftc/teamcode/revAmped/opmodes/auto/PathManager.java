package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;

import java.util.Iterator;

public class PathManager {
    private final Pose startPose;
    private final Iterator<PathChain> paths;

    public PathManager(PathSupplier pathSupplier) {
        this.startPose = pathSupplier.getStartPose();
        this.paths = pathSupplier.buildPaths().iterator();
    }

    public Pose getStartPose() {
        return startPose;
    }

    public PathChain next() {
        if (paths.hasNext())
            return paths.next();
        return null;
    }
}
