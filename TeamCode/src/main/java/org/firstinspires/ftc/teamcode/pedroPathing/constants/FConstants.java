package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Localizers;
import com.pedropathing.util.CustomFilteredPIDFCoefficients;
import com.pedropathing.util.CustomPIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = "motor_lf";
        FollowerConstants.leftRearMotorName = "motor_lb";
        FollowerConstants.rightFrontMotorName = "motor_rf";
        FollowerConstants.rightRearMotorName = "motor_rb";

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.FORWARD;

        FollowerConstants.mass = 16.2;

        FollowerConstants.xMovement = 78.261926752421046666666666666667;
        FollowerConstants.yMovement = 61.494551922189565;

        FollowerConstants.forwardZeroPowerAcceleration = -25.9346931313679598;
        FollowerConstants.lateralZeroPowerAcceleration = -67.342491844080064;

        FollowerConstants.translationalPIDFCoefficients = new CustomPIDFCoefficients(
                0.03,
                0,
                0,
                0);
        FollowerConstants.useSecondaryTranslationalPID = true;
        FollowerConstants.translationalPIDFFeedForward = 0.015;
        FollowerConstants.translationalPIDFSwitch = 4;

        FollowerConstants.secondaryTranslationalPIDFCoefficients = new CustomPIDFCoefficients(
                0.4,
                0,
                0.03,
                0); // Not being used, @see useSecondaryTranslationalPID
        FollowerConstants.secondaryTranslationalPIDFFeedForward = 0.0006;

        FollowerConstants.headingPIDFCoefficients = new CustomPIDFCoefficients(
                0.8,
                0,
                0,
                0);
        FollowerConstants.headingPIDFFeedForward = 0.01;
        FollowerConstants.useSecondaryHeadingPID = true;
        FollowerConstants.secondaryHeadingPIDFCoefficients = new CustomPIDFCoefficients(
                2.5,
                0,
                0.1,
                0); // Not being used, @see useSecondaryHeadingPID
        FollowerConstants.secondaryHeadingPIDFFeedForward = 0.0005;

        FollowerConstants.drivePIDFCoefficients = new CustomFilteredPIDFCoefficients(
                0.02,
                0.000008,
                0.0015,
                0.6,
                0);
        FollowerConstants.drivePIDFFeedForward = 0.01;
        FollowerConstants.useSecondaryDrivePID = false;
        FollowerConstants.secondaryDrivePIDFCoefficients = new CustomFilteredPIDFCoefficients(
                0.05,
                0,
                0.0004,
                0.6,
                0); // Not being used, @see useSecondaryDrivePID
        FollowerConstants.secondaryDrivePIDFFeedForward = 0.01;
        FollowerConstants.zeroPowerAccelerationMultiplier = 4;
        FollowerConstants.centripetalScaling = 0.0005;

        FollowerConstants.pathEndTimeoutConstraint = 0;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;
        Follower.useCentripetal = true;
        Follower.useDrive = true;
        Follower.useHeading = true;
        Follower.useTranslational = true;
        FollowerConstants.decelerationStartMultiplier = 0.4;
    }
}