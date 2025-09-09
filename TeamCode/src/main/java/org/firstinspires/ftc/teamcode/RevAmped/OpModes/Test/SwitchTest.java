package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import revAmped.components.Hardware.HwDevice;

public class SwitchTest extends DecodeRevAmpedOpMode {
    private String[] names;
    private DigitalChannel[] switches;

    @Override
    public void RevAmpedInit() {
        names = Robot.getComponentNames(HwDevice.DeviceType.DIGITAL);
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
