package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.HoodState;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class HoodServo extends HwServo2<Types.HoodState> {
    public HoodServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", setPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public HoodServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", initialPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public static float HOOD_REST;
    public static float HOOD_MAX_POS;
    public static float HOOD_MIN_POS;
    public static float HOOD_MAX_RAD;
    public static float HOOD_MIN_RAD;

    public static float AUTO_PRELOADS;
    public static float AUTO_SET_1;
    public static float AUTO_SET_2;
    public static float AUTO_SET_3;

    public static float TELE_NEAR_PRESET;
    public static float TELE_FAR_PRESET;
}
