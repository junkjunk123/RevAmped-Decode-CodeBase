package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.bylazar.ftcontrol.panels.plugins.html.primitives.P;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwServo2;

public class Popper extends HwServo2<Popper.PopperState> {
    public static double POP_TIME_MILLIS;

    public static float NEUTRAL;
    public static float POP;

    public Popper(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", setPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }

    public Popper(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", initialPosition, PopperState.NEUTRAL, ServoBrand.AXON_MINI);
    }

    public enum PopperState implements HwServo2.ServoState{
        NEUTRAL(Popper.NEUTRAL),
        POP(Popper.POP);
        float position;
        PopperState(float position){this.position = position;}

        @Override
        public float getPosition(){return position;}
    }
}
