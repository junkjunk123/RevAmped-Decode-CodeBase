package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;

import java.util.Objects;

import revAmped.components.CommandScheduler.Util.DelayedResult;
import revAmped.components.Hardware.HwDevice;
import revAmped.components.Hardware.HwSlide2;
import revAmped.components.Misc.Logger;

@TeleOp
public class AutomatedTest extends DecodeRevAmpedOpMode {
    private Robot robot;
    private HwDevice.DeviceType deviceType;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void RevAmpedStart() {
        for (HwDevice.DeviceType type : robot.allDevices.keySet()) {
            telemetry.addData("Testing", type.name());
            telemetry.update();
            deviceType = type;
            for (HwDevice device : Objects.requireNonNull(robot.allDevices.get(type))) {
                if (!device.testResponse()) {
                    Logger.permaLog(device.getId(), "FAILED");
                }
            }
        }

        Logger.permaLog("Testing", "Complete");
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.aWasPressed()) {
            deviceType = HwDevice.DeviceType.MOTOR;
            if (robot.allDevices.containsKey(HwDevice.DeviceType.MOTOR)) {
                for (HwDevice device : Objects.requireNonNull(robot.allDevices.get(HwDevice.DeviceType.MOTOR))) {
                    if (device instanceof HwSlide2<?> slide) {
                        DelayedResult<Integer> result = slide.testEncodersAndSwitch();
                        result.andThen(worked -> {
                            if (worked == -1) {
                                Logger.permaLog(slide.getId() + " encoder", "FAILED");
                            } else if (worked == -2) {
                                Logger.permaLog(slide.getId() + " limit switch", "FAILED");
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        robot.update();
        Logger.update(telemetry);
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}    