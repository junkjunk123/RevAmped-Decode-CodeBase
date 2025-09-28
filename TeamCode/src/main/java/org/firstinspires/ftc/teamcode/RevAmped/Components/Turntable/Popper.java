package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types.PopperState;

import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwServoWithVoltage;

public class Popper extends HwServoWithVoltage<PopperState, Types.PopperVoltageState> {
    public static double POP_TIME_MILLIS;

    public static float NEUTRAL;
    public static float POP;

    public static float UP_VOLTAGE;
    public static float DOWN_VOLTAGE;

    public Popper(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super (
                hardwareMap,
                "popper",
                setPosition,
                PopperState.NEUTRAL,
                new HwAnalogEncoder(hardwareMap, "popperVoltage"),
                ServoBrand.AXON_MINI
        );
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super (
                hardwareMap,
                "popper",
                initialPosition,
                PopperState.NEUTRAL,
                new HwAnalogEncoder(hardwareMap, "popperVoltage"),
                ServoBrand.AXON_MINI
        );
    }
}
