package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwColorSensor;

@Config
public class ITDColorSensor extends HwColorSensor implements ITDColor {
    private DetectionColor detectionColor = DetectionColor.None;
    public static double redMin = 10, redMax = 35;
    public static double yellowMin = 65, yellowMax = 90;
    public static double blueMin = 205, blueMax = 235;
    public static double maxDistance = 35, maxBlueDistance = 59.5;
    private int offset = 0;

    public ITDColorSensor(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    @Override
    public void update() {
        super.update();
        detectionColor = null;
    }

    public DetectionColor getColor(float distanceMax) {
        if (detectionColor == null) {
            double distance = getDistanceMM();

            if (distance > distanceMax) {
                detectionColor = DetectionColor.None;
                return DetectionColor.None;
            }

            detectionColor = HSVToDetectionColor(offset);
        }

        return detectionColor;
    }

    public DetectionColor getColor() {
        if (detectionColor == null) {
            double distance = getDistanceMM();
            if (distance > maxDistance) {
                if (distance > maxBlueDistance || HSVToDetectionColor(offset) != DetectionColor.Blue) {
                    detectionColor = DetectionColor.None;
                } else {
                    detectionColor = DetectionColor.Blue;
                }

                return detectionColor;
            }

            detectionColor = HSVToDetectionColor(offset);
        }

        return detectionColor;
    }

    private DetectionColor HSVToDetectionColor(float offset) {
        return HSVToDetectionColor((float) getHSVValues().getH(), offset);
    }

    public static DetectionColor HSVToDetectionColor(float hue, float offset) {
        hue = hue + offset;

        if (hue >= redMin && hue <= redMax) {
            return DetectionColor.Red;
        } else if (hue >= blueMin && hue <= blueMax) {
            return DetectionColor.Blue;
        } else if (hue >= yellowMin && hue <= yellowMax) {
            return DetectionColor.Yellow;
        }

        return DetectionColor.None;
    }

    public String detectionColorToString() {
        return detectionColorToString(getColor(0));
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