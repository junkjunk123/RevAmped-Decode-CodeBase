package org.firstinspires.ftc.teamcode.RevAmped.Util;

import static org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo.HOOD_REST;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TableServo;

import java.util.function.Function;
import revAmped.components.Hardware.CRServoInternal;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.HwServoWithVoltage;

public interface Types {
    enum IntakeState implements HwMotor2.MotorState {
        INTAKE(1.0f),
        STOP(0.0f);

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
        STOPPED
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
        TRACKING;

        @Override
        public float getPosition() {
            return HOOD_REST;
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

    record ShooterHardwareComponents(HwServo2<Types.HoodState> hoodServo, TurretMotor turretMotor, Flywheel flywheel) {
        public void update() {
            hoodServo.update();
            turretMotor.update();
            flywheel.update();
        }
    }

    record QueuerHardwareComponents(HwCRServo2<TableState> turntable, HwServo2<Types.PopperState> popper, HwMotor2<Types.IntakeState> intakeMotor, DecodeColorSensor intakeColor) {
        public void update() {
            intakeColor.update();
            intakeMotor.update();
            turntable.update();
            popper.update();
        }
    }
}
