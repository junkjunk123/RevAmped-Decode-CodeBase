package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import revAmped.components.Hardware.HwDevice;

public interface DecodeColorSensor extends HwDevice {
    enum ArtifactColor {
        PURPLE,
        GREEN,
        NONE
    }

    ArtifactColor getColor();
    void update();
    void disable();
}
