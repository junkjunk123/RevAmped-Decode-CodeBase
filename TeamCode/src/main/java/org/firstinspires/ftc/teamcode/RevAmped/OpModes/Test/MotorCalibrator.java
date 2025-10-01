package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.HwDevice;

@TeleOp (group = "Test")
public class MotorCalibrator extends DecodeRevAmpedOpMode {
    private DcMotor motor;
    private List<String> motors;
    private Robot robot;
    @Override
    public void RevAmpedInit() {
        motors = new ArrayList<>(Robot.getComponentNames(HwDevice.DeviceType.MOTOR));
    }

    @Override
    public void RevAmpedLoop() {
        for (int i = 0; i<motors.size(); i++){
            motor = hardwareMap.get(DcMotor.class, motors.get(i));
            telemetry.addData(motors.get(i), motor.getCurrentPosition());
        }
    }

    @Override
    public void update() {
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
