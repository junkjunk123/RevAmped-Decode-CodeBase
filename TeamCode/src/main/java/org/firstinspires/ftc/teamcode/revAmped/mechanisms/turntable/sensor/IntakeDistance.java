package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.util.Mutatable;

public interface IntakeDistance extends HwDevice.Sensor<Double> {
    Mutatable<Float> THRESHOLD_DISTANCE = new Mutatable<>();

    default boolean hasArtifact() {
        return getDistance() < THRESHOLD_DISTANCE.get();
    }

    Double getDistance();
}
