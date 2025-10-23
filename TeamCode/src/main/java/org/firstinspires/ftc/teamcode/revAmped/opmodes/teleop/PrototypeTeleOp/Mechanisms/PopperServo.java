package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
/*
Eric Zhao
10/20/2025
Popper Servo Hardware class for Prototyping Teleop
 */
public class PopperServo extends HwServo2<PopperServo.PopperState> {
    //Constructors
    public PopperServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", setPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }

    public PopperServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "popper", initialPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }
    //State enum
    public enum PopperState implements ServoState{
        POP(0.2824f),
        DOWN(0.4314f);

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
