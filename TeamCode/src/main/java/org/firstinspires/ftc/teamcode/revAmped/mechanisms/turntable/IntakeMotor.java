package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.IntakeState;

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
