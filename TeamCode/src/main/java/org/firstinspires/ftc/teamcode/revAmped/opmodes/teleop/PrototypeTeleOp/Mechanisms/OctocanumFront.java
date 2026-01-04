package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class OctocanumFront extends HwServo2<OctocanumFront.servoState> {
    public OctocanumFront(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "octo_front", setPosition, servoState.UP, ServoBrand.AXON_MAX);
    }

    public OctocanumFront(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "octo_front", initialPosition, servoState.UP, ServoBrand.AXON_MAX);
    }
    public enum servoState implements ServoState{
        UP,
        DOWN;

        @Override
        public float getPosition() {
            switch (this){
                case UP -> {return 110/255f;}
                case DOWN -> {return 216/255f;}
            };
            return 0.0f;
        }
    }
}
