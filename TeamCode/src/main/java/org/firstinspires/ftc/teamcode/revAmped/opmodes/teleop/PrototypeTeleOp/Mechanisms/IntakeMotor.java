package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
/*
Eric Zhao
10/20/2025
Intake Motor Hardware class for Prototyping Teleop
 */
public class IntakeMotor extends HwMotor2<IntakeMotor.IntakeState> {
    //Constructors
    public IntakeMotor(HardwareMap hardwareMap, double maxPower) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, maxPower, IntakeState.STOPPED);
    }

    public IntakeMotor(HardwareMap hardwareMap, DcMotor.RunMode runMode) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, runMode, IntakeState.STOPPED);
    }

    public IntakeMotor(HardwareMap hardwareMap) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, IntakeState.STOPPED);
    }

    //Assigning motor powers to enum values
    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(IntakeState.INTAKE,MotorPower.constant(1f));
        statePowerMap.put(IntakeState.OUTTAKE,MotorPower.constant(-1f));
        statePowerMap.put(IntakeState.STOPPED,MotorPower.constant(0f));
    }

    //Mandatory methods
    @Override
    public IntakeState getRest() {
        return IntakeState.STOPPED;
    }

    //State enum
    public enum IntakeState implements MotorState{
        INTAKE,
        OUTTAKE,
        STOPPED;
    }
}
