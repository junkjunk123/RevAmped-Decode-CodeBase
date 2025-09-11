package org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.TwoMotorMechanism;
import revAmped.components.SlideController.PIDF.PIDFController;

public class Flywheel extends TwoMotorMechanism<Flywheel.FlywheelState> {
    private final PIDFController controller = new PIDFController(new PIDFCoefficients(0f,0f,0f,0f));
    private Double velocity = null;
    private Integer prevPos = null;
    private long prevTimeNano;

    public Flywheel(HardwareMap hardwareMap, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, "flywheel_left", "flywheel_right", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
    }

    public static double FLYWHEEL_MAX_VELOCITY;
    private double targetVelocity;
    public static float AUTO_PRELOADS;
    public static float AUTO_SET_1;
    public static float AUTO_SET_2;
    public static float AUTO_SET_3;
    public static double RADIUS = 0; //Inches
    public static int COUNTS_PER_REVOLUTION = 0;
    public static double GEAR_RATIO = 0.75;
    public static double STALL_TORQUE = 1.47; //Kg * cm
    public static double STALL_CURRENT = 9.2; //Amps
    public static double k_T = 0.0980665 * STALL_TORQUE / STALL_CURRENT;
    public static double MOMENT_OF_INERTIA;

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) controller.runWithError(targetVelocity - motor.getVelocity()));
        statePowerMap.put(FlywheelState.STOPPED, MotorPower.constant(0f));
        statePowerMap.put(FlywheelState.TRACKING, () -> {
            targetVelocity = FLYWHEEL_MAX_VELOCITY;
            return (float) controller.runWithError(targetVelocity - motor.getVelocity());
        });
    }

    public enum FlywheelState implements MotorState {
        RUNNING,
        STOPPED,
        TRACKING;
    }

    public void run(double targetVelocity) {
        this.targetVelocity = targetVelocity;
        setCurrentState(FlywheelState.RUNNING);
    }

    @Override
    public void setCurrentState(FlywheelState motorState) {
        super.setCurrentState(motorState);
        prevPos = null;
        prevTimeNano = System.nanoTime();

        if (motorState == FlywheelState.RUNNING) targetVelocity = FLYWHEEL_MAX_VELOCITY;
    }

    @Override
    public void setCurrentState(FlywheelState motorState, float power) {
        super.setCurrentState(motorState, power);
        prevPos = null;
        prevTimeNano = System.nanoTime();
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

    @Override
    public void update() {
        super.update();
        prevPos = currentPos;
        velocity = null;
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
}
