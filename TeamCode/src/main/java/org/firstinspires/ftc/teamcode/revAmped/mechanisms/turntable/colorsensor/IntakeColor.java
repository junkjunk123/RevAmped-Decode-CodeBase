package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;

import android.provider.Settings;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.math.CalcHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedSupplier;

import java.util.function.BooleanSupplier;

@Config
public class IntakeColor extends DecodeColor<IntakeColor.Color> implements DecodeColorSensor {
    public static float minPurpleHue;
    public static float maxPurpleHue;
    public static float minGreenHue;
    public static float maxGreenHue;
    public static float maxGreenLeftDistance;
    public static float maxPurpleLeftDistance;
    public static float maxGreenRightDistance;
    public static float maxPurpleRightDistance;
    private double offset;

    public IntakeColor(HardwareMap hardwareMap, String id, double offset) {
        super(hardwareMap, id, Color.NEUTRAL);
        this.offset = offset;
    }

    @Override
    public DelayedResult<Boolean> testResponse() {
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

    @Override
    public Color getPurple() {
        return Color.PURPLE;
    }

    @Override
    public Color getGreen() {
        return Color.GREEN;
    }

    @Override
    public void disable() {}

    public enum CurrentSensor {
        RIGHT,
        LEFT;

        public double getMaxGreenDistance() {
            if (this == RIGHT) return IntakeColor.maxGreenRightDistance;
            return IntakeColor.maxGreenLeftDistance;
        }

        public double getMaxPurpleDistance() {
            if (this == RIGHT) return IntakeColor.maxPurpleRightDistance;
            return IntakeColor.maxPurpleLeftDistance;
        }
    }

    public enum Color implements DecodeColor.DetectionColor {
        PURPLE, GREEN, NEUTRAL;

        @Override
        public boolean evaluateHue(double hue) {
            return switch (this) {
                case PURPLE -> hue >= IntakeColor.minPurpleHue && hue <= IntakeColor.maxPurpleHue;
                case GREEN -> hue >= IntakeColor.minGreenHue && hue <= IntakeColor.maxGreenHue;
                default -> true;
            };
        }

        @Override
        public boolean evaluateDistance(double distance) {
            return switch (this) {
                case PURPLE -> distance <= IntakeColor.maxPurpleLeftDistance;
                case GREEN -> distance <= IntakeColor.maxGreenLeftDistance;
                default -> true;
            };
        }
    }

    @Override
    public double getHue() {
        return getHSVValues().getH() + offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }
}
