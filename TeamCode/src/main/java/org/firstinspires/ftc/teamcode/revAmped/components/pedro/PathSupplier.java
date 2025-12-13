package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import java.util.ArrayList;
import java.util.List;

/**
 * PathSupplier is an interface that provides methods to get the starting pose and build paths.
 * It is used to supply paths for autonomous actions in a robot's autonomous mode.
 */
public interface PathSupplier {
    /**
     * Returns the starting pose of the robot.
     * @return the starting Pose
     */
    Pose getStartPose();

    /**
     * Builds and returns a list of PathChain objects that represent the paths to be followed.
     * @return a List of PathChain objects
     */
    List<PathChain> buildPaths();

    default PlayingField getPlayingField() {
        return null;
    }

    default List<Boolean> holdEnd() {
        return null;
    }

    static ArrayList<Pose> mirror(boolean mirrorX, boolean mirrorY, ArrayList<Pose> points) {
        ArrayList<Pose> point = new ArrayList<>(points);
        for (int i = 0; i < points.size(); i++) {
            Pose pose = points.get(i);
            double x = pose.getX();
            double y = pose.getY();

            if (mirrorX) x = 144 - x;
            if (mirrorY) y = 144 - y;

            point.set(i, new Pose(x, y, mirrorX || mirrorY ? pose.getHeading() : Math.PI + pose.getHeading()));
        }

        return point;
    }

    static ArrayList<Double> mirrorInterpolations(boolean mirrorX, boolean mirrorY, ArrayList<Double> headingInterpolations) {
        ArrayList<Double> headingInterpol = new ArrayList<>(headingInterpolations);
        for (int i = 0; i < headingInterpolations.size(); i++) {
            double interpol = headingInterpolations.get(i);
            if (mirrorX || mirrorY) interpol += Math.PI;
            headingInterpol.set(i, interpol);
        }

        return headingInterpol;
    }
}
