package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwMotor2;

public class ConceptMotor extends HwMotor2<ConceptMotor.State> {
    public enum State implements MotorState{
        FORWARD(1.0f),
        REVERSE(-1.0f),
        STOPPED(0f);


        float power;
        State(float power) {
            this.power = power;
        }

        public float power() {
            return power;
        }
    }

    public ConceptMotor(HardwareMap hardwareMap, State motorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, motorState);
    }

    public ConceptMotor(HardwareMap hardwareMap, DcMotor.RunMode runMode, State motorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, runMode, motorState);
    }

    public ConceptMotor(HardwareMap hardwareMap, double maxPower, State motorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, maxPower, motorState);
    }
}
