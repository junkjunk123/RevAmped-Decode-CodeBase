package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwSwitch;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Turret;
import org.firstinspires.ftc.teamcode.revAmped.components.math.CalcHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TurretState;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Config
public class TurretMotor extends Turret<TurretState> {
    public TurretMotor(HardwareMap hardwareMap, TurretState motorState) throws IllegalArgumentException {
        super(hardwareMap, "turret", DcMotorSimple.Direction.REVERSE, motorState);
        if (useTurretSwitch)
            this.limitSwitch = new HwSwitch(hardwareMap, "turret_switch", true, true);
        else this.limitSwitch = null;
        controller = new PIDFController(TURRET_PIDF_COEFFICIENTS);
    }

    public static PIDFCoefficients TURRET_PIDF_COEFFICIENTS;
    public final PIDFController controller;
    public static int TURRET_TICKS_PER_ROTATION;
    private int targetPosition;
    public static int AUTO_PRELOADS;
    public static int AUTO_SET_1;
    public static int AUTO_SET_2;
    public static int AUTO_SET_3;
    public static int FAR_AUTO;
    public static float LL_DETECTION_RED;
    public static float LL_DETECTION_BLUE;
    public static boolean TURRET_LIMITS_ENABLED;
    public static int TURRET_POSITIVE_LIMIT_TICKS;
    public static int TURRET_NEGATIVE_LIMIT_TICKS;
    public static double TURRET_RAD_LIMIT;
    public final HwSwitch limitSwitch;
    private double prevFineTuneAngle = 0;
    public static boolean useTurretSwitch = true;

    public enum PresetState {
        LEFT_135,
        LEFT_90,
        LEFT_45,
        RESET,
        RIGHT_45,
        RIGHT_90,
        RIGHT_135;

        public int pos() {
            switch (this) {
                case LEFT_90 -> {
                    return TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS;
                }

                case LEFT_45 -> {
                    return TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS / 2;
                }

                case RIGHT_45 -> {
                    return TurretMotor.TURRET_POSITIVE_LIMIT_TICKS / 2;
                }

                case RIGHT_90 -> {
                    return TurretMotor.TURRET_POSITIVE_LIMIT_TICKS;
                }

                case LEFT_135 -> {
                    return (int) (TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS * 1.5);
                }

                case RIGHT_135 -> {
                    return (int) (TurretMotor.TURRET_POSITIVE_LIMIT_TICKS * 1.5);
                }

                default -> {
                    return 0;
                }
            }
        }
    }

    private PresetState presetState = PresetState.RESET;

