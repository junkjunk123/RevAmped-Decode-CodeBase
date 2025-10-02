package org.firstinspires.ftc.teamcode.RevAmped.Util;

import static org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo.HOOD_REST;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
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

    enum PopperState implements HwServo2.ServoState{
        NEUTRAL(Popper.NEUTRAL),
        POP(Popper.POP);

        final float position;

        PopperState(float position){this.position = position;}

        @Override
        public float getPosition(){return position;}
    }

    enum PopperVoltageState implements HwServoWithVoltage.ServoVoltageState {
        FULLY_UP (
                voltage -> voltage >= Popper.UP_VOLTAGE,
                new HwServoWithVoltage.VoltageStatePair(PopperState.NEUTRAL, PopperState.POP, 500)
        ),
        FULLY_DOWN (
                voltage -> voltage <= Popper.DOWN_VOLTAGE,
                new HwServoWithVoltage.VoltageStatePair(PopperState.POP, PopperState.NEUTRAL, 500)
        );

        @Override
        public boolean reached(double voltage) {
            return reached.apply(voltage);
        }

        @Override
        public HwServoWithVoltage.VoltageStatePair[] getStates() {
            return states;
        }

        public final Function<Double, Boolean> reached;
        public final HwServoWithVoltage.VoltageStatePair[] states;

        PopperVoltageState(Function<Double, Boolean> reached, HwServoWithVoltage.VoltageStatePair... states) {
            this.reached = reached;
            this.states = states;
        }
    }

    enum TableState implements CRServoInternal.ServoState {
        REST(0),
        BALL1(0),
        BALL2(0),
        BALL3(0);

        final int position;

        TableState(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    enum FlywheelState implements HwMotor2.MotorState {
        RUNNING,
        STOPPED
    }

    enum TurretState implements HwMotor2.MotorState {
        IDLE (0),
        HOLD(0),
        TRACKING (0);

        final int pos;

        TurretState(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }
    }

    enum HoodState implements HwServo2.ServoState {
        REST(HOOD_REST),
        TRACKING(HOOD_REST);

        final float pos;

        HoodState(float pos) {
            this.pos = pos;
        }

        @Override
        public float getPosition() {
            return pos;
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
