package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import revAmped.components.Hardware.HwServo2;

public class IntakeWristServo extends HwServo2<IntakeWristServo.WristState> {
    public IntakeWristServo(HardwareMap hardwareMap, String id, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, WristState.TRANSFER, ServoBrand.AXON_MINI);
    }

    public IntakeWristServo(HardwareMap hardwareMap, String id, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, WristState.TRANSFER, ServoBrand.AXON_MINI);
    }

    public enum WristState implements HwServo2.ServoState {
        INTAKE(Constants.INTAKE_WRIST_INTAKE),
        TRANSFER(Constants.INTAKE_WRIST_TRANSFER),
        DROP_TO_HUMAN_PLAYER(Constants.INTAKE_WRIST_DROP_TO_HUMAN_PLAYER),
        INTAKE_45(Constants.INTAKE_WRIST_45),
        INTAKE_135(Constants.INTAKE_WRIST_135),
        INTAKE_PERP(Constants.INTAKE_WRIST_PERP);

        float position;

        WristState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    public void setAngleFromIntakeNeutralRad(double angle) {
        setCurrentState(WristState.INTAKE, 0f);
    }
}
