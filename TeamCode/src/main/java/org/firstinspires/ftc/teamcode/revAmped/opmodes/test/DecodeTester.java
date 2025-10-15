package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.SelectableRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DriveIntakeTest;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MomentOfInertiaCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MotorSlipCalibrator;

@TeleOp(name = "DecodeTester")
public class DecodeTester extends SelectableRevAmpedOpMode {
    public DecodeTester() {
        super("Select a Testing OpMode.", s -> {
            s.folder("Pre-Match Tester", l -> {
                l.add("AutomatedTest", AutomatedTest::new);
            });
            s.folder("Calibrator", l -> {
                l.add("ServoCalibrateTest",ServoCalibrateTest::new);
                l.add("SwitchTest",SwitchTest::new);
            });
            s.folder("SoftwareTest", l -> {
                l.add("PromptTest", PromptTest::new);
            });
            s.folder("HardwareTest", l -> {
                l.add("DriveTest", MotorPortTest::new);
                l.add("SwitchTest",SwitchTest::new);
            });
            s.folder("ShooterTuning", l -> {
                l.add("MomentOfInertiaCalibrator", MomentOfInertiaCalibrator::new);
                l.add("MotorSlipCalibrator", MotorSlipCalibrator::new);
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
