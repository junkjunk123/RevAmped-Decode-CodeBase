package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwColorSensor;

public abstract class DecodeColor<T extends Enum<T> & DecodeColor.DetectionColor> extends HwColorSensor {
    public interface DetectionColor {
        double getMinHue();
        double getMaxHue();
    }

    public T neutralColor;

    public DecodeColor(HardwareMap hardwareMap, String id, T neutral) {
        super(hardwareMap, id);
        this.neutralColor = neutral;
    }

    @Override
    public boolean testResponse() {
        return getColor() == neutralColor;
    }

    public T getColor() {
        updateHSVValues();
        double hue = getHSVValues().getH();
        for (T color : neutralColor.getDeclaringClass().getEnumConstants()) {
            if (color != neutralColor) {
                if (hue >= color.getMinHue() && hue <= color.getMaxHue()) {
                    return color;
                }
            }
        }
        return neutralColor;
    }
}
