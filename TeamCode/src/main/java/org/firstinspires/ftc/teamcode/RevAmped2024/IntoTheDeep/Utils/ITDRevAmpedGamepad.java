package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;

import revAmped.components.GamepadDevices.Button2;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.MathUtil.RGBValue;

public class ITDRevAmpedGamepad extends RevAmpedGamepad {
    public Button2 left_trigger_button;
    public Button2 right_trigger_button;
    public float trigger0 = 0.3f;

    public ITDRevAmpedGamepad(Gamepad gamepad) {
        super(gamepad);
    }

    public ITDRevAmpedGamepad(Gamepad gamepad, float trigger0) {
        super(gamepad);
        left_trigger_button = new Button2(false);
        right_trigger_button = new Button2(false);
        this.trigger0 = trigger0;
    }

    public void sendGamepadSignals(DetectionColor color) {
        rumble(0.5, 0.5, 500);
        sendLEDS(color);
    }

    public void sendGamepadSignals(RGBValue values) {
        rumble(0.5, 0.5, 500);
        setLED(values.getIntegerR(), values.getIntegerG(), values.getIntegerB(), Gamepad.LED_DURATION_CONTINUOUS);
    }

    public void rumble() {
        rumble(0.5, 0.5, 500);
    }

    public void sendLEDS(DetectionColor color) {
        if (color == DetectionColor.Yellow) {
            setLED(1, 1, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (color == DetectionColor.Red) {
            setLED(1, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (color == DetectionColor.Blue) {
            setLED(0, 0, 1, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (color == DetectionColor.None) {
            setLED(0, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
        }
    }

    public void sendGamepadSignalsSampleDetection(boolean isAllianceSpecific) {
        rumble(0.5, 0.5, 500);
        if (isAllianceSpecific) {
            setLED(0, 1, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else {
            setLED(1, 1, 0, Gamepad.LED_DURATION_CONTINUOUS);
        }
    }

    public int fineTune(float magnitude, float maxValue) {
        magnitude = smoothControls(magnitude);

        return Math.round(magnitude * 1 / 300 * maxValue);
    }

    @Override
    public void update() {
        super.update();

        if (left_trigger_button != null) {
            left_trigger_button.update(left_trigger > trigger0);
        }

        if (left_trigger_button != null) {
            right_trigger_button.update(right_trigger > trigger0);
        }
    }
}