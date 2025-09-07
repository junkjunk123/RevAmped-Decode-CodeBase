package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.TwoMotorMechanism;
import revAmped.components.SlideController.PIDF.PIDFController;

public class Flywheel extends TwoMotorMechanism<Flywheel.FlywheelState> {
    private final PIDFController controller = new PIDFController(new PIDFCoefficients(0f,0f,0f,0f));
    public Flywheel(HardwareMap hardwareMap, String id, String id2, DcMotorSimple.Direction direction, DcMotorSimple.Direction direction2, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, id, id2, direction, direction2, motorState);
    }

    public static double FLYWHEEL_MAX_VELOCITY;
    private double targetVelocity;

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) controller.run(targetVelocity - motor.getVelocity()));
    }

    public enum FlywheelState implements MotorState {
        RUNNING(),
        STOPPED();
    }
    public void run(double targetVelocity){
        this.targetVelocity = targetVelocity;
        setMotorState(FlywheelState.RUNNING);
    }


}
