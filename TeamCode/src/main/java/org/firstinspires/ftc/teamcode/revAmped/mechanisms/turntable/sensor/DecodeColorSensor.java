package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;

public interface DecodeColorSensor extends HwDevice {
    enum ArtifactColor {
        PURPLE,
        GREEN,
        NONE;

        public Vector2D amplitudes() {
            if (this == PURPLE) return new Vector2D(1, 0);
            if (this == GREEN) return new Vector2D(0, 1);
            return new Vector2D(1 / Math.sqrt(2), 1 / Math.sqrt(2));
        }
    }

    ArtifactColor getColor();
    void update();
    void disable();

    static void disableSensors() {
        DualIntakeColor.disableSensors();
    }

    static void enableSensors() {
        DualIntakeColor.enableSensors();
    }
}
