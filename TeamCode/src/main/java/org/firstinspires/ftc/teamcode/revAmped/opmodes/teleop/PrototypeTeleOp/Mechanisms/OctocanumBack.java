package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;

public class OctocanumBack extends HwServo2<OctocanumBack.servoState> {
    public OctocanumBack(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "octo_back", setPosition, servoState.UP, ServoBrand.AXON_MAX);
    }

    public OctocanumBack(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "octo_back", initialPosition, servoState.UP, ServoBrand.AXON_MAX);
    }
    public enum servoState implements ServoState{
        UP,
        DOWN;

        @Override
        public float getPosition() {
            switch (this){
                case UP -> {return 56/255f;}
                case DOWN -> {return 162/255f;}
            };
            return 0.0f;
        }
    }
}
