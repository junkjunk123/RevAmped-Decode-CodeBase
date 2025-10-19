package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.IntakeState;

public class IntakeMotor extends HwMotor2<IntakeState> {
    private final HwVoltageSensor voltageSensor;
    private boolean useVoltageCompensation = false;

    public IntakeMotor(HardwareMap hardwareMap, IntakeState motorState, HwVoltageSensor voltageSensor) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, motorState);
        this.voltageSensor = voltageSensor;
    }

    @Override
    public void buildStatePowerMap() {
        for (IntakeState state : IntakeState.values()) {
            statePowerMap.put(state, () -> {
                if (state.getPower() == 0f || !useVoltageCompensation) {
                    return state.getPower();
                } else {
                    float compensatedPower = state.getPower() * voltageSensor.getVoltageNormalized();
                    compensatedPower = Range.clip(compensatedPower, -1f, 1f);
                    return compensatedPower;
                }
            });
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

    @Override
    public void setVoltageCompensation() {
        useVoltageCompensation = true;
    }
}
