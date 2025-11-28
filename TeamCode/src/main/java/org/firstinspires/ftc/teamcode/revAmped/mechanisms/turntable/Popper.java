package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.PopperState;

public class Popper extends HwServo2<PopperState> {
    public static double POP_TIME_MILLIS;

    public static float NEUTRAL;
    public static float POP;
    public static float POP_LAST;

    public static float UP_VOLTAGE;
    public static float DOWN_VOLTAGE;

    public Popper(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super (
                hardwareMap,
                "popper",
                setPosition,
                PopperState.NEUTRAL,
                ServoBrand.AXON_MINI
        );
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super (
                hardwareMap,
                "popper",
                initialPosition,
                PopperState.NEUTRAL,
                ServoBrand.AXON_MINI
        );
    }
}
