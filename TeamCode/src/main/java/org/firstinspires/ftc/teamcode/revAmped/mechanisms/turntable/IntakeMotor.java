package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.CalcHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.IntakeState;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class IntakeMotor extends HwMotor2<IntakeState> {
    private final HwVoltageSensor voltageSensor;
    private boolean useVoltageCompensation = false;

    public IntakeMotor(HardwareMap hardwareMap, IntakeState motorState, HwVoltageSensor voltageSensor) throws IllegalArgumentException {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, motorState);
        this.voltageSensor = voltageSensor;
    }

    public IntakeMotor(HardwareMap hardwareMap, IntakeState motorState) {
        super(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE, motorState);
        voltageSensor = null;
    }

    @Override
    public void buildStatePowerMap() {
        for (IntakeState state : IntakeState.values()) {
            statePowerMap.put(state, () -> {
                if (state.getPower() == 0f || !useVoltageCompensation) {
                    return state.getPower();
                } else {
                    float compensatedPower = state.getPower() * voltageSensor.getVoltageNormalized(12.75f);
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

    public void setUseVoltageCompensation(boolean useVoltageCompensation) {
        this.useVoltageCompensation = useVoltageCompensation;
    }

    @Override
    public DelayedResult<Boolean> testResponse(Consumer<String> telemetry) {
        setCurrentState(IntakeState.INTAKE, 0.5f);
        CalcHandler integrator = new CalcHandler();
        double nominalCurrent = motor.getCurrent(CurrentUnit.AMPS);
        BooleanSupplier timeout = MathUtil.booleanSupplierfromTimeDelay(5000);
        return new DelayedResult<>(() -> {
            if (!timeout.getAsBoolean()) {
                integrator.update(Math.abs(nominalCurrent - motor.getCurrent(CurrentUnit.AMPS)));
                return null;
            } else {
                setCurrentState(IntakeState.STOP);
                return integrator.getIntegral() > 0.08;
            }
        });
    }
}
