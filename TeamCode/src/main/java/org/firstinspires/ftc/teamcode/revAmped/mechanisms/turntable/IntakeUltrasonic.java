package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwUltrasonicSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.LowPassFilter;
import org.firstinspires.ftc.teamcode.revAmped.components.math.LinearFunction;

@LowPassFilter(alpha = 0.75)
@Config
public class IntakeUltrasonic extends HwUltrasonicSensor {
    public static double DISTANCE_THRESHOLD = 3.0; // in INCHES

    public IntakeUltrasonic(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id, new LinearFunction(1, 0));
    }

    public boolean hasArtifact() {
        return getDistance() < DISTANCE_THRESHOLD;
    }
}
