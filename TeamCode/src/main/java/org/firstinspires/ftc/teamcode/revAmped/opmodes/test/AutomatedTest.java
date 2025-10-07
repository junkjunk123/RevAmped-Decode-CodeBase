package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

import java.util.Objects;

import revAmped.components.scheduler.Util.DelayedResult;
import revAmped.components.hardware.HwDevice;
import revAmped.components.hardware.HwSlide2;
import revAmped.components.misc.Logger;

@Disabled
@TeleOp
public class AutomatedTest extends DecodeRevAmpedOpMode {
    private Robot robot;

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