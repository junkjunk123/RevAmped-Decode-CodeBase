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
public class LM0AutoPaths implements PathSupplier {
    public static ColoredDecodePose START_POSE = new ColoredDecodePose(22.460, 119.469, Math.toRadians(-90));
    public static ColoredDecodePose WAYPOINT_1 = new ColoredDecodePose(14.800, 94.600);
    public static ColoredDecodePose INTAKE_FIRST_ELEMENT_SET = new ColoredDecodePose(24.250, 85.250, Math.toRadians(-65));
    public static ColoredDecodePose OPEN_GATE = new ColoredDecodePose(12.250, 70.000, Math.toRadians(180));
    public static ColoredDecodePose SHOOT_FIRST_ELEMENT_SET = new ColoredDecodePose(48.750, 85.250);
    public static ColoredDecodePose WAYPOINT_2 = new ColoredDecodePose(49.341, 55.640);
    public static ColoredDecodePose INTAKE_SECOND_ELEMENT_SET = new ColoredDecodePose(24.050, 60.050, Math.toRadians(-90));
    public static ColoredDecodePose SHOOT_SECOND_ELEMENT_SET = new ColoredDecodePose(48.750, 85.250, Math.toRadians(-180));
    public static ColoredDecodePose WAYPOINT_3 = new ColoredDecodePose(16.560, 44.000);
    public static ColoredDecodePose INTAKE_THIRD_ELEMENT_SET = new ColoredDecodePose(24.000, 39.750, Math.toRadians(-70));
    public static ColoredDecodePose WAYPOINT4 = new ColoredDecodePose(16.560, 44.000);
    public static ColoredDecodePose SHOOT_THIRD_ELEMENT_SET = new ColoredDecodePose(48.750, 85.250, Math.toRadians(-180));
    public static ColoredDecodePose PARK = new ColoredDecodePose(48, 52, Math.toRadians(180));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain intakeFirstElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(START_POSE, WAYPOINT_1, INTAKE_FIRST_ELEMENT_SET))
                .setLinearHeadingInterpolation(START_POSE.heading(), INTAKE_FIRST_ELEMENT_SET.heading(), 0.8)
                .setGlobalDeceleration()
                .build();
        PathChain openGate = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_FIRST_ELEMENT_SET, OPEN_GATE))
                .setLinearHeadingInterpolation(INTAKE_FIRST_ELEMENT_SET.heading(), OPEN_GATE.heading())
                .setGlobalDeceleration()
                .build();
        PathChain shootFirstElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_FIRST_ELEMENT_SET, SHOOT_FIRST_ELEMENT_SET))
                .setTangentHeadingInterpolation()
                .setReversed()
                .setGlobalDeceleration()
                .build();
        PathChain intakeSecondElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_FIRST_ELEMENT_SET, WAYPOINT_2, INTAKE_SECOND_ELEMENT_SET))
                .setLinearHeadingInterpolation(shootFirstElementSet.lastPath().getHeadingGoal(1.0), INTAKE_SECOND_ELEMENT_SET.heading(), 0.8)
                .setGlobalDeceleration()
                .build();
        PathChain shootSecondElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_SECOND_ELEMENT_SET, SHOOT_SECOND_ELEMENT_SET))
                .setTangentHeadingInterpolation()
                .setGlobalDeceleration()
                .build();
        PathChain intakeThirdElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_SECOND_ELEMENT_SET, WAYPOINT_3, INTAKE_THIRD_ELEMENT_SET))
                .setLinearHeadingInterpolation(shootSecondElementSet.lastPath().getHeadingGoal(1.0), INTAKE_THIRD_ELEMENT_SET.heading())
                .setGlobalDeceleration()
                .build();
        PathChain shootThirdElementSet = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(INTAKE_THIRD_ELEMENT_SET, SHOOT_THIRD_ELEMENT_SET))
                .setTangentHeadingInterpolation()
                .setGlobalDeceleration()
                .build();
        PathChain park = follower.pathBuilder()
                .addPath(ColoredDecodePose.makeBezier(SHOOT_THIRD_ELEMENT_SET, PARK))
                .setGlobalDeceleration()
                .setLinearHeadingInterpolation(SHOOT_THIRD_ELEMENT_SET.heading(), PARK.heading())
                .build();

        return List.of(intakeFirstElementSet, openGate, shootFirstElementSet, intakeSecondElementSet, shootSecondElementSet, intakeThirdElementSet, shootThirdElementSet, park);
    }
}
