package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.HwSlide;
import revAmped.components.Hardware.HwSwitch;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;

@TeleOp(name = "SlideCalibrater", group = "Test")
public class SlideCalibrater extends RevAmpedTestOpMode {
    private HwSlide hslides;
    private HwSlide vslides;
    private RevAmpedGamepad gamepad_1;

    @Override
    public void RevAmpedInit() {

    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) {
            hslides.runToPosition(Constants.HSLIDE_MAX_EXTENSION);
        }

        telemetry.addData("hslidesCurrentPos", hslides.getCurrentPosition());
        telemetry.addData("hslidesRetracted",hslides.getVelocity() < 0.75 && hslides.getCurrentPosition() < 5);
        telemetry.addData("vslidesCurrentPos",vslides.getCurrentPosition());
        telemetry.addData("vslidesRetracted",vslides.getVelocity() < 0.75 && vslides.getCurrentPosition() < 5);
    }

    @Override
    public void update() {
        gamepad_1.update();
        hslides.update();
        vslides.update();
        telemetry.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

    @Override
    public void initialize(RevAmpedOpMode op) {
        gamepad_1 = new RevAmpedGamepad(op.gamepad1);
        hslides = new HwSlide(op.hardwareMap, "hslides", Constants.HSLIDE_DIRECTION);
        vslides = new HwSlide(op.hardwareMap, "vslides", Constants.VSLIDE_DIRECTION);
    }
}
