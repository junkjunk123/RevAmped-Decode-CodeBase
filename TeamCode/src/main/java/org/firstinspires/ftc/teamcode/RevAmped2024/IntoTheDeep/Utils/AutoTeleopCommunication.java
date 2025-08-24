package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils;

import com.pedropathing.localization.Pose;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;

public class AutoTeleopCommunication {
    private static RobotRevampedITD.AllianceColor allianceColor = RobotRevampedITD.AllianceColor.None;
    private static boolean cycling = true;
    private static Pose startPose = new Pose();

    public static void setAllianceColor(RobotRevampedITD.AllianceColor allianceColor) {
        AutoTeleopCommunication.allianceColor = allianceColor;
    }

    public static RobotRevampedITD.AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public static void reset() {
        allianceColor = RobotRevampedITD.AllianceColor.None;
    }

    public static Pose getStartPose() {
        return startPose;
    }

    public static void setStartPose(Pose startPose) {
        AutoTeleopCommunication.startPose = startPose;
    }

    public static void resetStartPose() {
        startPose = new Pose();
    }

    public static void handleAllianceColor(RobotRevampedITD robot, ITDRevAmpedGamepad gamepad1, ITDRevAmpedGamepad gamepad2) {
        if (robot.getAllianceColor() == RobotRevampedITD.AllianceColor.Blue) {
            robot.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        } else if (robot.getAllianceColor() == RobotRevampedITD.AllianceColor.Red) {
            robot.setAllianceColor(RobotRevampedITD.AllianceColor.Blue);
        } else if (AutoTeleopCommunication.getAllianceColor() != RobotRevampedITD.AllianceColor.None) {
            robot.setAllianceColor(AutoTeleopCommunication.getAllianceColor());
        } else {
            robot.setAllianceColor(RobotRevampedITD.AllianceColor.Red);
        }

        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());
    }

    public static RobotRevampedITD.AllianceColor handleAllianceColor(RobotRevampedITD.AllianceColor alliance) {
        if (alliance == RobotRevampedITD.AllianceColor.Blue) {
            alliance = RobotRevampedITD.AllianceColor.Red;
        } else if (alliance == RobotRevampedITD.AllianceColor.Red) {
            alliance = RobotRevampedITD.AllianceColor.Blue;
        } else if (AutoTeleopCommunication.getAllianceColor() != RobotRevampedITD.AllianceColor.None) {
            alliance = AutoTeleopCommunication.getAllianceColor();
        } else {
            alliance = RobotRevampedITD.AllianceColor.Red;
        }

        return alliance;
    }

    public static boolean isCycling() {
        return cycling;
    }

    public static void setCycling(boolean cycling) {
        AutoTeleopCommunication.cycling = cycling;
    }
}