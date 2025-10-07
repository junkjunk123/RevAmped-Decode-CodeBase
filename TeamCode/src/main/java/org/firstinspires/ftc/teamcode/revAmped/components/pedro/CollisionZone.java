package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;

public interface CollisionZone {
    Pose center();
    double minXDist();
    double minYDist();
    boolean in(Pose pose);
    boolean intersection(Pose current, Pose target);
    Pose safestPoint(double t, Pose pose, Path path);
}
