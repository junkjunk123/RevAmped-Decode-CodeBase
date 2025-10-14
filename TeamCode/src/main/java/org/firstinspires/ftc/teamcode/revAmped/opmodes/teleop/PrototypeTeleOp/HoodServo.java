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
    private static float farPositionDifference = 0f;
    private static float closePositionDifference = 0f;
    public enum HoodState implements ServoState{
        FAR(0.90f+ farPositionDifference),
        CLOSE(0.0667f + closePositionDifference);

        private float pos;

        @Override
        public float getPosition() {
            return pos;
        }
        HoodState(float pos){
            this.pos = pos;
        }
    }
    public void incrementFarPosition(){
        farPositionDifference += 0.01f;
    }
    public void decrementFarPosition(){
        farPositionDifference -= 0.01f;
    }
    public void incrementClosePosition(){
        closePositionDifference += 0.01f;
    }
    public void decrementClosePosition(){
        closePositionDifference -= 0.01f;
    }
    public float getFarPosition(){
        return farPositionDifference+0.93f;
    }
    public float getClosePosition(){
        return closePositionDifference+0.667f;
    }
}
