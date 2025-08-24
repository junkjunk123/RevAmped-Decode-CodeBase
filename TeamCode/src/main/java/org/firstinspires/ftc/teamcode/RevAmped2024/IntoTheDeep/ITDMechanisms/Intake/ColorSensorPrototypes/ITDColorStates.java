package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes;

public class ITDColorStates implements ITDColor {
    private final ITDBrushlandColorSensor primaryColorSensor; //LEFT
    private final ITDBrushlandColorSensor secondaryColorSensor; //RIGHT
    private DetectionColor color = DetectionColor.None;

    public ITDColorStates(ITDBrushlandColorSensor primaryColorSensor, ITDBrushlandColorSensor secondaryColorSensor) {
        this.primaryColorSensor = primaryColorSensor;
        this.secondaryColorSensor = secondaryColorSensor;
    }

    public void update() {
        color = null;
        primaryColorSensor.update();
        secondaryColorSensor.update();
    }

    public DetectionColor getColor() {
        if (color == null) {
            color = updateColor();
        }

        return color;
    }

    public String detectionColorToString() {
        return getColor().name();
    }

    public ITDBrushlandColorSensor getPrimaryColorSensor() {
        return primaryColorSensor;
    }

    public ITDBrushlandColorSensor getSecondaryColorSensor() {
        return secondaryColorSensor;
    }

    public DetectionColor updateColor() {
        DetectionColor primaryColor = primaryColorSensor.getColor();
        DetectionColor secondaryColor = secondaryColorSensor.getColor();

        if (primaryColor == DetectionColor.Red || secondaryColor == DetectionColor.Red) {
            return DetectionColor.Red;
        }

        if (primaryColor == DetectionColor.Blue || secondaryColor == DetectionColor.Blue) {
            return DetectionColor.Blue;
        }

        if (primaryColor == DetectionColor.Yellow) {
            if (secondaryColor == DetectionColor.Yellow) {
                return DetectionColor.Yellow;
            } else if (secondaryColor == DetectionColor.None) {
                return DetectionColor.Red;
            }
        }

        if (secondaryColor == DetectionColor.Yellow) {
            return DetectionColor.Yellow;
        }

        return DetectionColor.None;
    }
}
