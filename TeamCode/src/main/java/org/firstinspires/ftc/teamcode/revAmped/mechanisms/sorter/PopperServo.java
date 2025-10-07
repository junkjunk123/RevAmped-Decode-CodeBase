package org.firstinspires.ftc.teamcode.revAmped.mechanisms.sorter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class PopperServo extends HwServo2<PopperServo.PopperState> {

    public PopperServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "", initialPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }

    public PopperServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "", setPosition, PopperState.DOWN, ServoBrand.AXON_MINI);
    }

    public enum PopperState implements ServoState{
        DOWN(0),
        POP(0);

        private float pos;

        PopperState(float pos){
            this.pos = pos;
        }

        @Override
        public float getPosition() {
            return pos;
        }
    }
}
