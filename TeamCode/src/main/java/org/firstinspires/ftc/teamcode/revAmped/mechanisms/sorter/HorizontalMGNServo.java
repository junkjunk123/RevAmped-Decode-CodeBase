package org.firstinspires.ftc.teamcode.revAmped.mechanisms.sorter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class HorizontalMGNServo extends HwServo2<Types.HorizontalMGNState> {
    public HorizontalMGNServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "", setPosition, null, ServoBrand.AXON_MINI);
    }

    public HorizontalMGNServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "", initialPosition, null, ServoBrand.AXON_MINI);
    }
}
