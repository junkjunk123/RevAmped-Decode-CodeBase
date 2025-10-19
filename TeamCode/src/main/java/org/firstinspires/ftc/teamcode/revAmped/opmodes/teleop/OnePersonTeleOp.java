package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.GamepadMapper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

public class OnePersonTeleOp extends DecodeRevAmpedOpMode {
    private final DCTeleOp dcTeleOp = new DCTeleOp();

    @Override
    public void RevAmpedInit() {
        GamepadMapper gamepad_1 = new GamepadMapper(gamepad1);
        GamepadMapper gamepad_2 = new GamepadMapper(gamepad2);
        dcTeleOp.scheduler = this.scheduler;
        dcTeleOp.hardwareMap = this.hardwareMap;
        dcTeleOp.gamepad2 = gamepad2;
        dcTeleOp.gamepad1 = gamepad1;
        dcTeleOp.telemetry = this.telemetry;
        gamepad1.reset();
        gamepad2.reset();
        dcTeleOp.RevAmpedInit();
        dcTeleOp.setGamepad_1(gamepad_1);
        dcTeleOp.setGamepad_2(gamepad_2);
    }

    @Override
    public void RevAmpedLoop() {
        dcTeleOp.RevAmpedLoop();
    }

    @Override
    public void update() {
        dcTeleOp.update();
    }

    @Override
    public void RevAmpedInitLoop() {
        dcTeleOp.RevAmpedInitLoop();
    }

    @Override
    public void RevAmpedStart() {
        dcTeleOp.RevAmpedStart();
    }

    @Override
    public void RevAmpedStop() {
        dcTeleOp.RevAmpedStop();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}
