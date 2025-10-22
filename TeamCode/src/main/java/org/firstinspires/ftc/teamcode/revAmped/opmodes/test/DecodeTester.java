package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.SelectableRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DriveIntakeTest;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MomentOfInertiaCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MotorSlipCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.text.DecimalFormat;

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
                l.add("PopperTest", PopperTest::new);
                l.add("ColorTest", ColorTest::new);
                l.add("TurntableTest", TurntableTest::new);
                l.add("MotorTest", MotorTest::new);
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

    // Popper Test
    @TeleOp(name = "PopperTest", group = "Test")
    public static class PopperTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private Popper popper;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private boolean manualMode = false;
        private int testStep = 0;
        private boolean testPassed = false;

        @Override
        public void RevAmpedInit() {
            popper = new Popper(hardwareMap, false);
            testTimer = new ElapsedTime();
            telemetry.addData("Popper Test", "A=Auto Test, B=Manual Mode");
            telemetry.update();
        }

        @Override
        public void RevAmpedLoop() {
            popper.update();
            
            // Toggle between auto test and manual mode
            if (gamepad1.aWasPressed() && !testRunning && !manualMode) {
                testRunning = true;
                testStep = 0;
                testPassed = false;
                testTimer.reset();
            }
            
            if (gamepad1.bWasPressed()) {
                manualMode = !manualMode;
                testRunning = false;
            }
            
            // Manual control with X and Y
            if (manualMode) {
                if (gamepad1.xWasPressed()) {
                    popper.setCurrentState(Types.PopperState.POP);
                }
                if (gamepad1.yWasPressed()) {
                    popper.setCurrentState(Types.PopperState.NEUTRAL);
                }
            }
            
            // Run automated test
            if (testRunning) {
                runTest();
            }
            
            // Telemetry
            telemetry.clear();
            telemetry.addData("Popper Tester", "");
            telemetry.addData("State", popper.getCurrentState().toString());
            telemetry.addData("Position", String.format("%.3f", popper.getPosition()));
            telemetry.addData("Voltage", String.format("%.3f", popper.getVoltage()) + "V");
            telemetry.addData("UP Threshold", String.format("%.3f", Popper.UP_VOLTAGE) + "V");
            telemetry.addData("DOWN Threshold", String.format("%.3f", Popper.DOWN_VOLTAGE) + "V");
            
            // Voltage status
            double currentVoltage = popper.getVoltage();
            boolean atUpPosition = currentVoltage >= Popper.UP_VOLTAGE;
            boolean atDownPosition = currentVoltage <= Popper.DOWN_VOLTAGE;
            telemetry.addData("At UP Position", atUpPosition ? "YES" : "NO");
            telemetry.addData("At DOWN Position", atDownPosition ? "YES" : "NO");
            
            if (manualMode) {
                telemetry.addData("Mode", "MANUAL: X=POP, Y=NEUTRAL, B=Auto Mode");
            } else if (testRunning) {
                telemetry.addData("Mode", "AUTO: Step " + (testStep + 1) + "/4");
                telemetry.addData("Time", String.format("%.1f", testTimer.seconds()) + "s");
            } else if (testPassed) {
                telemetry.addData("Mode", "TEST PASSED: Popper working correctly!");
            } else {
                telemetry.addData("Mode", "A=Auto Test, B=Manual Mode");
            }
            
            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType.TEST;
        }

        private void runTest() {
            switch (testStep) {
                case 0:
                    // Move to POP
                    popper.setCurrentState(Types.PopperState.POP);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 1:
                    // Check POP voltage at UP
                    if (popper.reached(Types.PopperVoltageState.FULLY_UP) || testTimer.milliseconds() > 3000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 2:
                    // Move to NEUTRAL
                    popper.setCurrentState(Types.PopperState.NEUTRAL);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 3:
                    // Check NEUTRAL POP voltage at DOWN
                    if (popper.reached(Types.PopperVoltageState.FULLY_DOWN) || testTimer.milliseconds() > 3000) {
                        testRunning = false;
                        testPassed = true;
                        telemetry.addData("Test", "PASSED - Popper working correctly!");
                    }
                    break;
            }
        }
    }

    // Color Sensor
    @TeleOp(name = "ColorTest", group = "Test")
    public static class ColorTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private DualIntakeColor colorSensor;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private boolean testPassed = false;
        private double neutralHue = 150.0; // Expected neutral color hue
        private double hueTolerance = 15.0; // Allowable deviation

        @Override
        public void RevAmpedInit() {
            colorSensor = new DualIntakeColor(hardwareMap);
            testTimer = new ElapsedTime();
            telemetry.addData("Color Test", "Ready. Press A to start.");
            telemetry.update();
        }

        @Override
        public void RevAmpedLoop() {
            colorSensor.update();
            
            // Start test
            if (gamepad1.a && !testRunning) {
                testRunning = true;
                testPassed = false;
                testTimer.reset();
            }
            
            // Run test
            if (testRunning) {
                if (testTimer.milliseconds() > 2000) {
                    // Test the color sensor response
                    if (colorSensor.testResponse()) {
                        testPassed = true;
                    }
                    testRunning = false;
                }
            }
            
            // Display telemetry
            telemetry.clear();
            telemetry.addData("Color Sensor Tester", "");
            telemetry.addData("Detected Color", colorSensor.getColor().toString());
            telemetry.addData("Neutral Hue Expected", String.format("%.1f", neutralHue));
            telemetry.addData("Hue Tolerance", String.format("±%.1f", hueTolerance));
            
            if (testRunning) {
                telemetry.addData("Test", "Checking neutral color detection...");
                telemetry.addData("Time", String.format("%.1f", testTimer.seconds()) + "s");
            } else if (testPassed) {
                telemetry.addData("Test", "PASSED - Color sensor working properly!");
            } else if (testTimer.milliseconds() > 0) {
                telemetry.addData("Test", "FAILED - Color sensor not detecting neutral properly");
            } else {
                telemetry.addData("Test", "Ready. Press A to start.");
            }
            
            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType.TEST;
        }
    }

    // Turntable Test
    @TeleOp(name = "TurntableTest", group = "Test")
    public static class TurntableTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private TableServo tableServo;
        private IntakeMotor intakeMotor;

        @Override
        public void RevAmpedInit() {
            intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
            intakeMotor.setVoltageCompensation();
            tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intakeMotor));
            telemetry.addData("Turntable Test", "Manual Control - Use A/B/X buttons");
            telemetry.update();
        }

        @Override
        public void RevAmpedLoop() {
            intakeMotor.update();
            tableServo.update();
            
            // Manual control with gamepad buttons
            if (gamepad1.aWasPressed()) {
                tableServo.setCurrentState(Types.TableState.BALL1);
            }

            if (gamepad1.bWasPressed()) {
                tableServo.setCurrentState(Types.TableState.BALL2);
            }

            if (gamepad1.xWasPressed()) {
                tableServo.setCurrentState(Types.TableState.BALL3);
            }
            
            // Display telemetry
            telemetry.clear();
            telemetry.addData("Turntable Tester", "Manual Control");
            telemetry.addData("Table State", tableServo.getCurrentState().toString());
            telemetry.addData("Table Position", tableServo.getCurrentPosition());
            telemetry.addData("Intake State", intakeMotor.getCurrentState().toString());
            telemetry.addData("Controls", "A=BALL1, B=BALL2, X=BALL3");
            
            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType.TEST;
        }
    }

    // Motor Test
    @TeleOp(name = "MotorTest", group = "Test")
    public static class MotorTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private Flywheel flywheel;
        private TeleOpMecanumDrive drivetrain;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private int testStep = 0;
        
        // Current readings
        private double flywheelCurrentStart = 0;
        private double flywheelCurrentEnd = 0;
        private double driveCurrentStart = 0;
        private double driveCurrentEnd = 0;

        @Override
        public void RevAmpedInit() {
            flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
            drivetrain = new TeleOpMecanumDrive(hardwareMap);
            testTimer = new ElapsedTime();
            telemetry.addData("Motor Test", "Ready. Press A to start.");
            telemetry.update();
        }

        @Override
        public void RevAmpedLoop() {
            flywheel.update();
            drivetrain.update();
            
            // Start test
            if (gamepad1.a && !testRunning) {
                testRunning = true;
                testStep = 0;
                testTimer.reset();
            }
            
            // Run automated test
            if (testRunning) {
                runTest();
            }
            
            // Display telemetry
            telemetry.clear();
            telemetry.addData("MOTOR TESTER", "");
            telemetry.addData("Flywheel State", flywheel.getCurrentState().toString());
            telemetry.addData("Flywheel Current", String.format("%.2f", flywheel.getCurrent()) + "A");
            telemetry.addData("Flywheel Power", String.format("%.2f", flywheel.getPower()));
            telemetry.addData("Drive Current", String.format("%.2f", drivetrain.getCurrent()) + "A");
            
            if (testRunning) {
                telemetry.addData("Test", "Running Step " + (testStep + 1) + "/3");
                telemetry.addData("Time", String.format("%.1f", testTimer.seconds()) + "s");
            } else {
                telemetry.addData("Test", "Ready. Press A to start.");
            }
            
            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType.TEST;
        }

        private void runTest() {
            switch (testStep) {
                case 0:
                    // Test flywheel
                    if (testTimer.milliseconds() < 1000) {
                        // Initial current
                        flywheelCurrentStart = flywheel.getCurrent();
                    } else if (testTimer.milliseconds() < 2000) {
                        // Run flywheel
                        flywheel.setCurrentState(Types.FlywheelState.RUNNING);
                        flywheel.setPower(0.2f);
                    } else if (testTimer.milliseconds() > 3000) {
                        // Final current
                        flywheelCurrentEnd = flywheel.getCurrent();
                        flywheel.setCurrentState(Types.FlywheelState.STOPPED);
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                    
                case 1:
                    // Drivetrain
                    if (testTimer.milliseconds() < 1000) {
                        // Initial current
                        driveCurrentStart = drivetrain.getCurrent();
                    } else if (testTimer.milliseconds() < 2000) {
                        // Drivetrain power
                        drivetrain.setPower(0.2f, 0.2f, 0.2f, 0.2f);
                    } else if (testTimer.milliseconds() > 3000) {
                        // Final current
                        driveCurrentEnd = drivetrain.getCurrent();
                        drivetrain.setPower(0f, 0f, 0f, 0f);
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                    
                case 2:
                    // Results
                    if (testTimer.milliseconds() > 2000) {
                        testRunning = false;
                        telemetry.addData("Test", "Complete - Results:");
                        telemetry.addData("Flywheel Start Current", String.format("%.2f", flywheelCurrentStart) + "A");
                        telemetry.addData("Flywheel End Current", String.format("%.2f", flywheelCurrentEnd) + "A");
                        telemetry.addData("Flywheel Current Change", String.format("%.2f", flywheelCurrentEnd - flywheelCurrentStart) + "A");
                        telemetry.addData("Drive Start Current", String.format("%.2f", driveCurrentStart) + "A");
                        telemetry.addData("Drive End Current", String.format("%.2f", driveCurrentEnd) + "A");
                        telemetry.addData("Drive Current Change", String.format("%.2f", driveCurrentEnd - driveCurrentStart) + "A");
                        
                        // Check if current is reasonable (VALUES ARE EDITABLE)
                        boolean flywheelWorking = Math.abs(flywheelCurrentEnd - flywheelCurrentStart) > 0.1;
                        boolean driveWorking = Math.abs(driveCurrentEnd - driveCurrentStart) > 0.1;
                        
                        telemetry.addData("Flywheel Status", flywheelWorking ? "PASS" : "FAIL - No current change");
                        telemetry.addData("Drive Status", driveWorking ? "PASS" : "FAIL - No current change");
                    }
                    break;
            }
        }
    }
}
