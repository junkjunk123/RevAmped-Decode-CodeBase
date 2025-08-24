package revAmped.components.Misc;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class RevAmpedTestOpMode extends RevAmpedOpMode {
    public abstract void initialize(RevAmpedOpMode op);
    public void telemetryLog(Telemetry telemetry) {
        Globals.testing = false;
        Globals.init(telemetry);
        this.telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry(), telemetry);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetryLog(telemetry);
        initialize(this);
        super.runOpMode();
    }
}
