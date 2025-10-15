package org.firstinspires.ftc.teamcode.revAmped.mechanisms.sorter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public class GateServo extends HwServo2<Types.GateState> {
    public GateServo (HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException{
        super(hardwareMap, "", setPosition, null, ServoBrand.GOBILDA_SUPERSPEED);
    }
    public GateServo (HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException{
        super(hardwareMap, "", initialPosition, null, ServoBrand.GOBILDA_SUPERSPEED);
    }
}
