package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.Hardware.HwMotor;
import revAmped.components.Hardware.HwServo;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.HwServoWithVoltage;

public class TrapdoorServo extends HwServo2 {
    public enum TrapdoorState implements ServoState{
        OPEN(ITDConstants.INTAKE_TRAPDOOR_OPEN),
        CLOSE(ITDConstants.INTAKE_TRAPDOOR_CLOSED),
        SAMPLE_UNLOCKED(ITDConstants.INTAKE_TRAPDOOR_SAMPLE_UNLOCKED);

        final float position;

        TrapdoorState(float position) {
            this.position = position;
        }

        @Override
        public float getPosition() {
            return position;
        }
    }

    private final DelayManager delays;

    public TrapdoorServo(HardwareMap hardwareMap, String id, boolean setPosition, TrapdoorState trapdoorState, DelayManager delays) {
        super(hardwareMap, id, setPosition, trapdoorState, ServoBrand.AXON_MICRO);
        this.delays = delays;
    }

    public TrapdoorServo(HardwareMap hardwareMap, String id, float setPosition, TrapdoorState trapdoorState, DelayManager delays) {
        super(hardwareMap, id, setPosition, trapdoorState, ServoBrand.AXON_MICRO);
        this.delays = delays;
    }

    public void close() {
        setCurrentState(TrapdoorState.CLOSE);
    }

    public void open() {
        setCurrentState(TrapdoorState.OPEN);
    }

    public void passThrough() {
        unlockSample();
    }

    public void unlockSample() {
        setCurrentState(TrapdoorState.SAMPLE_UNLOCKED);
    }

    public TrapdoorState getCurrentState() {
        return (TrapdoorState) super.getCurrentState();
    }

    public void dropSample() {
        passThrough();
        delays.add(new Delay(500, this::close));
    }

    public void dropSample(ITDColor colorSensor, HwMotor intake, Runnable action) {
        intake.setPower(1f);
        passThrough();
        delays.add(new Delay(() -> colorSensor.getColor() == DetectionColor.None, () -> {
            delays.add(new Delay(300, () -> {
                close();
                action.run();
            }));
        }));
    }

    public void spitSample(HwMotor intake) {
        unlockSample();
        intake.setPower(-0.5f);
        delays.add(new Delay(500, () -> {
            close();
            intake.stop();
        }));
    }

    public void spitSample(ITDColor colorSensor, HwMotor intake, Runnable action, boolean fullPower) {
        spitSample(colorSensor, intake, action, -1.0f);
    }

    public void spitSample(ITDColor colorSensor, HwMotor intake, Runnable action, double power) {
        unlockSample();
        intake.stop();
        double finalPower = -1 * Math.abs(power);
        delays.add(new Delay(100, () -> {
            intake.setPower(finalPower);
            delays.add(new Delay(() -> colorSensor.getColor() == DetectionColor.None, () -> delays.add(new Delay(500, () -> {
                close();
                intake.stop();
                action.run();
            }))).setMaxTimeToRun(400));
        }));
    }

    public void spitSample(ITDColor colorSensor, HwMotor intake, Runnable action) {
        spitSample(colorSensor, intake, action, false);
    }

    public void spitSample(HwMotor intake, Runnable action) {
        unlockSample();
        intake.setPower(-0.5f);
        delays.add(new Delay(500, () -> {
            close();
            intake.stop();
            action.run();
        }));
    }

    public void spitSample(HwMotor intake, Runnable action, float newPower) {
        unlockSample();
        intake.setPower(-0.7f);
        delays.add(new Delay(500, () -> {
            close();
            intake.setPower(newPower);
            action.run();
        }));
    }

    public void setCurrentState(TrapdoorState state, float position) {
        super.setCurrentState(state, position);
    }

    public void toggleLock() {
        if (getCurrentState() == TrapdoorState.SAMPLE_UNLOCKED || getCurrentState() == TrapdoorState.OPEN) {
            close();
        } else {
            open();
        }
    }

    @Override
    protected TrapdoorState getInitialState() {
        return (TrapdoorState) super.getInitialState();
    }

    public boolean reached(TrapdoorState servoState) {
        return super.reached(servoState);
    }

    public boolean reached(TrapdoorState[] servoState) {
        return super.reached(servoState);
    }

    @NonNull
    @Override
    public String toString() {
        return "trapdoor";
    }
}
