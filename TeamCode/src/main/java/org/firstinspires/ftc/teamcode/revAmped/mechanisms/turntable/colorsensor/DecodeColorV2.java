package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.CalcHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import java.util.EnumMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class DecodeColorV2 extends HwColorSensor implements DecodeColorSensor {
    public enum DetectionColor {
        PURPLE,
        GREEN,
        NEUTRAL
    }

    private DetectionColor currentColor;
    private boolean disabled = false;

    @FunctionalInterface
    public interface ColorEvaluator {
        boolean evaluate(double hue, double distance);
    }

    public DecodeColorV2(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    @Override
    public DelayedResult<Boolean> testResponse(Consumer<String> telemetry) {
        CalcHandler integrator = new CalcHandler();
        double nominalHue = getHue();
        BooleanSupplier timeout = MathUtil.booleanSupplierfromTimeDelay(5000);
        return new DelayedResult<>(() -> {
            if (!timeout.getAsBoolean()) {
                integrator.update(Math.abs(nominalHue - getHue()));
                return null;
            } else {
                return integrator.getIntegral() > 0.08;
            }
        }
        );
    }

    protected ArtifactColor fromDetectionColor(DetectionColor color) {
        if (color == DetectionColor.PURPLE) return ArtifactColor.PURPLE;
        if (color == DetectionColor.GREEN) return ArtifactColor.GREEN;
        return ArtifactColor.NONE;
    }

    public ArtifactColor getColor() {
        if (disabled) return ArtifactColor.NONE;
        if (currentColor == null) {
            for (DetectionColor color : DetectionColor.values()) {
                if (color != DetectionColor.NEUTRAL) {
                    if (!colorEvaluators.get(color).evaluate(getHue(), getDistanceMM())) continue;
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

    public final EnumMap<DetectionColor, ColorEvaluator> colorEvaluators = buildColorEvaluatorMap();

    protected abstract EnumMap<DetectionColor, ColorEvaluator> buildColorEvaluatorMap();

    public double getHue() {
        return getHSVValues().getH();
    }

    @Override
    public void disable() {
        disabled = true;
    }
}