    @Override
    public int ticksPerRotation() {
        return (int) ((TURRET_POSITIVE_LIMIT_TICKS) * (Math.PI * 2 / TURRET_RAD_LIMIT));
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TurretState.IDLE, MotorPower.constant(0f));
        statePowerMap.put(TurretState.HOLD, () -> {
            float mult = targetPosition == 0 ? 0.5f : 1f;
            float val = (float) controller.runWithError(!TURRET_LIMITS_ENABLED ? getError(targetPosition) : targetPosition - getCurrentPosition());
            return val * mult;
        });
        statePowerMap.put(TurretState.TRACKING, () -> (float) controller.runWithError(!TURRET_LIMITS_ENABLED ? getError(targetPosition) : targetPosition - getCurrentPosition()));
    }

    @Override
    public TurretState getRest() {
        return TurretState.IDLE;
    }

    @Override
    public void setCurrentState(TurretState motorState) {
        super.setCurrentState(motorState);
        targetPosition = motorState.getPos();
        controller.reset();
        prevFineTuneAngle = 0;
        presetState = PresetState.RESET;
    }

    public void resetTurret() {
        resetTurret(getCurrentPosition());
    }

    public void resetTurret(int currentPos) {
        Scheduler.getInstance().add(new Delay(limitSwitch::isTouch, () -> {
            resetPosition();
            setCurrentState(TurretState.HOLD);
        }).setCancellation(1500, () -> setCurrentState(TurretState.HOLD)));
        hold(-100 * (int) Math.signum(currentPos));
    }

    public void setCurrentState(TurretState motorState, int targetPosition) {
        super.setCurrentState(motorState);
        this.targetPosition = targetPosition;
        controller.reset();
        prevFineTuneAngle = 0;
        presetState = PresetState.RESET;
    }
    
    public void finetuneAngle(double rad) {
        prevFineTuneAngle = rad;
        targetPosition = Range.clip((int) (rad * TURRET_POSITIVE_LIMIT_TICKS / TURRET_RAD_LIMIT), TURRET_NEGATIVE_LIMIT_TICKS, TURRET_POSITIVE_LIMIT_TICKS);
        motorState = TurretState.TRACKING;
    }

    public void fineTuneTicks(int ticks) {
        targetPosition += ticks;
        motorState = TurretState.HOLD;
    }

    public void limelightDetection() {
        switch (Globals.allianceColor) {
            case Blue -> setCurrentState(TurretState.HOLD, LL_DETECTION_BLUE);
            case Red -> setCurrentState(TurretState.HOLD, LL_DETECTION_RED);
        }
    }

    public double getPrevFineTuneAngle() {
        return prevFineTuneAngle;
    }

    @Override
    public int getTargetPosition() {
        return targetPosition;
    }

    public HwSwitch getLimitSwitch() {
        return limitSwitch;
    }

    public PresetState getPresetState() {
        return presetState;
    }

    @Override
    public void update() {
        limitSwitch.update();
        super.update();
    }

    public void hold(int targetPosition) {
        setCurrentState(TurretState.HOLD);
        this.targetPosition = targetPosition;
    }

    public void setPresetState(PresetState presetState) {
        if (motorState == TurretState.HOLD)
            targetPosition = presetState.pos();
        else
            setCurrentState(TurretState.HOLD, targetPosition);
        this.presetState = presetState;
    }

    public void nextPresetState() {
        if (presetState != PresetState.RIGHT_135)
            setPresetState(PresetState.values()[(presetState.ordinal() + 1) % PresetState.values().length]);
    }

    public void previousPresetState() {
        int length = PresetState.values().length;
        if (presetState != PresetState.LEFT_135)
            setPresetState(PresetState.values()[(presetState.ordinal() - 1 + length) % length]);
    }

    @Override
    public DelayedResult<Boolean> testResponse(Consumer<String> telemetry) {
        hold(500);
        CalcHandler integrator = new CalcHandler();
        double nominalCurrent = motor.getCurrent(CurrentUnit.AMPS);
        BooleanSupplier timeout1 = MathUtil.booleanSupplierfromTimeDelay(2500);
        BooleanSupplier timeout2 = MathUtil.booleanSupplierfromTimeDelay(5000);
        AtomicBoolean changedTarget = new AtomicBoolean(false);
        AtomicBoolean limitSwitchWorks = new AtomicBoolean(true);
        return new DelayedResult<>(() -> {
            if (!timeout1.getAsBoolean()) {
                integrator.update(Math.abs(nominalCurrent - motor.getCurrent(CurrentUnit.AMPS)));
                return null;
            } else if (!timeout2.getAsBoolean() && !changedTarget.get()) {
                if (limitSwitch.isTouch())
                    limitSwitchWorks.set(false);
                changedTarget.set(true);
                setCurrentState(TurretState.HOLD);
                return null;
            } else if (!timeout2.getAsBoolean()) {
                return null;
            } else {
                if (!limitSwitch.isTouch())
                    limitSwitchWorks.set(false);
                telemetry.accept("turret switch -> " + limitSwitchWorks.get());
                setCurrentState(TurretState.IDLE);
                return integrator.getIntegral() > 0.08;
            }
        });
    }
}
