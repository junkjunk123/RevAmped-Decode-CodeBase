package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.PopperState;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwAnalogEncoder;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServoWithVoltage;

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
                new HwAnalogEncoder(hardwareMap, "popper_voltage"),
                ServoBrand.AXON_MINI
        );
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super (
                hardwareMap,
                "popper",
                initialPosition,
                PopperState.NEUTRAL,
                new HwAnalogEncoder(hardwareMap, "popper_voltage"),
                ServoBrand.AXON_MINI
        );
    }
}
