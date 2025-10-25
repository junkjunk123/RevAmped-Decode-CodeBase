package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.ColoredDecodePose;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

public class ThreeBallFarPath implements PathSupplier {
    public static ColoredDecodePose PRE_POSE = new ColoredDecodePose(64.50, 7.7, Math.toRadians(-90));
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(64.50, 136, Math.toRadians(-90));
    public static ColoredDecodePose NEXT_POSE = new ColoredDecodePose(56, 56, Math.toRadians(-90));

    @Override
    public Pose getStartPose() {
        return PRE_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;
        PathChain initial = follower.pathBuilder()
                .addPath(new BezierLine(PRE_POSE, START_POSE))
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .build();
        PathChain pathChain = follower.pathBuilder()
                .addPath(new BezierLine(START_POSE, NEXT_POSE))
                .setConstantHeadingInterpolation(Math.toRadians(-90))
                .setBrakingStrength(0.75)
                .build();
        return List.of(initial, pathChain);
    }
}
