package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.TwoMotorMechanism;

public class Flywheel extends TwoMotorMechanism<Flywheel.FlywheelState> {
    public Flywheel(HardwareMap hardwareMap, String id, String id2, DcMotorSimple.Direction direction, DcMotorSimple.Direction direction2, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, id, id2, direction, direction2, motorState);
    }

    public static double FLYWHEEL_MAX_VELOCITY;
    public enum FlywheelState implements MotorState{
        RUNNING(1f),
        STOPPED(0f);

        float power;
        @Override
        public float power() {
            return power;
        }
        FlywheelState(float power){
            this.power = power;
        }

    }
}
