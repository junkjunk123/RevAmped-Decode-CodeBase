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
    private float farPowerDifference = 0f;
    private float closePowerDifference = 0f;
    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelMotor.FlywheelState.STOPPED, MotorPower.constant(0f));
        statePowerMap.put(FlywheelState.FAR,MotorPower.constant(0.90f + farPowerDifference));
        statePowerMap.put(FlywheelState.CLOSE,MotorPower.constant(0.67f + closePowerDifference));
    }

    @Override
    public FlywheelState getRest() {
        return FlywheelState.STOPPED;
    }

    public FlywheelMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, FlywheelMotor.FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap,id, direction, DcMotor.RunMode.RUN_USING_ENCODER, motorState);
    }
    public void incrementFarPower(){
        farPowerDifference += 0.05f;
    }
    public void decrementFarPower(){
        farPowerDifference -= 0.05f;
    }
    public void incrementClosePower(){
        closePowerDifference += 0.05f;
    }
    public void decrementClosePower(){
        closePowerDifference -= 0.05f;
    }
    public float getFarPower(){
        return farPowerDifference + 0.95f;
    }
    public float getClosePower(){
        return closePowerDifference + 0.67f;
    }

}
