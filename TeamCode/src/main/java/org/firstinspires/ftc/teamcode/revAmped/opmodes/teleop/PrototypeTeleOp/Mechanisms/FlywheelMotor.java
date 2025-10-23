package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
/*
Eric Zhao
10/20/2025
Flywheel Motor Hardware class for Prototyping Teleop
Currently Out of use
 */

public class FlywheelMotor extends HwMotor2<FlywheelMotor.FlywheelState> {
    //Flywheel Enum
    public enum FlywheelState implements MotorState{
        STOPPED,
        FAR,
        CLOSE;
    }

    //Assigning motor powers to enum values
    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelMotor.FlywheelState.STOPPED, MotorPower.constant(0f));
        statePowerMap.put(FlywheelState.FAR,MotorPower.constant(0.90f));
        statePowerMap.put(FlywheelState.CLOSE,MotorPower.constant(0.67f));
    }

    //Mandatory Methods
    @Override
    public FlywheelState getRest() {
        return FlywheelState.STOPPED;
    }

    //Constructor
    public FlywheelMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, FlywheelMotor.FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, id, direction, DcMotor.RunMode.RUN_USING_ENCODER, motorState);
    }
}
