package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import revAmped.components.Hardware.HwSwitch;

@TeleOp(name = "SwitchTest", group = "Test")
public class SwitchTest extends OpMode {
    HwSwitch hswitch;
    HwSwitch vswitch;
    HwSwitch clawSwitch;
    DigitalChannel hswitchUnwrapped;
    DigitalChannel vswitchUnwrapped;
    DigitalChannel clawSwitchUnwrapped;

    @Override
    public void init() {
        hswitch = new HwSwitch(hardwareMap, "hswitch",true, false);
        vswitch = new HwSwitch(hardwareMap, "vswitch", true, false);
        clawSwitch = new HwSwitch(hardwareMap, "clawSwitch",true, false);
        hswitchUnwrapped = hardwareMap.get(DigitalChannel.class, "hswitch");
        vswitchUnwrapped = hardwareMap.get(DigitalChannel.class, "vswitch");
        clawSwitchUnwrapped = hardwareMap.get(DigitalChannel.class, "clawSwitch");
    }

    @Override
    public void loop() {
        hswitch.update();
        vswitch.update();
        clawSwitch.update();

        telemetry.addData("hswitch",hswitch.isTouch());
        telemetry.addData("vswitch",vswitch.isTouch());
        telemetry.addData("clawSwitch",clawSwitch.isTouch());
        telemetry.addData("hswitchUnwrap",hswitchUnwrapped.getState());
        telemetry.addData("vswitchUnwrap",vswitchUnwrapped.getState());
        telemetry.addData("clawSwitchUnwrap",clawSwitchUnwrapped.getState());
        telemetry.update();
    }
}
