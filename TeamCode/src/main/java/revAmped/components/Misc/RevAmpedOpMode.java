package revAmped.components.Misc;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import revAmped.components.CommandScheduler.Scheduler;

public abstract class RevAmpedOpMode extends LinearOpMode {
    public Scheduler scheduler = new Scheduler();

    @Override
    public void runOpMode() throws InterruptedException {
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

    public abstract void RevAmpedInit();
    public abstract void RevAmpedLoop();
    public void RevAmpedStart() {};
    public void RevAmpedInitLoop() {};
    public void RevAmpedStop() {};
    public abstract void update();
    public abstract OpModeType getOpModeType();
}
