package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeClawServo extends HwServo2<IntakeClawServo.ClawState> {
    public enum ClawState implements ServoState {
        OPEN (Constants.INTAKE_CLAW_OPEN),
        CLOSE (Constants.INTAKE_CLAW_CLOSE);

        float position;
        
        ClawState(float position) {
            this.position = position;    
        }
        
        @Override
        public float getPosition() {
            return position;
        }
    }
    
    public IntakeClawServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, ClawState.OPEN, ServoBrand.AXON_MINI);
    }

    public IntakeClawServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, ClawState.OPEN, ServoBrand.AXON_MINI);
    }

    public void open() {
        setCurrentState(ClawState.OPEN);
    }

    public void close() {
        setCurrentState(ClawState.CLOSE);
    }
}
