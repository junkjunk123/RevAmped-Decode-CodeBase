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
        super(hardwareMap, "left", "right", DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.REVERSE, motorState);
    }

    public static double FLYWHEEL_MAX_VELOCITY;
    private double targetVelocity;

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(FlywheelState.RUNNING, () -> (float) controller.runWithError(targetVelocity - motor.getVelocity()));
    }

    public enum FlywheelState implements MotorState {
        RUNNING(),
        STOPPED();
    }

    public void run(double targetVelocity){
        this.targetVelocity = targetVelocity;
        setMotorState(FlywheelState.RUNNING);
    }

    @Override
    public void setMotorState(FlywheelState motorState) {
        super.setMotorState(motorState);
        prevPos = null;
        prevTimeNano = System.nanoTime();
    }

    @Override
    public void setMotorState(FlywheelState motorState, float power) {
        super.setMotorState(motorState, power);
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
}
