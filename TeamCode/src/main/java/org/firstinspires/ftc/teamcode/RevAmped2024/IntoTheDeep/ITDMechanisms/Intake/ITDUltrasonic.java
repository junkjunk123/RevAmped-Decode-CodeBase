package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwUltrasonicSensor;
import revAmped.components.MathUtil.LinearFunction;

public class ITDUltrasonic extends HwUltrasonicSensor {
    private static double MAX_DIST_TO_SUB = 2;
    private static double BRAKE_DISTANCE = 12;

    public ITDUltrasonic(HardwareMap hardwareMap, String id) {
        super(hardwareMap, id);
    }

    public ITDUltrasonic(HardwareMap hardwareMap, String id, LinearFunction sensorConversion) {
        super(hardwareMap, id, sensorConversion);
    }

    public boolean atSub() {
        return getDistance() < MAX_DIST_TO_SUB;
    }

    public boolean brake() {
        return getDistance() < BRAKE_DISTANCE;
    }
}
