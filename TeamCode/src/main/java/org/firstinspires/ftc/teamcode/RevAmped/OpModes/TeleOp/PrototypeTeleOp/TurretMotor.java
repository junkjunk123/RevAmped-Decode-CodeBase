package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.Turret;

public class TurretMotor extends HwMotor2<TurretMotor.TurretState> {
    @Override
    public void buildStatePowerMap() {

        statePowerMap.put(TurretState.STOPPED,MotorPower.constant(0f));
        statePowerMap.put(TurretState.FORWARD,MotorPower.constant(0.5f));
        statePowerMap.put(TurretState.BACKWARD,MotorPower.constant(-0.5f));
    }

    @Override
    public TurretState getRest() {
        return TurretState.STOPPED;
    }

    public enum TurretState implements MotorState{
        STOPPED,
        FORWARD,
        BACKWARD;
    }
    public TurretMotor(HardwareMap hardwareMap) throws IllegalArgumentException {
        super(hardwareMap, "turret", DcMotorSimple.Direction.FORWARD, TurretState.STOPPED);
    }
}
