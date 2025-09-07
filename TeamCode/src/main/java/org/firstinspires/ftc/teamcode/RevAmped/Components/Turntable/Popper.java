package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwServo2;

public class Popper extends HwServo2<Popper.PopperState> {

    public Popper(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", setPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", initialPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }

    public enum PopperState implements HwServo2.ServoState{
        NEUTRAL(0f),
        POP(0f);
        float position;
        PopperState(float position){this.position = position;}

        @Override
        public float getPosition(){return position;}
    }
}
