package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class HoodServo extends HwServo2<HoodServo.HoodState> {
    public HoodServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", setPosition, HoodState.CLOSE, ServoBrand.AXON_MAX);
    }

    public HoodServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", initialPosition, HoodState.CLOSE, ServoBrand.AXON_MAX);
    }

    public enum HoodState implements ServoState{
        FAR(0.251f),
        CLOSE(0.6196f);

        private float pos;

        @Override
        public float getPosition() {
            return pos;
        }
        HoodState(float pos){
            this.pos = pos;
        }
    }
}
