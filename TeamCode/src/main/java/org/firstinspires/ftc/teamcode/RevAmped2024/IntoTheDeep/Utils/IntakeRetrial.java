package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierPoint;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.util.Range;

public class IntakeRetrial {
    private final RobotRevampedITD robot;
    private final DelayManager delays;
    private final Follower follower;
    private boolean retrialNeeded = false;
    private boolean up = true;
    private int counter = 1;
    private boolean correctColor;

    public IntakeRetrial(RobotRevampedITD robot, DelayManager delays, Follower follower) {
        this.robot = robot;
        this.delays = delays;
        this.follower = follower;
    }

    private void intakeRetrialTurning(boolean up, float degs) {
        Pose currentPose = follower.getPose();
        robot.hslides.runToPosition(robot.hslides.getTargetPosition() - 50);
        PathChain retrial;

        if (up) {
            retrial = follower.pathBuilder()
                    .addPath(new BezierPoint(new Point(currentPose)))
                    .setConstantHeadingInterpolation(currentPose.getHeading() + Math.toRadians(degs))
                    .build();
        } else {
            retrial = follower.pathBuilder()
                    .addPath(new BezierPoint(new Point(currentPose)))
                    .setConstantHeadingInterpolation(currentPose.getHeading() - Math.toRadians(degs))
                    .build();
        }

        follower.followPath(retrial);
        delays.add(new Delay(150, () -> robot.hslides.runToPosition(robot.hslides.getTargetPosition() + 50)));
    }

    public void retryIntake() {
        setupIntakeRetrial();
        intakeRetrialIterator();
    }

    private void intakeRetrialIterator() {
        update();
        delays.add(new Delay(100, () -> {
            if (!correctColor) {
                intakeRetrialIterator();
            }
        }));
    }

    public boolean gotSample() {
        return correctColor;
    }

    private void setupIntakeRetrial() {
        retrialNeeded = true;
        counter = 1;
        up = true;
        correctColor = false;
    }

    private void update() {
        if (retrialNeeded) {
            DetectionColor detectionColor = robot.colorSensor.getColor();

            if (robot.correctColor(detectionColor)) {
                retrialNeeded = false;
                correctColor = true;
            } else {
                if (robot.getAllianceColor() == RobotRevampedITD.AllianceColor.Blue) {
                    intakeRetrialTurning(up, ((float) counter) * 1.5f * (float) Math.pow(-1, counter));
                } else {
                    intakeRetrialTurning(!up, ((float) counter) * 1.5f * (float) Math.pow(-1, counter));
                }

                up = !up;
                counter = Range.clip(counter + 1, 0, 4);

                retrialNeeded = false;
                delays.add(new Delay(450, () -> retrialNeeded = true));
            }
        }
    }
}
