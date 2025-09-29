package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeColor extends DecodeColor<IntakeColor.Color> implements DecodeColorSensor {
    public static float minPurpleHue;
    public static float maxPurpleHue;
    public static float minGreenHue;
    public static float maxGreenHue;

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
        PURPLE(IntakeColor.minPurpleHue,IntakeColor.maxPurpleHue),
        GREEN(IntakeColor.minGreenHue, IntakeColor.maxGreenHue),
        NEUTRAL(0,255);

        @Override
        public float getMinHue() {
            return minHue;
        }

        @Override
        public float getMaxHue() {
            return maxHue;
        }

        final float minHue;
        final float maxHue;

        Color(float minHue, float maxHue){
            this.minHue = minHue;
            this.maxHue = maxHue;
        }
    }

    @Override
    public double getHue() {
        return getHSVValues().getH() + offset;
    }
}
