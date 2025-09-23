package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;
import revAmped.components.prompter.BooleanPrompt;
import revAmped.components.prompter.OptionPrompt;
import revAmped.components.prompter.Prompter;
import revAmped.components.prompter.ValuePrompt;

@TeleOp
public class PromptTest extends DecodeRevAmpedOpMode {
    private int val;
    private boolean toggle;
    private Globals.AllianceColor color;
    private Prompter prompter;

    @Override
    public void RevAmpedInit() {
        prompter = new Prompter(this, new RevAmpedGamepad(gamepad1))
                .prompt("Select a number", new ValuePrompt(
                        "val",
                        0,
                        -10,
                        10,
                        1
                ))
                .prompt("Select a boolean", new BooleanPrompt(
                        "toggle",
                        false
                ))
                .prompt("Select an alliance color", new OptionPrompt<>(
                        "color",
                        Globals.AllianceColor.None,
                        Globals.AllianceColor.Red,
                        Globals.AllianceColor.Blue
                ))
                .onComplete(() -> {
                    val = prompter.get("val");
                    toggle = prompter.get("toggle");
                    color = prompter.get("color");
                });
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("Number", val);
        telemetry.addData("Boolean", toggle);
        telemetry.addData("Alliance Color", color);
    }

    @Override
    public void update() {
        prompter.run();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
