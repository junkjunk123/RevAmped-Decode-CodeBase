package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.function.Supplier;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwColorSensor;

public abstract class DecodeColor<T extends Enum<T> & DecodeColor.DetectionColor> extends HwColorSensor implements DecodeColorSensor {
    public interface DetectionColor {
        boolean evaluateHue(double hue);
        boolean evaluateDistance(double distance);
        default boolean evaluate(Supplier<Double> hue, Supplier<Double> distance) {
            return evaluateDistance(distance.get()) && evaluateHue(hue.get());
        }
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
            for (T color : neutralColor.getDeclaringClass().getEnumConstants()) {
                if (color != neutralColor) {
                    if (!color.evaluate(this::getHue, this::getDistanceMM)) continue;
                    currentColor = color;
                    return fromDetectionColor(currentColor);
                }
            }
        }

        return fromDetectionColor(currentColor);
    }

    @Override
    public void update() {
        super.update();
        currentColor = null;
    }

    public abstract double getHue();
}
