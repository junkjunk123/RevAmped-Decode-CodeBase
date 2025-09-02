package org.firstinspires.ftc.teamcode.UG.Intake;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwMotor2;

public class IntakeMotor extends HwMotor2<IntakeMotor.IntakeMotorState> {
    public enum IntakeMotorState implements MotorState{
        INTAKE(1.0f),
        OUTTAKE(-1.0f),
        STOPPED(0.0f);


        float power;
        IntakeMotorState(float power){
            this.power = power;
        }

        public float power(){
            return power;
        }
    }

    public IntakeMotor(HardwareMap hardwareMap, IntakeMotorState intakeMotorState) throws IllegalArgumentException{
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, intakeMotorState);
    }
    public IntakeMotor(HardwareMap hardwareMap, DcMotor.RunMode runMode, IntakeMotorState intakeMotorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, runMode, intakeMotorState);
    }

    public IntakeMotor(HardwareMap hardwareMap, double maxPower, IntakeMotorState intakeMotorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, maxPower, intakeMotorState);
    }
}
