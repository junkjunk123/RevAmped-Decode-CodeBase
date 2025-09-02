package org.firstinspires.ftc.teamcode.UG.Depo;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwMotor2;

public class FlywheelMotor extends HwMotor2<FlywheelMotor.FlywheelMotorState> {
    public enum FlywheelMotorState implements MotorState{
        MAX_POWER(1.0f),
        HIGH_POWER(0.7f),
        MED_POWER(0.5f),
        LOW_POWER(0.3f),
        STOPPED(0f);

        float power;
        FlywheelMotorState(float power){
            this.power = power;
        }

        public float power(){
            return power;
        }
    }

    public FlywheelMotor(HardwareMap hardwareMap, FlywheelMotorState flywheelMotorState) throws IllegalArgumentException{
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, flywheelMotorState);
    }
    public FlywheelMotor(HardwareMap hardwareMap, DcMotor.RunMode runMode, FlywheelMotorState flywheelMotorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, runMode, flywheelMotorState);
    }

    public FlywheelMotor(HardwareMap hardwareMap, double maxPower, FlywheelMotorState flywheelMotorState) throws IllegalArgumentException {
        super(hardwareMap, "motor", DcMotorSimple.Direction.FORWARD, maxPower, flywheelMotorState);
    }
}
