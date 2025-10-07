package org.firstinspires.ftc.teamcode.revAmped.components.prompter;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import revAmped.components.gamepad.Button2;
import revAmped.components.gamepad.RevAmpedGamepad;

public abstract class Prompt<T> {
    protected RevAmpedGamepad input;
    private Telemetry telemetry;

    public abstract T process();

    /**
     * Injects dependencies into the prompt.
     * This method is package-private to allow Prompter to configure the prompt
     * without exposing these dependencies publicly.
     */
    void configure(RevAmpedGamepad input, Telemetry telemetry) {
        this.input = input;
        this.telemetry = telemetry;
    }

    // Helper functions

    /**
     * Checks if any of the specified buttons were just pressed.
     */
    protected boolean anyJustPressed(Button2... buttons) {
        for (Button2 button : buttons) {
            if (input.isPressed(button)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a line to the Telemetry.
     */
    protected void addLine(String lineCaption) {
        telemetry.addLine(lineCaption);
    }

    /**
     * Adds data to the Telemetry.
     */
    protected void addData(String caption, Object value) {
        telemetry.addData(caption, value);
    }
}
