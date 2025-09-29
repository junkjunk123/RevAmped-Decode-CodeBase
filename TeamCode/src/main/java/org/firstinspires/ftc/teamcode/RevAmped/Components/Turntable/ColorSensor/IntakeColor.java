package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class IntakeColor extends DecodeColor<IntakeColor.Color> implements DecodeColorSensor {
    public static float minPurpleHue;
    public static float maxPurpleHue;
    public static float minGreenHue;
    public static float maxGreenHue;
    public static float maxGreenDistance;
    public static float maxPurpleDistance;
    private final double offset;

    public IntakeColor(HardwareMap hardwareMap, double offset) {
        super(hardwareMap, "intakeColor", Color.NEUTRAL);
        this.offset = offset;
    }

    @Override
    public boolean testResponse() {
        return Math.abs(getHSVValues().getH() - 150) < 10;
    }

    @Override
    public Color getPurple() {
        return Color.PURPLE;
    }

    @Override
    public Color getGreen() {
        return Color.GREEN;
    }

    public enum Color implements DecodeColor.DetectionColor {
        PURPLE(IntakeColor.minPurpleHue,IntakeColor.maxPurpleHue, maxPurpleDistance),
        GREEN(IntakeColor.minGreenHue, IntakeColor.maxGreenHue, maxGreenDistance),
        NEUTRAL(0,255, 10000);

        @Override
        public boolean evaluateHue(double hue) {
            return hue >= minHue && hue <= maxHue;
        }

        @Override
        public boolean evaluateDistance(double distance) {
            return distance <= maxDistance;
        }

        final float minHue;
        final float maxHue;
        final float maxDistance;

        Color(float minHue, float maxHue, float maxDistance) {
            this.minHue = minHue;
            this.maxHue = maxHue;
            this.maxDistance = maxDistance;
        }
    }

    @Override
    public double getHue() {
        return getHSVValues().getH() + offset;
    }
}
