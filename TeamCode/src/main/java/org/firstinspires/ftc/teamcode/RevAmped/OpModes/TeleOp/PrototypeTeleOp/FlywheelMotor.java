package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwSlide2;
import revAmped.components.Hardware.TwoMotorMechanism;

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
    public FlywheelMotor(HardwareMap hardwareMap,String id, FlywheelMotor.FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap,id, DcMotorSimple.Direction.REVERSE, motorState);
    }
}
