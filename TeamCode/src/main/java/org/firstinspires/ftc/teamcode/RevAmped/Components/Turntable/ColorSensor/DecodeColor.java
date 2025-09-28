package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwColorSensor;

public abstract class DecodeColor<T extends Enum<T> & DecodeColor.DetectionColor> extends HwColorSensor implements DecodeColorSensor {
    public interface DetectionColor {
        float getMinHue();
        float getMaxHue();
    }

    private final T neutralColor;
    private T currentColor;

    public DecodeColor(HardwareMap hardwareMap, String id, T neutral) {
        super(hardwareMap, id);
        this.neutralColor = neutral;
    }

    @Override
    public abstract boolean testResponse();

    public abstract T getPurple();
    public abstract T getGreen();

    protected ArtifactColor fromDetectionColor(T color) {
        if (color == getGreen()) return ArtifactColor.GREEN;
        if (color == getPurple()) return ArtifactColor.PURPLE;
        return ArtifactColor.NONE;
    }

    public ArtifactColor getColor() {
        if (currentColor == null) {
            double hue = getHue();
            for (T color : neutralColor.getDeclaringClass().getEnumConstants()) {
                if (color != neutralColor) {
                    if (hue >= color.getMinHue() && hue <= color.getMaxHue()) {
                        currentColor = color;
                        return fromDetectionColor(currentColor);
                    }
                }
            }
        }
        return fromDetectionColor(currentColor);
    }

    public abstract double getHue();
}
