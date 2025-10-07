package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ColorSensor;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;

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
