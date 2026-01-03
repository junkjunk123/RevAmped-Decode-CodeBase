package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.ColoredDecodePose;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Configurable
public class FarAutoPathsV2 implements PathSupplier {
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(55, 7.5, Math.PI);
    public static ColoredDecodePose SHOOT_POSE = new ColoredDecodePose(57, 20, Math.PI);
    public static ColoredDecodePose INTAKE_PRELOADS_1 = new ColoredDecodePose(9.5, 7, Math.toRadians(190));
    public static ColoredDecodePose INTAKE_PRELOADS_2_START = new ColoredDecodePose(37, 34, Math.PI);
    public static ColoredDecodePose INTAKE_PRELOADS_2 = new ColoredDecodePose(9, 36, Math.PI);
    public static ColoredDecodePose FAR_INTAKE_REPEAT = new ColoredDecodePose(10, 20, Math.PI);
    public static ColoredDecodePose PARK = new ColoredDecodePose(30, 13);

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;
        List<PathChain> paths = new ArrayList<>();

        PathChain initialShoot = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE))
                .setConstantHeadingInterpolation(START_POSE.getHeading())
                .build();

        PathChain firstPreloadIntake = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE, INTAKE_PRELOADS_1))
                .setConstantHeadingInterpolation(INTAKE_PRELOADS_1.getHeading())
                .setGlobalDeceleration(0.75)
                .build();

        PathChain firstPreloadShoot = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_PRELOADS_1, SHOOT_POSE))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .setGlobalDeceleration()
                .build();

        Supplier<PathChain> secondPreloadIntake = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_POSE, INTAKE_PRELOADS_2_START))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .addPath(ColoredDecodePose.makeBezier(INTAKE_PRELOADS_2_START, INTAKE_PRELOADS_2))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .setGlobalDeceleration(0.75)
                ::build;

        Supplier<PathChain> secondPreloadShoot = () -> follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_PRELOADS_2, SHOOT_POSE))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .setGlobalDeceleration()
                .build();

        Supplier<PathChain> farIntake = () -> follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_POSE, FAR_INTAKE_REPEAT))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .setGlobalDeceleration(0.75)
                .build();

        Supplier<PathChain> farShoot = () -> follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(FAR_INTAKE_REPEAT, SHOOT_POSE))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .setGlobalDeceleration()
                .build();

        PathChain park = follower.pathBuilder()
                .addPath(new BezierLine(follower::getPose, PARK.getPose()))
                .setConstantHeadingInterpolation(SHOOT_POSE.getHeading())
                .build();

        paths.add(initialShoot);
        paths.add(firstPreloadIntake);
        paths.add(firstPreloadShoot);
        paths.add(secondPreloadIntake.get());
        paths.add(secondPreloadShoot.get());

        for (int i = 0; i < 19; i++) {
            paths.add(farIntake.get());
            paths.add(farShoot.get());
            paths.add(secondPreloadIntake.get());
            paths.add(secondPreloadShoot.get());
        }

        paths.add(park); //index 81
        return paths;
    }
}
