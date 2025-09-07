package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Components.DecodeColor;

public class IntakeColor extends DecodeColor<IntakeColor.Color> {
    public static float minPurpleHue = 0f;
    public static float maxPurpleHue = 0f;
    public static float minGreenHue = 0f;
    public static float maxGreenHue = 0f;

    public IntakeColor(HardwareMap hardwareMap, Color neutral) {
        super(hardwareMap, "intakeColor", neutral);
    }

    @Override
    public boolean testResponse() {
        return Math.abs(getHSVValues().getH() - 150) < 10;
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
}
