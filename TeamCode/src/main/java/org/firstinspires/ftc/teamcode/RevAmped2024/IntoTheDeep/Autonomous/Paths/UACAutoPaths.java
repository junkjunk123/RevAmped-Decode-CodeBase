package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import java.util.Arrays;
import java.util.List;
import revAmped.components.PedroEnhancements.PathSupplier;

public class UACAutoPaths implements PathSupplier {
    Pose startPose;
    Pose firstSampleIntake;
    Pose secondSampleIntake;
    Pose thirdSampleIntake;
    Pose HPDepo = new Pose(8.5, 124.5, Math.toRadians(270));
    Pose HPIntake = new Pose(8.5, 51.5, Math.toRadians(270));
    Pose finalPreloadPartnerSide = new Pose(23.5, 22.5, Math.toRadians(317));
    SampleAutoPaths preloads = new SampleAutoPaths();

    @Override
    public Pose getStartPose() {
        return preloads.getStartPose();
    }

    public List<PathChain> buildPaths(Follower follower) {
        startPose = preloads.startPose;
        firstSampleIntake = preloads.firstSampleIntake;
        secondSampleIntake = preloads.secondSampleIntake;
        thirdSampleIntake = preloads.thirdSampleIntake;
        PathChain scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromHP1, depoFromHP, intakeFromHP2, intakePartnerPreload, depoPartnerPreload;
        scorePreload = follower.pathBuilder()
                .addPath(new Path(new BezierLine(startPose, firstSampleIntake)))
                .setLinearHeadingInterpolation(startPose.getHeading(), firstSampleIntake.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(100)
                .build();
        scoreFirstSample = follower.pathBuilder()
                .addPath(new Path(new BezierLine(firstSampleIntake, secondSampleIntake)))
                .setLinearHeadingInterpolation(firstSampleIntake.getHeading(), secondSampleIntake.getHeading(), 0.6)
                .setPathEndTimeoutConstraint(100)
                .build();
        scoreSecondSample = follower.pathBuilder()
                .addPath(new BezierPoint(thirdSampleIntake))
                .setConstantHeadingInterpolation(thirdSampleIntake.getHeading())
                .setPathEndTimeoutConstraint(0)
                .build();
        scoreThirdSample = follower.pathBuilder()
                .addPath(new BezierPoint(thirdSampleIntake))
                .setConstantHeadingInterpolation(thirdSampleIntake.getHeading())
                .setPathEndTimeoutConstraint(0)
                .build();
        intakeFromHP1 = follower.pathBuilder()
                .addPath(new Path(new BezierLine(thirdSampleIntake, new Pose(10, 115, Math.toRadians(270)))))
                .setLinearHeadingInterpolation(scoreThirdSample.getPath(0).getHeadingGoal(1), HPIntake.getHeading())
                .setPathEndTimeoutConstraint(0)
                .addPath(new Path(new BezierLine(new Pose(10, 115, 0), HPIntake)))
                .setConstantHeadingInterpolation(HPIntake.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .setGlobalDeceleration()
                .build();
        intakeFromHP2 = follower.pathBuilder()
                .addPath(new Path(new BezierLine(HPDepo, new Pose(HPIntake.getX(), HPIntake.getY() + 2))))
                .setConstantHeadingInterpolation(HPIntake.getHeading())
                .setZeroPowerAccelerationMultiplier(5)
                .setGlobalDeceleration()
                .build();
        depoFromHP = follower.pathBuilder()
                .addPath(new Path(new BezierLine(HPIntake, HPDepo)))
                .setConstantHeadingInterpolation(HPDepo.getHeading())
                .setPathEndTimeoutConstraint(100)
                .build();

        Pose modifiedHPIntake = new Pose(HPIntake.getX(), HPIntake.getY() + 6, HPIntake.getHeading());
        intakePartnerPreload = follower.pathBuilder()
                .addPath(new Path(new BezierLine(HPDepo, modifiedHPIntake)))
                .setConstantHeadingInterpolation(modifiedHPIntake.getHeading())
                .setZeroPowerAccelerationMultiplier(4)
                .setPathEndTimeoutConstraint(100)
                .addPath(new Path(new BezierLine(modifiedHPIntake, finalPreloadPartnerSide)))
                .setLinearHeadingInterpolation(modifiedHPIntake.getHeading(), finalPreloadPartnerSide.getHeading())
                .setGlobalDeceleration(0.5)
                .build();
        depoPartnerPreload = follower.pathBuilder()
                .addPath(new Path(new BezierLine(finalPreloadPartnerSide, modifiedHPIntake)))
                .setLinearHeadingInterpolation(finalPreloadPartnerSide.getHeading(), modifiedHPIntake.getHeading())
                .setPathEndTimeoutConstraint(100)
                .addPath(new Path(new BezierLine(modifiedHPIntake, HPDepo)))
                .setConstantHeadingInterpolation(HPDepo.getHeading())
                .setZeroPowerAccelerationMultiplier(4)
                .build();
        return Arrays.asList(scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromHP1, depoFromHP, intakeFromHP2, depoFromHP, intakePartnerPreload, depoPartnerPreload);
    }
}
