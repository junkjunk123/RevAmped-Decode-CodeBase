package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.hardware.HwMotor2;

public class IntakeMotor extends HwMotor2<IntakeMotor.IntakeState> {
    public IntakeMotor(HardwareMap hardwareMap, double maxPower) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, maxPower, IntakeState.STOPPED);
    }

    public IntakeMotor(HardwareMap hardwareMap, DcMotor.RunMode runMode) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, runMode, IntakeState.STOPPED);
    }

    public IntakeMotor(HardwareMap hardwareMap) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, IntakeState.STOPPED);
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(IntakeState.INTAKE,MotorPower.constant(1f));
        statePowerMap.put(IntakeState.OUTTAKE,MotorPower.constant(-1f));
        statePowerMap.put(IntakeState.STOPPED,MotorPower.constant(0f));
    }

    @Override
    public IntakeState getRest() {
        return IntakeState.STOPPED;
    }

    public enum IntakeState implements MotorState{
        INTAKE,
        OUTTAKE,
        STOPPED;
    }
}
