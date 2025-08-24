package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes;

import revAmped.components.MathUtil.HSVValue;

public interface ITDColor {
    enum DetectionColor {
        None,
        Blue,
        Yellow,
        Red
    }

    DetectionColor getColor();

    default HSVValue getHSVValues() {
        return new HSVValue(0,0,0);
    }

    default double getDistanceMM() {
        return 0;
    }
}
