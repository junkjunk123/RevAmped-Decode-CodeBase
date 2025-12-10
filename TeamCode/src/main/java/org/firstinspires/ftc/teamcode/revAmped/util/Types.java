package org.firstinspires.ftc.teamcode.revAmped.util;

import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo.HOOD_REST;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.CRServoInternal;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServoWithVoltage;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Hood;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.IntakeDistance;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServoV2;

import java.util.List;

public interface Types {
    enum IntakeState implements HwMotor2.MotorState {
        INTAKE(1.0f),
        STOP(0.0f),
        OUTTAKE(-0.5f),
        SLOW_OUTTAKE(-0.3f);

        final float power;

        IntakeState(float power) {
            this.power = power;
        }

        public float getPower() {
            return power;
        }
    }

    enum PopperState implements HwServo2.ServoState {
        NEUTRAL,
        POP;

        @Override
        public float getPosition() {
            if (this == NEUTRAL) return Popper.NEUTRAL;
            else return Popper.POP;
        }
    }

    enum PopperVoltageState implements HwServoWithVoltage.ServoVoltageState {
        FULLY_UP,
        FULLY_DOWN;

        @Override
        public boolean reached(double voltage) {
            if (this == FULLY_UP) {
                return voltage >= Popper.UP_VOLTAGE;
            }

            return voltage <= Popper.DOWN_VOLTAGE;
        }

        @Override
        public HwServoWithVoltage.VoltageStatePair[] getStates() {
            if (this == FULLY_UP) return new HwServoWithVoltage.VoltageStatePair[] {new HwServoWithVoltage.VoltageStatePair(PopperState.NEUTRAL, PopperState.POP, 500)};
            return new HwServoWithVoltage.VoltageStatePair[] {new HwServoWithVoltage.VoltageStatePair(PopperState.POP, PopperState.NEUTRAL, 500)};
        }
    }

    enum TableState implements CRServoInternal.ServoState {
        REST,
        BALL1,
        BALL2,
        BALL3,
        ABSOLUTE_ROTATION;

        public int getPosition() {
            switch (this) {
                case REST, BALL1 -> {
                    return TableServo.BALL1;
                }
                case BALL2 -> {
                    return TableServo.BALL2;
                }
                case BALL3 -> {
                    return TableServo.BALL3;
                }
                default -> {
                    return 0;
                }
            }
        }

        public static TableState[] relativeStates() {
            return new TableState[] {BALL1, BALL2, BALL3};
        }
    }

    enum FlywheelState implements HwMotor2.MotorState {
        RUNNING,
        STOPPED;
    }

    enum TurretState implements HwMotor2.MotorState {
        IDLE,
        HOLD,
        TRACKING;

        public int getPos() {
            return 0;
        }

        public static int[] getPresets() {
            return new int[] {TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS, 0, TurretMotor.TURRET_POSITIVE_LIMIT_TICKS};
        }
    }

    enum HoodState implements HwServo2.ServoState {
        REST,
        TRACKING,
        NEAR_PRESET,
        FAR_PRESET,
        MED_PRESET;

        @Override
        public float getPosition() {
            switch (this) {
                case TRACKING, REST -> {
                    return HOOD_REST;
                }
                case FAR_PRESET -> {
                    return HoodServo.TELE_FAR_PRESET;
                }
                case NEAR_PRESET -> {
                    return HoodServo.TELE_NEAR_PRESET;
                }
                case MED_PRESET -> {
                    return HoodServo.TELE_MED_PRESET;
                }
            }

            return HOOD_REST;
        }

        public boolean isPreset() {
            return this == NEAR_PRESET || this == FAR_PRESET || this == MED_PRESET;
        }
    }

    enum GateState implements HwServo2.ServoState {
        OPEN(0),
        CLOSED(0);

        final float pos;

        GateState(float pos){this.pos=pos;}

        @Override
        public float getPosition() {
            return pos;
        }
    }

    enum HorizontalMGNState implements HwServo2.ServoState{
        LOADED(0),
        OUT(0);

        final float pos;

        HorizontalMGNState(float pos){this.pos = pos;}

        @Override
        public float getPosition() {
            return pos;
        }
    }

    enum VerticalMGNState implements HwServo2.ServoState {
        LOADED(0),
        OUT(0);

        final float pos;

        VerticalMGNState(float pos){this.pos = pos;}

        @Override
        public float getPosition() {
            return pos;
        }
    }

    enum ShooterState {
        REST,
        TRACKING
    }

    enum TableState2 implements HwServo2.ServoState {
        BALL1,
        BALL2,
        BALL3,
        BALL1_REV2,
        BALL2_REV2,
        BALL1_END,
        BALL2_END,
        BALL3_END;

        public float getPosition() {
            switch (this) {
                case BALL1 -> {
                    return TableServoV2.BALL1;
                }
                case BALL2 -> {
                    return TableServoV2.BALL2;
                }
                case BALL3 -> {
                    return TableServoV2.BALL3;
                }
                case BALL1_END -> {
                    return TableServoV2.BALL1_END;
                }
                case BALL2_END -> {
                    return TableServoV2.BALL2_END;
                }
                case BALL3_END -> {
                    return TableServoV2.BALL3_END;
                }
                case BALL1_REV2 -> {
                    return TableServoV2.BALL1 + fullRotation();
                }
                case BALL2_REV2 -> {
                    return TableServoV2.BALL2 + fullRotation();
                }
            }

            return TableServoV2.BALL1;
        }

        public static float fullRotation() {
            return (BALL2.getPosition() - BALL1.getPosition()) * 3f;
        }

        public static List<TableState2> relativeStates() {
            return List.of(BALL1, BALL2, BALL3);
        }
    }

    record ShooterHardwareComponents(Hood hoodServo, TurretMotor turretMotor, Flywheel flywheel) {
        public void update() {
            hoodServo.update();
            turretMotor.update();
            flywheel.update();
        }
    }

    record QueuerHardwareComponents(TableServoV2 tableServo, HwServo2<Types.PopperState> popper, HwMotor2<Types.IntakeState> intakeMotor, ColorManager intakeColor, IntakeDistance intakeDistance) {
        public void update() {
            intakeColor.update();
            intakeMotor.update();
            tableServo.update();
            popper.update();
            intakeDistance.update();
        }
    }
}
