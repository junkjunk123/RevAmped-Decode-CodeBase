package org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MovingAverage;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Disabled
@TeleOp
public class MomentOfInertiaCalibrator extends DecodeRevAmpedOpMode {
    private Flywheel flywheel;
    private double prevAngularVelocity = 0;
    private ElapsedTime timer;
    private MovingAverage inertia;
    private boolean record;

    @Override
    public void RevAmpedInit() {
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.RUNNING);
        timer = new ElapsedTime();
        timer.reset();
        inertia = new MovingAverage(15);
    }

    @Override
    public void RevAmpedStart() {
        flywheel.run(1500);
        scheduler.add(new Delay(3000, () -> record = true));
    }

    @Override
    public void RevAmpedLoop() {
        if (record) {
            if (!inertia.isCompleted()) {
                double angularVelocity = 1500 * Math.PI * 2 / Flywheel.COUNTS_PER_REVOLUTION;
                double time = timer.nanoseconds() / Math.pow(10, 9);
                double angularAcceleration = (angularVelocity - prevAngularVelocity) / time;
                prevAngularVelocity = angularVelocity;
                timer.reset();

                double motorCurrent = flywheel.motor.getCurrent(CurrentUnit.AMPS);
                double torque = 2 * motorCurrent * Flywheel.k_T / Flywheel.GEAR_RATIO;
                double momentOfInertia = torque / angularAcceleration;
                inertia.addData(momentOfInertia);
                telemetry.addData("momentOfInertia", momentOfInertia);

                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {

                }
            } else {
                flywheel.stop();
            }

            telemetry.addData("inertia", inertia.getMovingAverageForNum());
        }
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
