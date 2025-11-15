package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwSwitch;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Turret;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TurretState;

@Config
public class TurretMotor extends Turret<TurretState> {
    public TurretMotor(HardwareMap hardwareMap, TurretState motorState) throws IllegalArgumentException {
        super(hardwareMap, "turret", DcMotorSimple.Direction.REVERSE, motorState);
        if (useTurretSwitch) this.limitSwitch = new HwSwitch(hardwareMap, "turret_switch", true, false);
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

    private PresetState presetState=PresetState.RESET;

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
        if (this.motorState != TurretState.HOLD && this.motorState != TurretState.IDLE && motorState == TurretState.HOLD && useTurretSwitch)
            Scheduler.getInstance().add(new Delay(limitSwitch::isTouch, this::resetPosition).setCancellation(1500));
        targetPosition = motorState.getPos();
        controller.reset();
        prevFineTuneAngle = 0;
        presetState = PresetState.RESET;
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
}
