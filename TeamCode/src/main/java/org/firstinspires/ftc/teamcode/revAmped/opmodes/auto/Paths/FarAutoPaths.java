package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.Paths;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.ColoredDecodePose;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathSupplier;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configurable
public class FarAutoPaths implements PathSupplier {
    private final boolean detectObelisk;

    public static ColoredDecodePose START_POSE = new ColoredDecodePose(55, 7.5, Math.PI);
    public static ColoredDecodePose DETECT_OBELISK = new ColoredDecodePose(55, 60, -Math.PI / 2);
    public static ColoredDecodePose INTAKE_START = new ColoredDecodePose(12.5, 23, Math.toRadians(205));
    public static ColoredDecodePose INTAKE_END = new ColoredDecodePose(9.5, 16, Math.toRadians(205));
    public static ColoredDecodePose INTAKE_RETRIAL = new ColoredDecodePose(20, 9, Math.toRadians(180));
    public static ColoredDecodePose SHOOT_POSE = new ColoredDecodePose(55, 7.5, Math.PI);
    public static ColoredDecodePose PARK = new ColoredDecodePose(12, 9, Math.PI);

    public FarAutoPaths(boolean detectObelisk) {
        this.detectObelisk = detectObelisk;
    }

    @Override
    public Pose getStartPose() {
        return START_POSE.getPose();
    }

    @Override
    public List<PathChain> buildPaths() {
        Follower follower = Globals.followerManager.follower;
        List<PathChain> pathChains = new ArrayList<>();

        if (detectObelisk) {
            PathChain detectObelisk = follower.pathBuilder()
                    .addPath(new BezierLine(START_POSE, DETECT_OBELISK))
                    .setConstantHeadingInterpolation(DETECT_OBELISK.getHeading())
                    .build();

            PathChain returnFromObelisk = follower.pathBuilder()
                    .addPath(new BezierLine(DETECT_OBELISK, START_POSE))
                    .setConstantHeadingInterpolation(DETECT_OBELISK.getHeading())
                    .build();

            pathChains.add(detectObelisk);
            pathChains.add(returnFromObelisk);
        } else {
            PathChain shootInitial = follower.pathBuilder()
                    .addPath(new BezierPoint(START_POSE))
                    .setConstantHeadingInterpolation(START_POSE.getHeading())
                    .build();
            pathChains.add(shootInitial);
        }

        PathChain cornerTwo = follower.pathBuilder()
                .addPath(new BezierLine(START_POSE, INTAKE_START))
                .setLinearHeadingInterpolation(START_POSE.getHeading(), INTAKE_START.getHeading(), 0.8)
                .addParametricCallback(0.5, () -> Robot.INSTANCE.intakeMotor().setCurrentState(Types.IntakeState.INTAKE))
                .build();

        PathChain intake = follower.pathBuilder()
                .addPath(new BezierLine(INTAKE_START, INTAKE_END))
                .setConstantHeadingInterpolation(INTAKE_END.getHeading())
                .build();

        PathChain intakeRetrial = follower.pathBuilder()
                .addPath(new BezierLine(INTAKE_END, INTAKE_RETRIAL))
                .setConstantHeadingInterpolation(INTAKE_RETRIAL.getHeading())
                .build();

        PathChain intakeRetrial2 = follower.pathBuilder()
                .addPath(new BezierLine(INTAKE_RETRIAL, PARK))
                .setConstantHeadingInterpolation(PARK.getHeading())
                .build();

        PathChain shoot = follower.pathBuilder()
                .addPath(new BezierLine(PARK, SHOOT_POSE))
                .setLinearHeadingInterpolation(INTAKE_END.getHeading(), SHOOT_POSE.getHeading(), 0.8)
                .build();

        PathChain park = follower.pathBuilder()
                .addPath(new BezierLine(SHOOT_POSE, PARK))
                .setLinearHeadingInterpolation(SHOOT_POSE.getHeading(), PARK.getHeading())
                .build();

        Collections.addAll(pathChains, cornerTwo, intake, intakeRetrial, intakeRetrial2, shoot, park);
        return pathChains;
    }
}
