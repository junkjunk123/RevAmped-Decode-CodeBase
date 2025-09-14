package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

public interface Color {
    enum ArtifactColor {
        PURPLE,
        GREEN,
        NONE
    }

    ArtifactColor getColor();

    void update();
}
