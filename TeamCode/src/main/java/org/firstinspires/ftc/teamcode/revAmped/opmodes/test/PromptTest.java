package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.AllianceColor.None;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.BooleanPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.OptionPrompt;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.Prompter;
import org.firstinspires.ftc.teamcode.revAmped.components.prompter.ValuePrompt;

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
