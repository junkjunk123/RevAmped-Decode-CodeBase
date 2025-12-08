package org.firstinspires.ftc.teamcode.revAmped.components.misc;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.DelayScheduler;

import java.util.Objects;

/**
 * Base class for all RevAmped OpModes.
 */
public abstract class RevAmpedOpMode extends LinearOpMode {
    /**
     * Scheduler instance for managing commands.
     */
    public DelayScheduler scheduler = new DelayScheduler();

    @Override
    public void runOpMode() throws InterruptedException {
        Globals.aae = null;
        Globals.followerManager = null;
        Globals.testing = false;
        Globals.disableDrive = false;
        Globals.init(telemetry);
        Logger.reset();
        RevAmpedInit();

        while (opModeInInit()) {
            RevAmpedInitLoop();
        }

        waitForStart();

        RevAmpedStart();

        while (opModeIsActive()) {
            update();
            scheduler.update();
            RevAmpedLoop();

            if (isStopRequested()) {
                scheduler.clear();
                RevAmpedStop();
            }
        }
    }

    /**
     * Called during the initialization phase of the OpMode.
     * This method is called once when the OpMode is initialized.
     */
    public abstract void RevAmpedInit();

    /**
     * Called during the main loop of the OpMode.
     * This method is called repeatedly while the OpMode is active.
     */
    public abstract void RevAmpedLoop();

    /**
     * Called during the start phase of the OpMode.
     * This method is called once when the OpMode is started.
     */
    public void RevAmpedStart() {};

    /**
     * Called during the initialization phase of the OpMode.
     * This method is called repeatedly when the OpMode is initialized.
     */
    public void RevAmpedInitLoop() {};

    /**
     * Called when the OpMode is stopped or requested to stop.
     * This method is called once when the OpMode is stopped.
     */
    public void RevAmpedStop() {};

    /**
     * Updates the OpMode state. This is called before the Scheduler
     * This method is called repeatedly while the OpMode is active.
     */
    public abstract void update();

    /**
     * Returns the type of OpMode.
     * @return the OpModeType enum value
     */
    public abstract OpModeType getOpModeType();

    /**
     * Returns the name on the driver station for this OpMode.
     * This is typically the name specified in the TeleOp or Autonomous annotation.
     * @return the name of the driver station
     * @throws RuntimeException if the OpMode is not annotated with TeleOp or Autonomous
     */
    public String getDriverStationName() {
        Class<?> opClass = this.getClass();
        if (opClass.isAnnotationPresent(TeleOp.class)) {
            return Objects.requireNonNull(opClass.getAnnotation(TeleOp.class)).name();
        } else if (opClass.isAnnotationPresent(Autonomous.class)) {
            return Objects.requireNonNull(opClass.getAnnotation(Autonomous.class)).name();
        }

        throw new RuntimeException("getDriverStationName() on an un-annotated opMode");
    }
}
