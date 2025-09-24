package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import static revAmped.components.Misc.Globals.AllianceColor.None;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;
import revAmped.components.prompter.BooleanPrompt;
import revAmped.components.prompter.OptionPrompt;
import revAmped.components.prompter.Prompter;
import revAmped.components.prompter.ValuePrompt;

@Disabled
@TeleOp
public class PromptTest extends DecodeRevAmpedOpMode {
    private int val;
    private boolean toggle;
    private Globals.AllianceColor color;
    private Prompter prompter;
    private boolean complete = false;

    @Override
    public void RevAmpedInit() {
        prompter = new Prompter(this, new RevAmpedGamepad(gamepad1))
                .prompt("val", new ValuePrompt(
                        "Select a number",
                        -10,
                        10,
                        0,
                        1
                ))
                .prompt("toggle", new BooleanPrompt(
                        "Select a boolean",
                        false
                ))
                .prompt("color", new OptionPrompt<>(
                        "Select an alliance color",
                        None,
                        Globals.AllianceColor.Red,
                        Globals.AllianceColor.Blue
                ))
                .onComplete(() -> {
                    val = (int) Math.floor(prompter.getOrDefault("val", 0.0));
                    toggle = prompter.getOrDefault("toggle", false);
                    color = prompter.getOrDefault("color", None);
                    complete = true;
                })
                .thenDisplay("Finished processing inputs");
    }

    @Override
    public void RevAmpedLoop() {
        if (complete) {
            telemetry.addData("Number", val);
            telemetry.addData("Boolean", toggle);
            telemetry.addData("Alliance Color", color);
        }
    }

    @Override
    public void RevAmpedInitLoop() {
        prompter.run();
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
