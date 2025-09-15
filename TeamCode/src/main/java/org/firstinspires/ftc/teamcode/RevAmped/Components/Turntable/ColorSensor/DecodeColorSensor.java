package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

public interface DecodeColorSensor {
    enum ArtifactColor {
        PURPLE,
        GREEN,
        NONE
    }

    ArtifactColor getColor();
    void update();
}
