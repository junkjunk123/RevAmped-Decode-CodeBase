package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF.PSFLCoefficients;
import org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF.PSFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.TwoMotorMechanism;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.FlywheelState;

@Config
public class Flywheel extends TwoMotorMechanism<FlywheelState> {
    public static double P;
    public static double K_static;
    public static double K_V;
    public static double L;
    public PSFController controller;
    private final Differentiator currentAcceleration;
    private final Differentiator currentVelocity;
    private final HwVoltageSensor voltageSensor;

    public Flywheel(HardwareMap hardwareMap, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, "flywheel_right", "flywheel_left", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
        this.voltageSensor = null;
        currentVelocity = new Differentiator(() -> 0.0, () -> (double) getCurrentPosition());
        currentAcceleration = new Differentiator(() -> 0.0, this::getVelocity);
        controller = new PSFController(new PSFLCoefficients(P, K_static, K_V, L));
        controller.setUseFilter(true);
    }

    public Flywheel(HardwareMap hardwareMap, FlywheelState motorState, HwVoltageSensor voltageSensor) throws IllegalArgumentException {
        super(hardwareMap, "flywheel_right", "flywheel_left", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
        this.voltageSensor = voltageSensor;
        currentVelocity = new Differentiator(() -> 0.0, () -> (double) getCurrentPosition());
        currentAcceleration = new Differentiator(() -> 0.0, this::getVelocity);
        controller = new PSFController(new PSFLCoefficients(P, K_static, K_V, L));
        controller.setUseFilter(true);
    }

    public static double FLYWHEEL_FAR_VELOCITY;
    public static double FLYWHEEL_NEAR_VELOCITY;
    public static double FLYWHEEL_MEDIUM_VELOCITY;
    private double targetVelocity;
    public static float AUTO_PRELOADS;
    public static float AUTO_SET_1;
    public static float AUTO_SET_2;
    public static float AUTO_SET_3;
    public static double RADIUS; //Inches
    public static int COUNTS_PER_REVOLUTION;
    public static double GEAR_RATIO;
    public static double STALL_TORQUE; //Kg * cm
    public static double STALL_CURRENT; //Amps
    public static double k_T;
    public static double MOMENT_OF_INERTIA;
    public static double LAUNCH_ETA; //Motor energy transfer slip and speed loss from ball jamming
    public static double VELOCITY_ERROR_THRESHOLD;
    private boolean updateLock = false;
    private boolean voltageCompensation = false;

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) runController());
        statePowerMap.put(FlywheelState.STOPPED, MotorPower.constant(0f));
    }

    public void run(double targetVelocity) {
        if (Math.abs(targetVelocity - this.targetVelocity) > 15) {
            controller.reset(targetVelocity - getVelocity());
            this.targetVelocity = targetVelocity;
        }

        if (getCurrentState() != FlywheelState.RUNNING) {
            currentAcceleration.reset();
            currentVelocity.reset();
        }
        motorState = FlywheelState.RUNNING;
    }

    public double runController() {
        double velTargetTicks = targetVelocity * ticksPerInch();

        if (voltageCompensation && voltageSensor != null)
            return controller.run(getVelocity(), velTargetTicks) * voltageSensor.getVoltageNormalized(12.75f);
        else
            return controller.run(getVelocity(), velTargetTicks);
    }

    public void setVoltageCompensation(boolean voltageCompensation) {
        this.voltageCompensation = voltageCompensation;
    }

    public void updateTargetVelocity(double dist) {
        if (updateLock || (getCurrentState() != FlywheelState.RUNNING)) return;
        if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
        else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
        else targetVelocity = FLYWHEEL_FAR_VELOCITY;
        controller.reset(targetVelocity - getVelocity());
    }

    @Override
    public void setCurrentState(FlywheelState motorState) {
        if (motorState != this.motorState) {
            currentAcceleration.reset();
            currentVelocity.reset();
        }

        super.setCurrentState(motorState);

        if (motorState == FlywheelState.RUNNING) {
            targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
            controller.reset(targetVelocity - getVelocity());
        }
    }

    public void setCurrentState(FlywheelState motorState, double dist) {
        if (motorState != this.motorState) {
            currentAcceleration.reset();
            currentVelocity.reset();
        }

        super.setCurrentState(motorState);
        if (!updateLock) {
            if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
            else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
            else targetVelocity = FLYWHEEL_FAR_VELOCITY;
            controller.reset(targetVelocity - getVelocity());
        }
    }

    @Override
    public void setCurrentState(FlywheelState motorState, float power) {
        if (motorState != this.motorState) {
            currentAcceleration.reset();
            currentVelocity.reset();
        }

        super.setCurrentState(motorState, power);
    }

    @Override
    public double getVelocity() {
        return currentVelocity.calculate();
    }

    public double getAcceleration() {
        return currentAcceleration.calculate();
    }

    public double getVelocityImperial() {
        return getVelocity() / ticksPerInch();
    }

    @Override
    public void update() {
        currentVelocity.update();
        currentAcceleration.update();
        super.update();
    }

    @Override
    public FlywheelState getRest() {
        return FlywheelState.STOPPED;
    }

    /**
        @return Radians
     */
    public double getAngularVelocity() {
        return Math.PI * 2 * getVelocity() / COUNTS_PER_REVOLUTION;
    }

    public double getBallLaunchVelocity() {
        return getAngularVelocity() * RADIUS * LAUNCH_ETA;
    }

    public double getTheoreticalLaunchVelocity() {
        return targetVelocity * LAUNCH_ETA;
    }

    public boolean canShoot() {
        return Math.abs(getVelocityImperial() - targetVelocity) < VELOCITY_ERROR_THRESHOLD;
    }

    public boolean canShootLinear() {
        return Math.abs(targetVelocity - getVelocityImperial() + Popper.POP_TIME_MILLIS * currentAcceleration.calculate()) < VELOCITY_ERROR_THRESHOLD;
    }

    public static double ticksPerInch() {
       return (double) COUNTS_PER_REVOLUTION / 2 / Math.PI / RADIUS;
    }

    public void setUpdateLock(boolean updateLock) {
        this.updateLock = updateLock;
    }

    public boolean isUpdateLock() {
        return updateLock;
    }

    public void updateConstants(PSFLCoefficients coefficients) {
        controller.setCoefficients(coefficients);
    }

    @Override
    public String getId() {
        return "flywheel";
    }
}
