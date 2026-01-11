package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.ColoredDecodePose;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;

import java.util.List;

@Config
public class ParkAutoPaths implements PathSupplier {
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(55, 7, Math.toRadians(180));
    public static ColoredDecodePose PARK_POSE = new ColoredDecodePose(36, 7, Math.toRadians(180));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain park = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE, PARK_POSE))
                .setConstantHeadingInterpolation(PARK_POSE.getHeading())
                .build();
        return List.of(park);
    }
}
