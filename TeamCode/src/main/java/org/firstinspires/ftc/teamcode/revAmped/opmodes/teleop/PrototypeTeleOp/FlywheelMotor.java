package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.hardware.HwMotor2;

public class FlywheelMotor extends HwMotor2<FlywheelMotor.FlywheelState> {
    public enum FlywheelState implements MotorState{
        STOPPED,
        RUNNING;
    }
    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelMotor.FlywheelState.STOPPED, MotorPower.constant(0f));
        statePowerMap.put(FlywheelState.RUNNING,MotorPower.constant(1f));
    }

    @Override
    public FlywheelState getRest() {
        return FlywheelState.STOPPED;
    }

    public FlywheelMotor(HardwareMap hardwareMap, String id, FlywheelMotor.FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap,id, DcMotorSimple.Direction.REVERSE, motorState);
    }

}
