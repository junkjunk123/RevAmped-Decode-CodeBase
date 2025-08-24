package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import java.util.Arrays;
import java.util.List;
import revAmped.components.PedroEnhancements.PathSupplier;

public class SampleAutoPaths implements PathSupplier {
    public static Pose startPose = new Pose(8.8, 118.7, Math.toRadians(315));
    public static Pose firstSampleIntake = new Pose(17.25,129.5, Math.toRadians(-17.5));
    public static Pose secondSampleIntake = new Pose(19, 133.5, Math.toRadians(-7));
    public static Pose thirdSampleIntake = new Pose(20, 135.5, Math.toRadians(13));
    public static Pose firstSubIntake = new Pose(59.7, 93.75, Math.toRadians(270));
    public static Pose firstSubDepo = new Pose(20.3, 129.5, Math.toRadians(-26));

    @Override
    public Pose getStartPose() {
        return startPose;
    }

    public List<PathChain> buildPaths(Follower follower) {
        PathChain scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromSub1, depoFromSub1, intakeFromSub2, depoFromSub2, intakeFromSub3, depoFromSub3, intakeFromSub4, depoFromSub4, intakeFromSub5, depoFromSub5;
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
                .setPathEndTimeoutConstraint(0)
                .addPath(new BezierPoint(firstSubIntake))
                .setPathEndTimeoutConstraint(50)
                .setConstantHeadingInterpolation(Math.toRadians(270))
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        depoFromSub1 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(60.3, 107.3, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 6, firstSubDepo.getY() + 2, firstSubDepo.getHeading())))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub2 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.9, 109.6, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 1, firstSubIntake.getY())))
                .setLinearHeadingInterpolation(depoFromSub1.getPath(depoFromSub1.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTimeoutConstraint(50)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        depoFromSub2 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(59.7, 113.3, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 5, firstSubDepo.getY() + 3)))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub3 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(57.8, 112.9, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 6, firstSubIntake.getY())))
                .setLinearHeadingInterpolation(depoFromSub2.getPath(depoFromSub2.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTranslationalConstraint(50)
                .setGlobalDeceleration(0.5)
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        depoFromSub3 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(56.8, 116.2, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 4, firstSubDepo.getY() + 2)))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub4 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.3, 107.3, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX(), firstSubIntake.getY() + 3.5)))
                .setLinearHeadingInterpolation(depoFromSub3.getPath(depoFromSub3.size() - 1).getHeadingGoal(1), Math.toRadians(270), 0.75)
                .setPathEndTranslationalConstraint(50)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(4.5)
                .build();
        depoFromSub4 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubIntake, new Pose(60.3, 107.3, firstSubDepo.getHeading()), new Pose(firstSubDepo.getX() - 4, firstSubDepo.getY() + 4)))
                .setReversed(true)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration(0.2)
                .setZeroPowerAccelerationMultiplier(5.5)
                .build();
        intakeFromSub5 = follower.pathBuilder()
                .addPath(new BezierCurve(firstSubDepo, new Pose(60.9, 109.6, firstSubIntake.getHeading()), new Pose(firstSubIntake.getX() + 3, firstSubIntake.getY() - 2)))
                .setTangentHeadingInterpolation()
                .setGlobalDeceleration()
                .setPathEndTranslationalConstraint(50)
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
        return Arrays.asList(scorePreload, scoreFirstSample, scoreSecondSample, scoreThirdSample, intakeFromSub1, depoFromSub1, intakeFromSub2, depoFromSub2, intakeFromSub3, depoFromSub3, intakeFromSub4, depoFromSub4, intakeFromSub5, depoFromSub5);
    }
}
