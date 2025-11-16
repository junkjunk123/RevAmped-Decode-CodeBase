package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@Disabled
@TeleOp
public class ObeliskDetectionTest extends DecodeRevAmpedOpMode {
    private DecodeLimelight limelight;
    private TelemetryManager manager;

    @Override
    public void RevAmpedInit() {
        limelight = new DecodeLimelight(hardwareMap);
        limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        manager = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("motif", Globals.randomizationState);
        manager.addData("motif", Globals.randomizationState);
        telemetry.update();
        manager.update();
    }

    @Override
    public void update() {
        limelight.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
