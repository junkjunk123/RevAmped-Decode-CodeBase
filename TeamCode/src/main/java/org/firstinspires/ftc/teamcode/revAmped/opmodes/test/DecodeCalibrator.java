package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.SelectableRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DriveIntakeTest;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.shooterPositionCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MomentOfInertiaCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MotorSlipCalibrator;

@TeleOp(name = "DecodeCalibrator")
public class DecodeCalibrator extends SelectableRevAmpedOpMode {
    public DecodeCalibrator() {
        super("Select a Testing OpMode.", s -> {
            s.folder("Pre-Match Tester", l -> {
                l.add("AutomatedTest", AutomatedTest::new);
            });
            s.folder("Calibrator", l -> {
                l.add("ServoCalibrateTest", ServoCalibrateTest::new);
                l.add("SwitchTest", SwitchTest::new);
            });
            s.folder("SoftwareTest", l -> {
                l.add("PromptTest", PromptTest::new);
                l.add("SortTest", SortTest::new);
                l.add("ObeliskDetectionTest", ObeliskDetectionTest::new);
            });
            s.folder("HardwareTest", l -> {
                l.add("DriveTest", MotorPortTest::new);
                l.add("SwitchTest", SwitchTest::new);
                l.add("TurntableTest", TurntableTest::new);
                l.add("TurntableTuner", TurntablePIDTuner::new);
                l.add("TurretTest", TurretCalibrator::new);
                l.add("TurretTuner", TurretPIDTuner::new);
                l.add("IntakeSensorTest", IntakeSensorTest::new);
            });
            s.folder("ShooterTuning", l -> {
                l.add("MomentOfInertiaCalibrator", MomentOfInertiaCalibrator::new);
                l.add("MotorSlipCalibrator", MotorSlipCalibrator::new);
                l.add("AccuracyMaxxer", shooterPositionCalibrator::new);
                l.add("FlywheelPIDTuner", FlywheelPIDTuner::new);
            });
            s.folder("Misc", l -> {
                l.add("DriveIntakePopperTest", DriveIntakeTest::new);
            });
        });
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}