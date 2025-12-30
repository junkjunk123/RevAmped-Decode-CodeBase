package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.FlywheelController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.TwoMotorMechanism;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.FlywheelState;

@Config
public class Flywheel extends TwoMotorMechanism<FlywheelState> {
    public final FlywheelController controller;

    public Flywheel(HardwareMap hardwareMap, FlywheelState motorState) throws IllegalArgumentException {
        super(hardwareMap, "flywheel_right", "flywheel_left", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
        controller = new FlywheelController();
    }

    public static double FLYWHEEL_FAR_VELOCITY;
    public static double FLYWHEEL_NEAR_VELOCITY;
    public static double FLYWHEEL_MEDIUM_VELOCITY;
    public static double FLYWHEEL_AUTO_VELOCITY;
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

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) runController());
        statePowerMap.put(FlywheelState.STOPPED, MotorPower.constant(0f));
    }

    public void run(double targetVelocity) {
        if (Math.abs(targetVelocity - this.targetVelocity) > 15) {
            controller.reset(getVelocity());
            this.targetVelocity = targetVelocity;
        }

        motorState = FlywheelState.RUNNING;
    }

    public double runController() {
        return controller.update(getVelocityImperial(), targetVelocity);
    }

    public void updateTargetVelocity(double dist) {
        if (updateLock || (getCurrentState() != FlywheelState.RUNNING)) return;
        if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
        else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
        else targetVelocity = FLYWHEEL_FAR_VELOCITY;
        controller.reset(getVelocity());
    }

    @Override
    public void setCurrentState(FlywheelState motorState) {
        super.setCurrentState(motorState);

        if (motorState == FlywheelState.RUNNING) {
            targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
            controller.reset(getVelocity());
        }
    }

    public void setCurrentState(FlywheelState motorState, double dist) {
        super.setCurrentState(motorState);
        if (!updateLock) {
            if (dist <= 50) targetVelocity = FLYWHEEL_NEAR_VELOCITY;
            else if (50 < dist && dist < 100) targetVelocity = FLYWHEEL_MEDIUM_VELOCITY;
            else targetVelocity = FLYWHEEL_FAR_VELOCITY;
            controller.reset(getVelocity());
        }
    }

    public double getVelocityImperial() {
        return getVelocity() / ticksPerInch();
    }

    @Override
    public void update() {
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

    public static double ticksPerInch() {
       return (double) COUNTS_PER_REVOLUTION / 2 / Math.PI / RADIUS;
    }

    public void setUpdateLock(boolean updateLock) {
        this.updateLock = updateLock;
    }

    public boolean isUpdateLock() {
        return updateLock;
    }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    @Override
    public String getId() {
        return "flywheel";
    }
}
