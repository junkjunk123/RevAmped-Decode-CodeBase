package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwColorSensor;

public class ITDI2CBrushlandSensor extends HwColorSensor implements ITDColor {
    private DetectionColor detectionColor = DetectionColor.None;
    public static double redMin = 235, redMax = 243, red1Min = 10, red1Max = 40;
    public static double yellowMin = 55, yellowMax = 110;
    public static double blueMin = 185, blueMax = 210;
    private int offset = 0;

    public ITDI2CBrushlandSensor(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    @Override
    public void update() {
        super.update();
        detectionColor = null;
    }

    public DetectionColor getColor() {
        double distance = getDistanceMM();

        if (distance > 152) {
            return DetectionColor.None;
        }

        if (detectionColor == null) {
            detectionColor = HSVToDetectionColor(offset);
        }

        return detectionColor;
    }

    private DetectionColor HSVToDetectionColor(float offset) {
        return HSVToDetectionColor((float) getHSVValues().getH(), offset);
    }

    public static DetectionColor HSVToDetectionColor(float hue, float offset) {
        hue = hue + offset;
        if ((hue >= redMin && hue <= redMax) || (hue >= red1Min && hue <= red1Max)) {
            return DetectionColor.Red;
        } else if (hue >= blueMin && hue <= blueMax) {
            return DetectionColor.Blue;
        } else if (hue >= yellowMin && hue <= yellowMax) {
            return DetectionColor.Yellow;
        }

        return DetectionColor.None;
    }

    public String detectionColorToString() {
        return detectionColorToString(getColor());
    }

    public static String detectionColorToString(DetectionColor detectionColor) {
        if (detectionColor == DetectionColor.Red) {
            return "Red";
        } else if (detectionColor == DetectionColor.Blue) {
            return "Blue";
        } else if (detectionColor == DetectionColor.Yellow) {
            return "Yellow";
        } else {
            return "None";
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}