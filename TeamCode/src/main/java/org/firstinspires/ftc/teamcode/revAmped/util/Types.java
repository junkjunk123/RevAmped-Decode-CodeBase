package org.firstinspires.ftc.teamcode.revAmped.util;

import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo.HOOD_REST;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.CRServoInternal;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServoWithVoltage;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Hood;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;

public interface Types {
    enum IntakeState implements HwMotor2.MotorState {
        INTAKE(1.0f),
        STOP(0.0f),
        OUTTAKE(-1.0f);

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
        BALL3;

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
            }

            return 0;
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
    }

    enum HoodState implements HwServo2.ServoState {
        REST,
        TRACKING,
        NEAR_PRESET,
        FAR_PRESET;

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
            }

            return HOOD_REST;
        }

        public boolean isPreset() {
            return this == NEAR_PRESET || this == FAR_PRESET;
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

    enum VerticalMGNState implements HwServo2.ServoState{
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

    record ShooterHardwareComponents(Hood hoodServo, TurretMotor turretMotor, Flywheel flywheel) {
        public void update() {
            hoodServo.update();
            turretMotor.update();
            flywheel.update();
        }
    }

    record QueuerHardwareComponents(HwCRServo2<TableState> turntable, HwServoWithVoltage<Types.PopperState, Types.PopperVoltageState> popper, HwMotor2<Types.IntakeState> intakeMotor, DecodeColorSensor intakeColor) {
        public void update() {
            intakeColor.update();
            intakeMotor.update();
            turntable.update();
            popper.update();
        }
    }
}
