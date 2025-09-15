package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types.PopperState;

import revAmped.components.Hardware.HwServo2;

public class Popper extends HwServo2<PopperState> {
    public static double POP_TIME_MILLIS;

    public static float NEUTRAL;
    public static float POP;

    public Popper(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", setPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", initialPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }
}
