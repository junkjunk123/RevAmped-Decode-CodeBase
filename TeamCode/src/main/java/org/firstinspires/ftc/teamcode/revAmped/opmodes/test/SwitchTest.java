package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;

@Disabled
@TeleOp
public class SwitchTest extends DecodeRevAmpedOpMode {
    private String[] names;
    private DigitalChannel[] switches;

    @Override
    public void RevAmpedInit() {
        names = Robot.getComponentNames(HwDevice.DeviceType.DIGITAL).toArray(new String[0]);
        switches = Arrays.stream(names)
                .map(s -> hardwareMap.get(DigitalChannel.class, s))
                .toArray(DigitalChannel[]::new);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();
    }

    @Override
    public void RevAmpedLoop() {}

    @Override
    public void update() {
        for (int i = 0; i < switches.length; i++) {
            telemetry.addData(names[i], switches[i].getState());
        }

        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
