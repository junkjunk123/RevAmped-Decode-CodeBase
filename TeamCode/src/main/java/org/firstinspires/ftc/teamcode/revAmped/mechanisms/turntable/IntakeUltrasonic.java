package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwUltrasonicSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.LinearFunction;

@Config
public class IntakeUltrasonic extends HwUltrasonicSensor {
    public static float DISTANCE_THRESHOLD;

    public IntakeUltrasonic(HardwareMap hardwareMap) {
        super(hardwareMap, "intakeUltrasonic", new LinearFunction(1, 0));
    }

    public boolean hasArtifact() {
        return false;
    }
}
