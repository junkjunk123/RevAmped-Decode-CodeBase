package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.Hardware.HwServo2;

public class PopperServo extends HwServo2<PopperServo.PopperState> {

    public PopperServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", setPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }

    public PopperServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", initialPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }

    public enum PopperState implements ServoState{
        POP(0.302f),
        DOWN(0.4392f);

        private float pos;

        PopperState(float pos){
            this.pos=pos;
        }

        @Override
        public float getPosition() {
            return pos;
        }
    }
}
