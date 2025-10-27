package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.TwoMotorMechanism;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.FlywheelState;

@Config
public class Flywheel extends TwoMotorMechanism<FlywheelState> {
    private final PIDFController controllerLarge;
    private final PIDFController controllerMedium;
    private final PIDFController controllerSmall;

    public static PIDFCoefficients largeCoefficients;
    public static PIDFCoefficients mediumCoefficients;
    public static PIDFCoefficients smallCoefficients;
    private Double velocity = null;
    private final Differentiator currentAcceleration;
    private Integer prevPos = null;
    private long prevTimeNano;

    public Flywheel(HardwareMap hardwareMap, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, "flywheel_right", "flywheel_left", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
        controllerLarge = new PIDFController(largeCoefficients);
        controllerMedium = new PIDFController(mediumCoefficients);
        controllerSmall = new PIDFController(smallCoefficients);
        currentAcceleration = new Differentiator(() -> 0.0, this::getVelocity);
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
    public static double kStatic;
    private boolean updateLock = false;

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) controllerOutput());
        statePowerMap.put(FlywheelState.STOPPED, MotorPower.constant(0f));
    }

    public void run(double targetVelocity) {
        this.targetVelocity = targetVelocity;
        if (getCurrentState() != FlywheelState.RUNNING) {
            controllerSmall.reset();
            controllerMedium.reset();
            controllerLarge.reset();
            currentAcceleration.reset();
            prevPos = null;
            prevTimeNano = System.nanoTime();
        }
        motorState = FlywheelState.RUNNING;
    }

    public double controllerOutput() {
        if (targetVelocity <= 600) return runController(controllerSmall, targetVelocity - getVelocityImperial());
        else if (targetVelocity <= 1200) return runController(controllerMedium, targetVelocity - getVelocityImperial());
        else return runController(controllerLarge, targetVelocity - getVelocityImperial());
    }

    public double runController(PIDFController controller, double error) {
        controller.updateError(error);
        controller.updateFeedForwardInput(targetVelocity);
        return controller.runPIDF() + kStatic;
    }

    public void updateTargetVelocity(double dist) {
        if (updateLock || (getCurrentState() != FlywheelState.RUNNING)) return;
        if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
        else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
        else targetVelocity = FLYWHEEL_FAR_VELOCITY;
    }

    @Override
    public void setCurrentState(FlywheelState motorState) {
        super.setCurrentState(motorState);
        prevPos = null;
        prevTimeNano = System.nanoTime();
        controllerLarge.reset();
        controllerSmall.reset();
        controllerMedium.reset();
        currentAcceleration.reset();

        if (motorState == FlywheelState.RUNNING) {
            targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
        }
    }

    public void setCurrentState(FlywheelState motorState, double dist) {
        super.setCurrentState(motorState);
        prevPos = null;
        prevTimeNano = System.nanoTime();
        controllerLarge.reset();
        controllerSmall.reset();
        controllerMedium.reset();
        currentAcceleration.reset();

        if (!updateLock) {
            if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
            else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
            else targetVelocity = FLYWHEEL_FAR_VELOCITY;
        }
    }

    @Override
    public void setCurrentState(FlywheelState motorState, float power) {
        super.setCurrentState(motorState, power);
        prevPos = null;
        prevTimeNano = System.nanoTime();
        controllerLarge.reset();
        controllerSmall.reset();
        controllerMedium.reset();
        currentAcceleration.reset();
    }

    @Override
    public double getVelocity() {
        if (velocity == null) {
            if (prevPos == null) {
                return super.getVelocity();
            }

            int currentPos = getCurrentPosition();
            long currentTimeNano = System.nanoTime();

            double deltaTime = (currentTimeNano - prevTimeNano) / Math.pow(10, 9);
            prevTimeNano = currentTimeNano;

            if (deltaTime == 0) velocity = 0.0;
            else velocity = (currentPos - prevPos) / deltaTime;
        }

        return velocity;
    }

    public double getAcceleration() {
        return currentAcceleration.calculate();
    }

    public double getVelocityImperial() {
        return getVelocity() / ticksPerInch();
    }

    @Override
    public void update() {
        super.update();
        prevPos = currentPos;
        velocity = null;
        currentAcceleration.update();
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
}
