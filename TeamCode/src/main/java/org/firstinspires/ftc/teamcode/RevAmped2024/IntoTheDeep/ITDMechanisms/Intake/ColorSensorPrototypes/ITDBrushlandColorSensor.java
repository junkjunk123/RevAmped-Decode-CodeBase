package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes;

import revAmped.components.Hardware.HwDevice;
import revAmped.components.Hardware.HwSwitch;

public class ITDBrushlandColorSensor implements HwDevice, ITDColor {
    private final HwSwitch pin0;
    private final HwSwitch pin1;
    private DetectionColor color = DetectionColor.None;

    public ITDBrushlandColorSensor(HwSwitch pin0, HwSwitch pin1) {
        this.pin0 = pin0;
        this.pin1 = pin1;
    }

    @Override
    public String getId() {
        return pin0.getId() + pin1.getId();
    }

    @Override
    public void update() {
        pin0.update();
        pin1.update();
        color = null;
    }

    public DetectionColor getColor() {
        if (color == null) {
            updateColor();
        }

        return color;
    }

    public static DetectionColor HSVToDetectionColor(float hue) {
        if (hue >= 25 && hue <= 45) {
            return DetectionColor.Red;
        } else if (hue >= 57 && hue < 80) {
            return DetectionColor.Yellow;
        } else if (hue >= 85 && hue < 120) {
            return DetectionColor.Blue;
        }

        return DetectionColor.None;
    }

    private void updateColor() {
        if (pin1.isTouch() && pin0.isTouch()) {
            color = DetectionColor.Yellow;
        } else if (pin1.isTouch() && !pin0.isTouch()) {
            color = DetectionColor.Red;
        } else if (!pin1.isTouch() && pin0.isTouch()) {
            color = DetectionColor.Blue;
        } else {
            color = DetectionColor.None;
        }
    }

    public String detectionColorToString() {
        return getColor().name();
    }

    public boolean getPin0State() {
        return pin0.isTouch();
    }

    public boolean getPin1State() {
        return pin1.isTouch();
    }
}
