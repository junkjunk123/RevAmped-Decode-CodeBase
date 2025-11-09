package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.ColoredDecodePose;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;

import java.util.Collections;
import java.util.List;

@Config
public class LM1AutoPaths implements PathSupplier {
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(34.5, 136, Math.toRadians(-90));
    public static ColoredDecodePose SHOOT_PRELOADS = new ColoredDecodePose(50, 96, Math.toRadians(135));
    public static ColoredDecodePose FIRST_INTAKE_START = new ColoredDecodePose(40, 84, Math.toRadians(180));
    public static ColoredDecodePose FIRST_INTAKE_END = new ColoredDecodePose(20, 84, Math.toRadians(180));
    public static ColoredDecodePose OPEN_GATE = new ColoredDecodePose(15, 73, Math.toRadians(180));
    public static ColoredDecodePose FIRST_SHOOT = new ColoredDecodePose(50, 96, Math.toRadians(135));
    public static ColoredDecodePose SECOND_INTAKE_START = new ColoredDecodePose(40, 61, Math.toRadians(180));
    public static ColoredDecodePose SECOND_INTAKE_END = new ColoredDecodePose(20, 61, Math.toRadians(180));
    public static ColoredDecodePose SECOND_SHOOT = new ColoredDecodePose(50, 96, Math.toRadians(135));
    public static ColoredDecodePose THIRD_INTAKE_START = new ColoredDecodePose(40, 35.5, Math.toRadians(180));
    public static ColoredDecodePose THIRD_INTAKE_END = new ColoredDecodePose(20, 35.5, Math.toRadians(180));
    public static ColoredDecodePose THIRD_SHOOT = new ColoredDecodePose(50, 96, Math.toRadians(135));
    public static ColoredDecodePose PARK = new ColoredDecodePose(50, 75, Math.toRadians(135));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain initialShoot = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE, SHOOT_PRELOADS))
                .setLinearHeadingInterpolation(START_POSE.heading(), SHOOT_PRELOADS.heading(), 0.8)
                .build();
        PathChain intakeFirstSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_PRELOADS, FIRST_INTAKE_START))
                .setLinearHeadingInterpolation(SHOOT_PRELOADS.heading(), FIRST_INTAKE_START.heading(), 0.5)
                .addPath(ColoredDecodePose.makeBezier(FIRST_INTAKE_START, FIRST_INTAKE_END))
                .setConstantHeadingInterpolation(FIRST_INTAKE_END.heading())
                .build();
        PathChain openGate = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(FIRST_INTAKE_END, OPEN_GATE))
                .setConstantHeadingInterpolation(OPEN_GATE.heading())
                .build();
        PathChain shootFirstSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(OPEN_GATE, FIRST_SHOOT))
                .setLinearHeadingInterpolation(OPEN_GATE.heading(), FIRST_SHOOT.heading(), 0.8)
                .build();
        PathChain intakeSecondSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(FIRST_SHOOT, SECOND_INTAKE_START))
                .setLinearHeadingInterpolation(FIRST_SHOOT.heading(), SECOND_INTAKE_START.heading(), 0.8)
                .addPath(ColoredDecodePose.makeBezier(SECOND_INTAKE_START, SECOND_INTAKE_END))
                .setConstantHeadingInterpolation(SECOND_INTAKE_END.heading())
                .build();
        PathChain shootSecondSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SECOND_INTAKE_END, SECOND_SHOOT))
                .setLinearHeadingInterpolation(SECOND_INTAKE_END.heading(), SECOND_SHOOT.heading(), 0.8)
                .build();
        PathChain intakeThirdSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SECOND_SHOOT, THIRD_INTAKE_START))
                .setLinearHeadingInterpolation(SECOND_SHOOT.heading(), THIRD_INTAKE_START.heading(), 0.8)
                .addPath(ColoredDecodePose.makeBezier(THIRD_INTAKE_START, THIRD_INTAKE_END))
                .setConstantHeadingInterpolation(THIRD_INTAKE_END.heading())
                .build();
        PathChain shootThirdSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(THIRD_INTAKE_END, THIRD_SHOOT))
                .setLinearHeadingInterpolation(THIRD_INTAKE_END.heading(), THIRD_SHOOT.heading(), 0.8)
                .build();
        PathChain park = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(THIRD_SHOOT, PARK))
                .setConstantHeadingInterpolation(THIRD_SHOOT.heading())
                .build();

        return List.of(initialShoot, intakeFirstSet, openGate, shootFirstSet, intakeSecondSet, shootSecondSet, intakeThirdSet, shootThirdSet, park);
    }
}
