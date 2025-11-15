package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
    public abstract DelayedResult<Boolean> testResponse(Consumer<String> telemetry);

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
