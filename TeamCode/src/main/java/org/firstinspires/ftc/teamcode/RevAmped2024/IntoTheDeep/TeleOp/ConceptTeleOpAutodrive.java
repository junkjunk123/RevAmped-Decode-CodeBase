package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SpecAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

@TeleOp(name = "ConceptTeleOpAutodrive", group = "Game")
public class ConceptTeleOpAutodrive extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private ITDRevAmpedGamepad gamepad_1;
    private Follower follower;
    private DepositionArm depositionArm;
    private boolean autoDriving = false;
    private Pose specIntakePose = new Pose(39.75,65,Math.toRadians(255));
    private Pose specDepositPose = new Pose(26.5,28,Math.toRadians(305));
    private PathChain specIntakePath;
    private PathChain specDepositPath;
    private boolean hasSpec = false;

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.None, SpecAutoPaths.startPose);
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1, 0.3f);
        gamepad1.reset();
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(SpecAutoPaths.startPose);
        depositionArm = new DepositionArm(robot, scheduler.delays);
        specIntakePath = follower.pathBuilder()
                .addPath(new Path(new BezierLine(specDepositPose, specIntakePose)))
                .setLinearHeadingInterpolation(specDepositPose.getHeading(), specIntakePose.getHeading(), 0.8)
                .setGlobalDeceleration()
                .build();
        specDepositPath = follower.pathBuilder()
                .addPath(new Path(new BezierLine(specIntakePose, specDepositPose)))
                .setLinearHeadingInterpolation(specIntakePose.getHeading(), specDepositPose.getHeading(), 0.8)
                .setGlobalDeceleration()
                .build();
    }

    @Override
    public void RevAmpedLoop() {
        if (!autoDriving) {
            robot.drive.arcadeDrive(gamepad_1, false);
        }

        if (gamepad_1.a.isPress()) {
            autoDriving = true;
            if (hasSpec) {
                follower.followPath(specDepositPath);
            } else {
                follower.followPath(specIntakePath);
            }
        }

        if (gamepad_1.b.isPress() && !follower.isBusy()) {
            autoDriving = false;
            follower.breakFollowing();
            hasSpec = !hasSpec;
        }
    }

    @Override
    public void update() {
        robot.update();
        follower.update();
        gamepad_1.update();
    }

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }
}
