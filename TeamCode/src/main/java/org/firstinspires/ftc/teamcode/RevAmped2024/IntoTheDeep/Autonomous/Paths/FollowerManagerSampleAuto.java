package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.SampleAutonomousActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FollowerManagerSampleAuto {
    private Follower follower;
    private Pose startPose = new Pose(8.8, 118.7, Math.toRadians(315));
    private Pose firstSampleIntake = new Pose(17.25,129.5, Math.toRadians(-15.5));
    private Pose secondSampleIntake = new Pose(20, 133.5, Math.toRadians(-7));
    private Pose thirdSampleIntake = new Pose(20, 135.5, Math.toRadians(13));
    private Pose firstSubIntake = new Pose(59.7, 93.75, Math.toRadians(270));
    private Pose firstSubDepo = new Pose(19.3, 130.5, Math.toRadians(-26));
    private PathChain scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromSub1, depoFromSub1, intakeFromSub2, depoFromSub2, intakeFromSub3, depoFromSub3, intakeFromSub4, depoFromSub4, intakeFromSub5, depoFromSub5;
    private List<PathChain> pathChains = new ArrayList<>();
    private int currentChainIndex = 0;

    public FollowerManagerSampleAuto(Follower follower) {
        this.follower = follower;
        follower.setStartingPose(startPose);
        buildPaths();
        pathChains = Arrays.asList(scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromSub1, depoFromSub1, intakeFromSub2, depoFromSub2, intakeFromSub3, depoFromSub3, intakeFromSub4, depoFromSub4, intakeFromSub5, depoFromSub5);
    }

    public void buildPaths() {
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
                .setConstantHeadingInterpolation(Math.toRadians(-4))
                .setPathEndTimeoutConstraint(0)
                .build();
        intakeFromSub1 = follower.pathBuilder()
                .addPath(new BezierCurve(thirdSampleIntake, new Pose(59.7, 113.3, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX(), firstSubIntake.getY())))
                .setTangentHeadingInterpolation()
                .setPathEndTimeoutConstraint(100)
                .addPath(new BezierPoint(firstSubIntake))
                .setPathEndTranslationalConstraint(200)
                .setConstantHeadingInterpolation(Math.toRadians(270))
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        depoFromSub1 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(60.3, 107.3, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 3, firstSubDepo.getY() + 3, firstSubDepo.getHeading())))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub2 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.9, 109.6, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 3, firstSubIntake.getY() + 2.5)))
                .setLinearHeadingInterpolation(depoFromSub1.getPath(depoFromSub1.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTranslationalConstraint(100)
                .setGlobalDeceleration(0.5)
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        depoFromSub2 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(59.7, 113.3, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 3, firstSubDepo.getY() + 4)))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        intakeFromSub3 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(57.8, 112.9, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 6, firstSubIntake.getY() - 1.5)))
                .setLinearHeadingInterpolation(depoFromSub2.getPath(depoFromSub2.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTranslationalConstraint(100)
                .setGlobalDeceleration(0.5)
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        depoFromSub3 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(56.8, 116.2, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 3, firstSubDepo.getY() + 4)))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub4 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.3, 107.3, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX(), firstSubIntake.getY() + 3)))
                .setLinearHeadingInterpolation(depoFromSub3.getPath(depoFromSub3.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTranslationalConstraint(100)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(3.5)
                .build();
        depoFromSub4 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(60.3, 107.3, firstSubDepo.getHeading()), firstSubDepo))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub5 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.9, 109.6, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 3, firstSubIntake.getY() - 2)))
                .setTangentHeadingInterpolation()
                .setGlobalDeceleration()
                .setPathEndTranslationalConstraint(100)
                .addPath(new BezierPoint(new Pose(firstSubIntake.getX() + 3, firstSubIntake.getY())))
                .setPathEndTranslationalConstraint(200)
                .setConstantHeadingInterpolation(Math.toRadians(270))
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        depoFromSub5 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(60.7, 109.8, firstSubDepo.getHeading()), firstSubDepo))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
    }

    public PathChain getPathChain(SampleAutonomousActions.AutoState autoState) {
        return pathChains.get(autoState.ordinal());
    }

    public int getPathChainSize() {
        return pathChains.size();
    }

    public void update() {
        follower.update();
    }

    public void followPath(SampleAutonomousActions.AutoState autoState, boolean holdEnd) {
        currentChainIndex = autoState.ordinal();
        follower.followPath(pathChains.get(currentChainIndex), holdEnd);
    }

    public void followPath(PathChain pathChain, boolean holdEnd) {
        follower.followPath(pathChain, holdEnd);
    }

    public void followPath(Path path, boolean holdEnd) {
        follower.followPath(path, holdEnd);
    }

    public void followPath(SampleAutonomousActions.AutoState autoState) {
        followPath(autoState, true);
    }

    public int currentPathChainIndex() {
        return currentChainIndex;
    }

    public void followNextChain() {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), true);
        }
    }

    public void followNextChain(boolean holdEnd) {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), holdEnd);
        }
    }

    public PathChain getCurrentPathChain() {
        return pathChains.get(currentChainIndex);
    }

    public int getCurrentChainIndex() {
        return currentChainIndex;
    }

    public double distanceFromTarget() {
        return new Point(follower.getPose()).distanceFrom(follower.getCurrentPathChain().getLastPoint());
    }

    public Follower getFollower() {
        return follower;
    }
}
