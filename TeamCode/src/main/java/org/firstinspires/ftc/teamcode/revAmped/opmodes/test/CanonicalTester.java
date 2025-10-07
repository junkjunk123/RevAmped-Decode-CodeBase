package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import revAmped.components.hardware.HwDevice;

public abstract class CanonicalTester<T> extends DecodeRevAmpedOpMode {
    private String[] strings;
    private Object[] devices;
    private HwDevice.DeviceType type;
    private int index;
    private T selectedDevice;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        type = getDeviceType();
        Class<?> deviceClass = type.getCanonicalClass();
        strings = Robot.getComponentNames(type).toArray(new String[0]);
        devices = Arrays.stream(strings).map(s -> hardwareMap.get(deviceClass, s)).toArray();
    }

    @Override
    public void RevAmpedInitLoop() {
        telemetry.addData("SelectedDevice", strings[index]);
        telemetry.update();

        if (gamepad1.dpadUpWasPressed()) {
            index = (index + 1) % strings.length;
        }

        if (gamepad1.dpadDownWasPressed()) {
            index = (index - 1) % strings.length;
        }
    }

    public abstract HwDevice.DeviceType getDeviceType();

    @Override
    public void RevAmpedLoop() {
        loop(selectedDevice);
    }

    @Override
    public void RevAmpedStart() {
        selectedDevice = (T) devices[index];
    }

    public abstract void loop(T t);

    @Override
    public void update() {
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
