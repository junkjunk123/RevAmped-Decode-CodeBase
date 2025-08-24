package org.firstinspires.ftc.teamcode.pedroPathing.examples;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.AutoMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

import revAmped.components.CommandScheduler.Delay;
@Disabled
@Autonomous(name = "Triangle2", group = "Examples")
public class Triangle2 extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private Follower follower;
    private IntakeFourbar intakeFourbar;
    private DepositionArm depositionArm;
    private AutoMethods autoMethods;
    private boolean firstTime = true;
    private boolean firstTime1 = true;
    private boolean firstTime2 = true;
    private final Pose startPose = new Pose(7.5, 114, Math.toRadians(0));
    private final Pose interPose = new Pose(20,123.25, Math.toRadians(-8));
    private final Pose endPose = new Pose(20, 136.5, Math.toRadians(-7));
    private final Pose last = new Pose(20, 136.5, Math.toRadians(11));
    private int pathState = 0;

    private PathChain first, second, third;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.AUTO_TEST;
    }

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this, RobotRevampedITD.AllianceColor.Red, startPose);
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        depositionArm = new DepositionArm(robot, scheduler.delays, false);
        intakeFourbar = new IntakeFourbar(robot.intakeArm, robot.intakeTilt, robot.intakeTrapdoor, scheduler.delays);
        autoMethods = new AutoMethods(robot, scheduler.delays, intakeFourbar, depositionArm);
        AutoTeleopCommunication.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        buildPaths();

        robot.vslides.resetPosition();
        robot.hslides.resetPosition();

        follower.setStartingPose(startPose);
        robot.init();
        follower.followPath(first, true);
    }

    @Override
    public void RevAmpedStart() {
        robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
        depositionArm.autoBasketDepo(ITDConstants.DEPO_TURRET_AUTO_BASKET_1, () -> firstTime1 = false);
    }

    @Override
    public void RevAmpedLoop() {
        switch (pathState) {
            case 0:
                scheduler.run("path1",() -> scheduler.add(new Delay(() -> robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 25 && !firstTime1, () -> {
                    scheduler.add(new Delay(50, robot.depoClaw::open));
                    robot.depoTilt.sampleDepoAutoDownFinal();
                    intakeFourbar.intakePreloadAuto();
                    scheduler.add(new Delay(200, () -> {
                        robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX, false);
                        robot.intake.setPower(1f);
                        scheduler.add(new Delay(50, () -> {
                            robot.vslides.retract(() -> firstTime2 = false);
                            depositionArm.resetDepoArm();
                            scheduler.add(new Delay(() -> robot.colorSensor.getColor() != DetectionColor.None, () -> autoMethods.transferPreload(() -> nextState(second))));
                        }));
                    }));
                })));
                break;
            case 1:
                scheduler.run("path2",() -> {
                    robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO);
                    depositionArm.autoBasketDepo(ITDConstants.DEPO_TURRET_AUTO_BASKET_1, () -> firstTime1 = false);
                    scheduler.add(new Delay(follower::atParametricEnd, () -> scheduler.add(new Delay(() -> robot.vslides.getCurrentPosition() >= ITDConstants.VSLIDE_BASKET_AUTO - 25 && !firstTime1, () -> {
                        scheduler.add(new Delay(50, robot.depoClaw::open));
                        robot.depoTilt.sampleDepoAutoDownFinal();
                        intakeFourbar.intakePreloadAuto();
                        scheduler.add(new Delay(200, () -> {
                            robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX, false);
                            robot.intake.setPower(1f);
                            scheduler.add(new Delay(50, () -> {
                                robot.vslides.retract();
                                depositionArm.resetDepoArm();
                                scheduler.add(new Delay(() -> robot.colorSensor.getColor() != DetectionColor.None, () -> autoMethods.transferPreload(() -> nextState(third))));
                            }));
                        }));
                    }))));
                });
        }
    }

    private void buildPaths() {
        first = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(interPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), interPose.getHeading(), 0.9)
                .build();
        second = follower.pathBuilder()
                .addPath(new BezierLine(new Point(startPose), new Point(endPose)))
                .setLinearHeadingInterpolation(startPose.getHeading(), endPose.getHeading())
                .build();
        third = follower.pathBuilder()
                .addPath(new BezierLine(new Point(endPose), new Point(last)))
                .setLinearHeadingInterpolation(startPose.getHeading(), last.getHeading())
                .build();
    }

    @Override
    public void update() {
        follower.update();
        telemetry.addData("vswitch",!firstTime2);
        telemetry.update();
        robot.update();
    }

    private void nextState(PathChain pathChain, int state) {
        firstTime1 = true;
        follower.followPath(pathChain, true);
        pathState = state;
        firstTime = true;
        firstTime2 = true;
    }

    private boolean pathJustComplete() {
        boolean result = !follower.isBusy() && firstTime;

        if (result) {
            firstTime = false;
        }

        return result;
    }

    private void nextState(PathChain pathChain) {
        firstTime1 = true;
        follower.followPath(pathChain, true);
        pathState++;
        firstTime = true;
        firstTime2 = true;
    }
}
