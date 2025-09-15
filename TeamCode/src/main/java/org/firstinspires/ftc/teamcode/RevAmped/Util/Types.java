package org.firstinspires.ftc.teamcode.RevAmped.Util;

import static org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo.HOOD_REST;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;

import revAmped.components.Hardware.CRServoInternal;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;

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
        STOPPED,
        TRACKING;
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
}
