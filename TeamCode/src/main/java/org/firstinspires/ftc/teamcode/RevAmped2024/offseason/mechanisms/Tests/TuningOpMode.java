package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;
import java.util.function.Supplier;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;

@TeleOp(name = "TuningOpMode", group = "Tuning")
public class TuningOpMode extends RevAmpedOpMode {
    private RevAmpedGamepad gamepad_1;
    private List<Supplier<RevAmpedTestOpMode>> tests;
    private int currentTest;
    private RevAmpedTestOpMode selectedTest;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        tests = List.of(HslideTuner::new, SlideCalibrater::new, VslideTuner::new); //add tests here
    }

    @Override
    public void RevAmpedInitLoop() {
        gamepad_1.update();

        if (gamepad_1.a.isPress()) {
            currentTest++;
            if (currentTest >= tests.size()) currentTest = 0;
        }

        telemetry.addData("Current Test", tests.get(currentTest).get().getDriverStationName());
        telemetry.update();
    }

    @Override
    public void RevAmpedStart() {
        selectedTest = tests.get(currentTest).get();
        selectedTest.telemetryLog(telemetry);
        selectedTest.initialize(this);
        selectedTest.RevAmpedInit();
        selectedTest.RevAmpedStart();
    }

    @Override
    public void RevAmpedLoop() {
        selectedTest.RevAmpedLoop();
    }

    @Override
    public void update() {
        selectedTest.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return selectedTest.getOpModeType();
    }
}
