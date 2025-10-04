package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types.IntakeState;

import revAmped.components.Hardware.HwMotor2;

public class IntakeMotor extends HwMotor2<IntakeState> {
    public IntakeMotor(HardwareMap hardwareMap, IntakeState motorState) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, motorState);
    }

    @Override
    public void buildStatePowerMap() {
        for (IntakeState state : IntakeState.values()) {
            statePowerMap.put(state, MotorPower.constant(state.getPower()));
        }
    }

    @Override
    public IntakeState getRest() {
        return IntakeState.STOP;
    }

    @Override
    public DeviceType getDeviceType() {
        return super.getDeviceType();
    }
}
