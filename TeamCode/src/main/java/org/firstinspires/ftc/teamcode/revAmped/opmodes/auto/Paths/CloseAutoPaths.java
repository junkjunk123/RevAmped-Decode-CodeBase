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
public class CloseAutoPaths implements PathSupplier {
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(34.5, 136, Math.toRadians(-90));
    public static ColoredDecodePose SHOOT_PRELOADS = new ColoredDecodePose(60, 102, Math.toRadians(249));
    public static ColoredDecodePose FIRST_INTAKE_START = new ColoredDecodePose(56, 83, Math.toRadians(180));
    public static ColoredDecodePose FIRST_INTAKE_END = new ColoredDecodePose(28, 83, Math.toRadians(180));
    public static ColoredDecodePose FIRST_SHOOT = new ColoredDecodePose(60, 102, Math.toRadians(240));
    public static ColoredDecodePose SECOND_INTAKE_START = new ColoredDecodePose(53, 62, Math.toRadians(180));
    public static ColoredDecodePose SECOND_INTAKE_END = new ColoredDecodePose(25, 52, Math.toRadians(180));
    public static ColoredDecodePose SECOND_SHOOT = new ColoredDecodePose(55, 97, Math.toRadians(240));
    public static ColoredDecodePose THIRD_INTAKE_START = new ColoredDecodePose(69, 33, Math.toRadians(180));
    public static ColoredDecodePose THIRD_INTAKE_END = new ColoredDecodePose(28, 33, Math.toRadians(180));
    public static ColoredDecodePose THIRD_SHOOT = new ColoredDecodePose(66, 108, Math.toRadians(240));
    public static ColoredDecodePose PARK = new ColoredDecodePose(52, 120, Math.toRadians(240));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain initialShoot = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE, SHOOT_PRELOADS))
                .setConstantHeadingInterpolation(SHOOT_PRELOADS.getHeading())
                .build();
        PathChain intakeFirstSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_PRELOADS, FIRST_INTAKE_START))
                .setLinearHeadingInterpolation(SHOOT_PRELOADS.getHeading(), FIRST_INTAKE_END.getHeading(), 0.8)
                .addPath(ColoredDecodePose.makeBezier(FIRST_INTAKE_START, FIRST_INTAKE_END))
                .setConstantHeadingInterpolation(FIRST_INTAKE_END.getHeading())
                .addParametricCallback(0, () -> follower.setMaxPower(0.5))
                .addParametricCallback(0.95, () -> follower.setMaxPower(1.0))
                .build();
        PathChain shootFirstSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(FIRST_INTAKE_END, FIRST_SHOOT))
                .setConstantHeadingInterpolation(FIRST_SHOOT.getHeading())
                .build();
        PathChain intakeSecondSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(FIRST_SHOOT, SECOND_INTAKE_START))
                .setLinearHeadingInterpolation(FIRST_SHOOT.getHeading(), SECOND_INTAKE_END.getHeading(), 0.8)
                .setVelocityConstraint(0.5)
                .addPath(ColoredDecodePose.makeBezier(SECOND_INTAKE_START, SECOND_INTAKE_END))
                .setConstantHeadingInterpolation(SECOND_INTAKE_END.getHeading())
                .addParametricCallback(0, () -> follower.setMaxPower(0.5))
                .addParametricCallback(0.95, () -> follower.setMaxPower(1.0))
                .setVelocityConstraint(0.5)
                .setGlobalDeceleration()
                .build();
        PathChain shootSecondSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SECOND_INTAKE_END, SECOND_SHOOT))
                .setConstantHeadingInterpolation(SECOND_SHOOT.getHeading())
                .setVelocityConstraint(0.5)
                .build();
        PathChain intakeThirdSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SECOND_SHOOT, THIRD_INTAKE_START))
                .setLinearHeadingInterpolation(SECOND_SHOOT.getHeading(), THIRD_INTAKE_END.getHeading(), 0.8)
                .addPath(ColoredDecodePose.makeBezier(THIRD_INTAKE_START, THIRD_INTAKE_END))
                .setConstantHeadingInterpolation(THIRD_INTAKE_END.getHeading())
                .addParametricCallback(0, () -> follower.setMaxPower(0.5))
                .addParametricCallback(0.95, () -> follower.setMaxPower(1.0))
                .setGlobalDeceleration(0.75)
                .build();
        PathChain shootThirdSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(THIRD_INTAKE_END, THIRD_SHOOT))
                .setConstantHeadingInterpolation(THIRD_SHOOT.getHeading())
                .setGlobalDeceleration(0.75)
                .setVelocityConstraint(0.5)
                .build();
        PathChain park = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(THIRD_SHOOT, PARK))
                .setConstantHeadingInterpolation(THIRD_SHOOT.getHeading())
                .build();
        return List.of(initialShoot, intakeFirstSet, shootFirstSet, intakeSecondSet, shootSecondSet, intakeThirdSet, shootThirdSet, park);
    }
}
