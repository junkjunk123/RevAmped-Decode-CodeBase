package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;

public class TurretMotor extends HwMotor2<TurretMotor.TurretState> {
    @Override
    public void buildStatePowerMap() {

        statePowerMap.put(TurretState.STOPPED,MotorPower.constant(0f));
        statePowerMap.put(TurretState.FORWARD,MotorPower.constant(0.3f));
        statePowerMap.put(TurretState.BACKWARD,MotorPower.constant(-0.3f));
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
