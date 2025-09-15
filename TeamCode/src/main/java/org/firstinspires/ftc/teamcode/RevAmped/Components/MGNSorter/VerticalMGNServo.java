package org.firstinspires.ftc.teamcode.RevAmped.Components.MGNSorter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.Hardware.HwServo2;

public class VerticalMGNServo extends HwServo2<Types.VerticalMGNState> {

    public VerticalMGNServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "", setPosition, null, ServoBrand.AXON_MINI);
    }

    public VerticalMGNServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "", initialPosition, null, ServoBrand.AXON_MINI);
    }
}
