package org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Shooter;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import org.firstinspires.ftc.teamcode.revAmped.components.math.MovingAverage;

@Disabled
@TeleOp
public class MotorSlipCalibrator extends DecodeRevAmpedOpMode {
    private Flywheel flywheel;
    private MovingAverage omega_i;
    private MovingAverage omega_f;
    private double eta;

    private enum State {
        V_I,
        V_F,
        ETA
    }
    private State currentState = State.V_I;

    @Override
    public void RevAmpedInit() {
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.RUNNING);
        omega_i = new MovingAverage(10);
        omega_f = new MovingAverage(10);
    }

    @Override
    public void RevAmpedLoop() {
        switch (currentState) {
            case V_I -> {
                if (startedLaunch()) currentState = State.V_F;
                else omega_i.addData(flywheel.getAngularVelocity());
            }
            case V_F -> {
                omega_f.addData(flywheel.getAngularVelocity());

                if (omega_f.isCompleted()) {
                    currentState = State.ETA;
                    double omega_i = this.omega_i.getMovingAverageForNum();
                    double omega_f = this.omega_f.getMovingAverageForNum();
                    double vBall = Flywheel.MOMENT_OF_INERTIA * (omega_f - omega_i) / Shooter.BALL_MASS / Flywheel.RADIUS;
                    eta = vBall / (Flywheel.RADIUS * omega_i);
                }
            }
            case ETA -> telemetry.addData("eta",eta);
        }
    }

    @Override
    public void update() {
        telemetry.addData("currState",currentState);
        flywheel.update();
        telemetry.update();
    }

    private boolean startedLaunch() {
        //todo: fill this out
        return false;
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
