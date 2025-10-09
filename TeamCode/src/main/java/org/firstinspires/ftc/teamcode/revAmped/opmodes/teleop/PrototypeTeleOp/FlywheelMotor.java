package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;

public class FlywheelMotor extends HwMotor2<FlywheelMotor.FlywheelState> {
    public enum FlywheelState implements MotorState{
        STOPPED,
        FAR,
        CLOSE;
    }
    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelMotor.FlywheelState.STOPPED, MotorPower.constant(0f));
        statePowerMap.put(FlywheelState.FAR,MotorPower.constant(0.95f));
        statePowerMap.put(FlywheelState.CLOSE,MotorPower.constant(0.75f));
    }

    @Override
    public FlywheelState getRest() {
        return FlywheelState.STOPPED;
    }

    public FlywheelMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, FlywheelMotor.FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap,id, direction, DcMotor.RunMode.RUN_USING_ENCODER, motorState);
    }

}
