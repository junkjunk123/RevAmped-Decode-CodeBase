package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.PathChain;
import java.util.List;
import revAmped.components.PedroEnhancements.PathSupplier;

public class SpecAutoPaths implements PathSupplier {
    public static final Pose startPose = new Pose(6.45, 66, Math.toRadians(180));
    private final Pose initialSpecimenDepositPose = new Pose(33.5, 72, Math.toRadians(180));
    private final Pose firstSampleIntakePose = new Pose(26.5,45,Math.toRadians(306.5));
    private final Pose secondSampleIntakePose = new Pose(28.5,37,Math.toRadians(302.25));
    private final Pose thirdSampleIntakePose = new Pose(26.5,28,Math.toRadians(305));
    private final Pose intakeSpecPose = new Pose(10.5,28,Math.toRadians(285));
    private final Pose scorePose1 = new Pose(39.75,65,Math.toRadians(255));
    private final Pose scorePose2 = new Pose(40.5,63.5,Math.toRadians(255));
    private final Pose scorePose3 = new Pose(39.25,63,Math.toRadians(255));
    private final Pose scorePose4 = new Pose(40,60.5,Math.toRadians(255));
    
    @Override
    public Pose getStartPose() {
        return startPose;
    }

    @Override
    public List<PathChain> buildPaths(Follower follower) {
        PathChain presetScore, firstSampleIntake, firstSpecimenDeposit, secondSampleIntake, firstSpecimenIntake, secondSpecimenIntake, secondSpecimenDeposit, thirdSpecimenIntake, thirdSpecimenDeposit, thirdSampleIntake, fourthSpecimenIntake, fourthSpecimenDeposit, firstSampleDropToHumanPlayer, secondSampleDropToHumanPlayer, thirdSampleDropToHumanPlayer, park;
        presetScore = follower.pathBuilder()
                .addPath(new BezierLine(startPose, initialSpecimenDepositPose))
                .setConstantHeadingInterpolation(initialSpecimenDepositPose.getHeading())
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .setPathEndTimeoutConstraint(0)
                .build();
        firstSampleIntake = follower.pathBuilder()
                .addPath(new BezierPoint(firstSampleIntakePose))
                .setConstantHeadingInterpolation(firstSampleIntakePose.getHeading())
                .setPathEndTimeoutConstraint(100)
                .setZeroPowerAccelerationMultiplier(3)
                .build();
        firstSampleDropToHumanPlayer = follower.pathBuilder()
                .addPath(new BezierPoint(firstSampleIntakePose))
                .setConstantHeadingInterpolation(Math.toRadians(235))
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .build();
        secondSampleIntake = follower.pathBuilder()
                .addPath(new BezierLine(firstSampleIntakePose, secondSampleIntakePose))
                .setConstantHeadingInterpolation(secondSampleIntakePose.getHeading())
                .setPathEndTimeoutConstraint(100)
                .setZeroPowerAccelerationMultiplier(3)
                .build();
        secondSampleDropToHumanPlayer = follower.pathBuilder()
                .addPath(new BezierPoint(secondSampleIntakePose))
                .setConstantHeadingInterpolation(Math.toRadians(230))
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .build();
        thirdSampleIntake = follower.pathBuilder()
                .addPath(new BezierLine(secondSampleIntakePose, thirdSampleIntakePose))
                .setConstantHeadingInterpolation(thirdSampleIntakePose.getHeading())
                .setPathEndTimeoutConstraint(100)
                .setZeroPowerAccelerationMultiplier(3)
                .build();
        double thirdSampleDropHeading = Math.toRadians(215);
        thirdSampleDropToHumanPlayer = follower.pathBuilder()
                .addPath(new BezierPoint(thirdSampleIntakePose))
                .setConstantHeadingInterpolation(thirdSampleDropHeading)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration()
                .build();
        firstSpecimenIntake = follower.pathBuilder()
                .addPath(new BezierLine(thirdSampleIntakePose, new Pose(intakeSpecPose.getX() - 0.75, intakeSpecPose.getY())))
                .setConstantHeadingInterpolation(intakeSpecPose.getHeading())
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .setPathEndTimeoutConstraint(100)
                .build();
        firstSpecimenDeposit = follower.pathBuilder()
                .addPath(new BezierCurve(intakeSpecPose, scorePose1))
                .setLinearHeadingInterpolation(intakeSpecPose.getHeading(), scorePose1.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .build();
        secondSpecimenIntake = follower.pathBuilder()
                .addPath(new BezierCurve(scorePose1, new Pose(intakeSpecPose.getX() - 1, intakeSpecPose.getY(), intakeSpecPose.getHeading())))
                .setLinearHeadingInterpolation(scorePose1.getHeading(), intakeSpecPose.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration()
                .build();
        secondSpecimenDeposit = follower.pathBuilder()
                .addPath(new BezierCurve(intakeSpecPose, scorePose2))
                .setLinearHeadingInterpolation(intakeSpecPose.getHeading(), scorePose2.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .build();
        thirdSpecimenIntake = follower.pathBuilder()
                .addPath(new BezierCurve(scorePose2, new Pose(intakeSpecPose.getX() - 1, intakeSpecPose.getY(), intakeSpecPose.getHeading())))
                .setLinearHeadingInterpolation(scorePose2.getHeading(), intakeSpecPose.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(100)
                .setGlobalDeceleration()
                .build();
        thirdSpecimenDeposit = follower.pathBuilder()
                .addPath(new BezierCurve(intakeSpecPose, new Pose(scorePose3.getX() + 1, scorePose3.getY())))
                .setLinearHeadingInterpolation(intakeSpecPose.getHeading(), scorePose3.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .build();
        fourthSpecimenIntake = follower.pathBuilder()
                .addPath(new BezierCurve(scorePose3, new Pose(intakeSpecPose.getX() - 0.5, intakeSpecPose.getY() - 1, intakeSpecPose.getHeading())))
                .setLinearHeadingInterpolation(scorePose3.getHeading(), intakeSpecPose.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(100)
                .build();
        fourthSpecimenDeposit = follower.pathBuilder()
                .addPath(new BezierCurve(intakeSpecPose, new Pose(scorePose4.getX() + 1, scorePose4.getY())))
                .setLinearHeadingInterpolation(intakeSpecPose.getHeading(), scorePose4.getHeading())
                .setPathEndTimeoutConstraint(0)
                .setGlobalDeceleration()
                .setZeroPowerAccelerationMultiplier(5)
                .build();
        park = follower.pathBuilder()
                .addPath(new BezierCurve(scorePose3, new Pose(intakeSpecPose.getX() - 0.5, intakeSpecPose.getY() - 0.5, intakeSpecPose.getHeading())))
                .setLinearHeadingInterpolation(scorePose3.getHeading(), intakeSpecPose.getHeading(), 0.8)
                .setPathEndTimeoutConstraint(100)
                .setZeroPowerAccelerationMultiplier(5)
                .build();
        return List.of(presetScore, firstSampleIntake, firstSampleDropToHumanPlayer, secondSampleIntake, secondSampleDropToHumanPlayer, thirdSampleIntake, thirdSampleDropToHumanPlayer, firstSpecimenIntake, firstSpecimenDeposit, secondSpecimenIntake, secondSpecimenDeposit, thirdSpecimenIntake, thirdSpecimenDeposit, fourthSpecimenIntake, fourthSpecimenDeposit, park);
    }
}
