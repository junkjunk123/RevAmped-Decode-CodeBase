package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

import revAmped.components.pedro.FollowerManager;
import revAmped.components.scheduler.Scheduler;
import revAmped.components.misc.Globals;

public class DecodeAutonomousMethods {
    public static Robot robot;
    public static FollowerManager followerManager;
    public static Scheduler scheduler;
    public static Telemetry telemetry;
    public static ElapsedTime overallTimer;
    public static ElapsedTime driveTimer;

    public static void initialize() {
        robot = Globals.aae.parameters.getRobot();
        scheduler = Scheduler.getInstance();
        followerManager = Globals.aae.parameters.getFollowerManager();
        telemetry = Globals.telemetry;
        driveTimer = Globals.aae.preloadDriveTimer;
        overallTimer = Globals.aae.overallTimer;
    }
}
