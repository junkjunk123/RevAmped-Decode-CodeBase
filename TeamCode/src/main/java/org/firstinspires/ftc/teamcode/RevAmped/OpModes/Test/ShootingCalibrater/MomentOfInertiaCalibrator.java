package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test.ShootingCalibrater;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.MathUtil.MovingAverage;

@Disabled
@TeleOp
public class MomentOfInertiaCalibrator extends DecodeRevAmpedOpMode {
    private Flywheel flywheel;
    private double prevAngularVelocity = 0;
    private ElapsedTime timer;
    private MovingAverage inertia;

    @Override
    public void RevAmpedInit() {
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.RUNNING);
        timer = new ElapsedTime();
        timer.reset();
        inertia = new MovingAverage(15);
    }

    @Override
    public void RevAmpedLoop() {
        double angularVelocity = flywheel.getAngularVelocity();
        double time = timer.nanoseconds() / Math.pow(10, 9);
        double angularAcceleration = (angularVelocity - prevAngularVelocity) / time;
        prevAngularVelocity = angularVelocity;
        timer.reset();

        double motorCurrent = flywheel.getCurrentAmps();
        double torque = 2 * motorCurrent * Flywheel.k_T / Flywheel.GEAR_RATIO;
        double momentOfInertia = torque / angularAcceleration;
        inertia.addData(momentOfInertia);

        telemetry.addData("momentOfInertia", momentOfInertia);
        telemetry.addData("inertia",inertia.getMovingAverageForNum());
    }

    @Override
    public void update() {
        flywheel.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
