package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import java.util.List;

@Config
public class LM1AutoPaths implements PathSupplier {
    public static Pose START_POSE = new Pose(34.5, 136, Math.toRadians(-90));
    public static Pose SHOOT_PRELOADS = new Pose(50, 96, Math.toRadians(-135));
    public static Pose FIRST_INTAKE_START = new Pose(40, 84, Math.toRadians(180));
    public static Pose FIRST_INTAKE_END = new Pose(20, 84, Math.toRadians(180));
    public static Pose OPEN_GATE = new Pose(15, 73, Math.toRadians(180));
    public static Pose FIRST_SHOOT = new Pose(50, 96, Math.toRadians(-135));
    public static Pose SECOND_INTAKE_START = new Pose(40, 61, Math.toRadians(180));
    public static Pose SECOND_INTAKE_END = new Pose(20, 61, Math.toRadians(180));
    public static Pose SECOND_SHOOT = new Pose(50, 96, Math.toRadians(-135));
    public static Pose THIRD_INTAKE_START = new Pose(40, 35.5, Math.toRadians(180));
    public static Pose THIRD_INTAKE_END = new Pose(20, 35.5, Math.toRadians(180));
    public static Pose THIRD_SHOOT = new Pose(50, 96, Math.toRadians(-135));
    public static Pose PARK = new Pose(50, 75, Math.toRadians(-135));

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;

        PathChain initialShoot = follower.pathBuilder()
                .addPath(new BezierLine(START_POSE, SHOOT_PRELOADS))
                .setLinearHeadingInterpolation(START_POSE.getHeading(), SHOOT_PRELOADS.getHeading(), 0.8)
                .build();
        PathChain intakeFirstSet = follower.pathBuilder()
                .addPath(new BezierLine(SHOOT_PRELOADS, FIRST_INTAKE_START))
                .setLinearHeadingInterpolation(SHOOT_PRELOADS.getHeading(), FIRST_INTAKE_START.getHeading(), 0.5)
                .addPath(new BezierLine(FIRST_INTAKE_START, FIRST_INTAKE_END))
                .setConstantHeadingInterpolation(FIRST_INTAKE_END.getHeading())
                .build();
        PathChain openGate = follower.pathBuilder()
                .addPath(new BezierLine(FIRST_INTAKE_END, OPEN_GATE))
                .setConstantHeadingInterpolation(OPEN_GATE.getHeading())
                .build();
        PathChain shootFirstSet = follower.pathBuilder()
                .addPath(new BezierLine(OPEN_GATE, FIRST_SHOOT))
                .setLinearHeadingInterpolation(OPEN_GATE.getHeading(), FIRST_SHOOT.getHeading(), 0.8)
                .build();
        PathChain intakeSecondSet = follower.pathBuilder()
                .addPath(new BezierLine(FIRST_SHOOT, SECOND_INTAKE_START))
                .setLinearHeadingInterpolation(FIRST_SHOOT.getHeading(), SECOND_INTAKE_START.getHeading(), 0.8)
                .addPath(new BezierLine(SECOND_INTAKE_START, SECOND_INTAKE_END))
                .setConstantHeadingInterpolation(SECOND_INTAKE_END.getHeading())
                .build();
        PathChain shootSecondSet = follower.pathBuilder()
                .addPath(new BezierLine(SECOND_INTAKE_END, SECOND_SHOOT))
                .setLinearHeadingInterpolation(SECOND_INTAKE_END.getHeading(), SECOND_SHOOT.getHeading(), 0.8)
                .build();
        PathChain intakeThirdSet = follower.pathBuilder()
                .addPath(new BezierLine(SECOND_SHOOT, THIRD_INTAKE_START))
                .setLinearHeadingInterpolation(SECOND_SHOOT.getHeading(), THIRD_INTAKE_START.getHeading(), 0.8)
                .addPath(new BezierLine(THIRD_INTAKE_START, THIRD_INTAKE_END))
                .setConstantHeadingInterpolation(THIRD_INTAKE_END.getHeading())
                .build();
        PathChain shootThirdSet = follower.pathBuilder()
                .addPath(new BezierLine(THIRD_INTAKE_END, THIRD_SHOOT))
                .setLinearHeadingInterpolation(THIRD_INTAKE_END.getHeading(), THIRD_SHOOT.getHeading(), 0.8)
                .build();
        PathChain park = follower.pathBuilder()
                .addPath(new BezierLine(THIRD_SHOOT, PARK))
                .setConstantHeadingInterpolation(THIRD_SHOOT.getHeading())
                .build();

        return List.of(initialShoot, intakeFirstSet, openGate, shootFirstSet, intakeSecondSet, shootSecondSet, intakeThirdSet, shootThirdSet, park);
    }
}
