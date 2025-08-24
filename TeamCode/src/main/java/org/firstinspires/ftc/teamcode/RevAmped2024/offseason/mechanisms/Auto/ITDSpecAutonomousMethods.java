package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;
import revAmped.components.Architecture.AEABuilder;
import revAmped.components.Architecture.AutonomousEnumeratedAction;
import revAmped.components.Pedro.FollowerManager;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.Globals;

public class ITDSpecAutonomousMethods {
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

    //fill this in
    public static AutonomousEnumeratedAction generateInitialSpecDepo() {
        return AEABuilder.createAction(() -> {}, Runnable::run, followerManager.follower::isBusy);
    }
}
