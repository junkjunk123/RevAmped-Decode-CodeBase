package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwMotor2;

public class IntakeMotor extends HwMotor2<IntakeMotor.IntakeState> {
    protected IntakeMotor(HardwareMap hardwareMap, IntakeState motorState) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.FORWARD, motorState);
    }

    @Override
    public void buildStatePowerMap() {
        for (IntakeState state : IntakeState.values()) {
            statePowerMap.put(state, () -> state.power);
        }
    }

    @Override
    public DeviceType getDeviceType() {
        return super.getDeviceType();
    }

    public enum IntakeState implements HwMotor2.MotorState{
        IN(1.0f),
        OUT(-1.0f),
        STOP(0.0f);

        public final float power;

        IntakeState(float power) {
            this.power = power;
        }
    }
}
