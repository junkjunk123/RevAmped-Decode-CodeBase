package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import java.util.List;

@Config
public class LM1AutoPaths implements PathSupplier {
    public static Pose START_POSE = new Pose(34.5, 136, Math.toRadians(-90));
    public static Pose SHOOT_PRELOADS = new Pose(60, 102, Math.toRadians(240));
    public static Pose FIRST_INTAKE_START = new Pose(59.5, 85.5, Math.toRadians(180));
    public static Pose FIRST_INTAKE_END = new Pose(28, 78, Math.toRadians(180));
    public static Pose FIRST_SHOOT = new Pose(60, 102, Math.toRadians(240));
    public static Pose SECOND_INTAKE_START = new Pose(65, 55, Math.toRadians(180));
    public static Pose SECOND_INTAKE_END = new Pose(20, 49, Math.toRadians(180));
    public static Pose SECOND_SHOOT = new Pose(60, 102, Math.toRadians(240));
    public static Pose THIRD_INTAKE_START = new Pose(60, 30, Math.toRadians(180));
    public static Pose THIRD_INTAKE_END = new Pose(24, 30, Math.toRadians(180));
    public static Pose THIRD_SHOOT = new Pose(60, 102, Math.toRadians(240));
    public static Pose PARK = new Pose(52, 120, Math.toRadians(240));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain initialShoot = follower.pathBuilder()
                .addPath(new BezierLine(START_POSE, SHOOT_PRELOADS))
                .setLinearHeadingInterpolation(START_POSE.getHeading(), SHOOT_PRELOADS.getHeading())
                .build();
        PathChain intakeFirstSet = follower.pathBuilder()
                .addPath(new BezierCurve(SHOOT_PRELOADS, FIRST_INTAKE_START, FIRST_INTAKE_END))
                .setLinearHeadingInterpolation(SHOOT_PRELOADS.getHeading(), FIRST_INTAKE_END.getHeading(), 0.8)
                .build();
        PathChain shootFirstSet = follower.pathBuilder()
                .addPath(new BezierLine(FIRST_INTAKE_END, FIRST_SHOOT))
                .setConstantHeadingInterpolation(FIRST_SHOOT.getHeading())
                .build();
        PathChain intakeSecondSet = follower.pathBuilder()
                .addPath(new BezierCurve(FIRST_SHOOT, SECOND_INTAKE_START, SECOND_INTAKE_END))
                .setLinearHeadingInterpolation(FIRST_SHOOT.getHeading(), SECOND_INTAKE_END.getHeading(), 0.8)
                .build();
        PathChain shootSecondSet = follower.pathBuilder()
                .addPath(new BezierLine(SECOND_INTAKE_END, SECOND_SHOOT))
                .setConstantHeadingInterpolation(SECOND_SHOOT.getHeading())
                .build();
        PathChain intakeThirdSet = follower.pathBuilder()
                .addPath(new BezierCurve(SECOND_SHOOT, THIRD_INTAKE_START, THIRD_INTAKE_END))
                .setLinearHeadingInterpolation(SECOND_SHOOT.getHeading(), THIRD_INTAKE_END.getHeading(), 0.8)
                .build();
        PathChain shootThirdSet = follower.pathBuilder()
                .addPath(new BezierLine(THIRD_INTAKE_END, THIRD_SHOOT))
                .setConstantHeadingInterpolation(THIRD_SHOOT.getHeading())
                .build();
        PathChain park = follower.pathBuilder()
                .addPath(new BezierLine(THIRD_SHOOT, PARK))
                .setConstantHeadingInterpolation(THIRD_SHOOT.getHeading())
                .build();
        return List.of(initialShoot, intakeFirstSet, shootFirstSet, intakeSecondSet, shootSecondSet, intakeThirdSet, shootThirdSet, park);
    }
}
