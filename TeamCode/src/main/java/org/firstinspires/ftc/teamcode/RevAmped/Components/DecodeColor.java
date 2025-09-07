package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwColorSensor;

public abstract class DecodeColor<T extends Enum<T> & DecodeColor.DetectionColor> extends HwColorSensor {
    public interface DetectionColor {
        float getMinHue();
        float getMaxHue();
    }

    public T neutralColor;
    private T currentColor;

    public DecodeColor(HardwareMap hardwareMap, String id, T neutral) {
        super(hardwareMap, id);
        this.neutralColor = neutral;
    }

    @Override
    public abstract boolean testResponse();

    public T getColor() {
        if (currentColor == null) {
            updateHSVValues();
            double hue = getHSVValues().getH();
            for (T color : neutralColor.getDeclaringClass().getEnumConstants()) {
                if (color != neutralColor) {
                    if (hue >= color.getMinHue() && hue <= color.getMaxHue()) {
                        currentColor = color;
                        return currentColor;
                    }
                }
            }
        }
        return currentColor;
    }
}
