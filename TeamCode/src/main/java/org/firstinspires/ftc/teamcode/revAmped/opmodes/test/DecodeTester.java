package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.Arrays;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.SelectableRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DriveIntakeTest;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MomentOfInertiaCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.test.ShootingCalibrater.MotorSlipCalibrator;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.text.DecimalFormat;
import java.util.Locale;

@TeleOp(name = "DecodeTester")
public class DecodeTester extends SelectableRevAmpedOpMode {
    public DecodeTester() {
        super("Select a Testing OpMode.", s -> {
            s.folder("Pre-Match Tester", l -> {
                l.add("AutomatedTest", AutomatedTest::new);
                l.add("FullTestSequence", FullTestSequence::new);
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
        private int testStep = 0;
        private boolean testPassed = false;

        @Override
        public void RevAmpedInit() {
            popper = new Popper(hardwareMap, false);
            testTimer = new ElapsedTime();
            testRunning = true;
            testStep = 0;
            testPassed = false;
            testTimer.reset();
        }

        @Override
        public void RevAmpedLoop() {
            popper.update();

            // Run automated test
            if (testRunning) {
                runTest();
            }

            // Display telemetry
            telemetry.clear();
            telemetry.addData("Popper Tester", "");
            telemetry.addData("State", popper.getCurrentState().toString());
            telemetry.addData("Position", String.format(Locale.US, "%.3f", popper.getPosition()));
            telemetry.addData("Voltage", String.format(Locale.US, "%.3f", popper.getVoltage()) + "V");
            telemetry.addData("UP Threshold", String.format(Locale.US, "%.3f", Popper.UP_VOLTAGE) + "V");
            telemetry.addData("DOWN Threshold", String.format(Locale.US, "%.3f", Popper.DOWN_VOLTAGE) + "V");

            // Voltage status indicators
            double currentVoltage = popper.getVoltage();
            boolean atUpPosition = currentVoltage >= Popper.UP_VOLTAGE;
            boolean atDownPosition = currentVoltage <= Popper.DOWN_VOLTAGE;
            telemetry.addData("At UP Position", atUpPosition ? "YES" : "NO");
            telemetry.addData("At DOWN Position", atDownPosition ? "YES" : "NO");

            if (testRunning) {
                telemetry.addData("Mode", "AUTO TEST - Step " + (testStep + 1) + "/4");
                telemetry.addData("Time", String.format(Locale.US, "%.1f", testTimer.seconds()) + "s");
            } else if (testPassed) {
                telemetry.addData("Mode", "TEST PASSED. Popper working correctly.");
            } else {
                telemetry.addData("Mode", "TEST FAILED. Voltage thresholds not met.");
            }

            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return Robot.OpModeType.TEST;
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
                    // Check POP voltage - verify it reached FULLY_UP state
                    boolean upVoltageReached = popper.reached(Types.PopperVoltageState.FULLY_UP);
                    if (upVoltageReached || testTimer.milliseconds() > 3000) {
                        if (upVoltageReached) {
                            telemetry.addData("Voltage Test", "UP position voltage reached: " + String.format(Locale.US, "%.3f", popper.getVoltage()) + "V");
                        } else {
                            telemetry.addData("Voltage Test", "UP position voltage FAILED: " + String.format(Locale.US, "%.3f", popper.getVoltage()) + "V (expected >= " + String.format(Locale.US, "%.3f", Popper.UP_VOLTAGE) + "V)");
                        }
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
                    // Check NEUTRAL voltage - verify it reached FULLY_DOWN state
                    boolean downVoltageReached = popper.reached(Types.PopperVoltageState.FULLY_DOWN);
                    if (downVoltageReached || testTimer.milliseconds() > 3000) {
                        if (downVoltageReached) {
                            telemetry.addData("Voltage Test", "DOWN position voltage reached: " + String.format(Locale.US, "%.3f", popper.getVoltage()) + "V");
                        } else {
                            telemetry.addData("Voltage Test", "DOWN position voltage FAILED: " + String.format(Locale.US, "%.3f", popper.getVoltage()) + "V (expected <= " + String.format(Locale.US, "%.3f", Popper.DOWN_VOLTAGE) + "V)");
                        }
                        testRunning = false;
                        testPassed = downVoltageReached;
                        telemetry.addData("Test", testPassed ? "PASSED - Popper working correctly!" : "FAILED - Voltage thresholds not met!");
                    }
                    break;
            }
        }
    }

    // Color Sensor
    @TeleOp(name = "ColorTest", group = "Test")
    public static class ColorTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private IntakeColor leftColorSensor;
        private IntakeColor rightColorSensor;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private boolean testPassed = false;
        private int testStep = 0;
        private boolean leftTestPassed = false;
        private boolean rightTestPassed = false;

        @Override
        public void RevAmpedInit() {
            leftColorSensor = new IntakeColor(hardwareMap, "color_left", 0.0);
            rightColorSensor = new IntakeColor(hardwareMap, "color_right", 0.0);
            testTimer = new ElapsedTime();
            testRunning = true;
            testPassed = false;
            testStep = 0;
            leftTestPassed = false;
            rightTestPassed = false;
            testTimer.reset();
        }

        @Override
        public void RevAmpedLoop() {
            leftColorSensor.update();
            rightColorSensor.update();

            if (testRunning) {
                runTest();
            }

            // Display telemetry
            telemetry.clear();
            telemetry.addData("Color Sensor Tester", "");
            telemetry.addData("Left Color", leftColorSensor.getColor().toString());
            telemetry.addData("Right Color", rightColorSensor.getColor().toString());
            telemetry.addData("Left Hue", String.format(Locale.US, "%.1f", leftColorSensor.getHue()));
            telemetry.addData("Right Hue", String.format(Locale.US, "%.1f", rightColorSensor.getHue()));

            if (testRunning) {
                telemetry.addData("Test", "Step " + (testStep + 1) + "/3");
                telemetry.addData("Time", String.format(Locale.US, "%.1f", testTimer.seconds()) + "s");
            } else if (testPassed) {
                telemetry.addData("Test", "PASSED. Both color sensors working!");
                telemetry.addData("Left Sensor", leftTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Right Sensor", rightTestPassed ? "PASS" : "FAIL");
            } else if (testTimer.milliseconds() > 0) {
                telemetry.addData("Test", "FAILED. Color sensors not working properly");
                telemetry.addData("Left Sensor", leftTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Right Sensor", rightTestPassed ? "PASS" : "FAIL");
            } else {
                telemetry.addData("Test", "Test Complete");
            }

            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return Robot.OpModeType.TEST;
        }

        private void runTest() {
            switch (testStep) {
                case 0:
                    // Test left color sensor
                    if (testTimer.milliseconds() > 2000) {
                        leftTestPassed = leftColorSensor.testResponse();
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 1:
                    // Test right color sensor
                    if (testTimer.milliseconds() > 2000) {
                        rightTestPassed = rightColorSensor.testResponse();
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 2:
                    // Finalize results
                    if (testTimer.milliseconds() > 1000) {
                        testPassed = leftTestPassed && rightTestPassed;
                        testRunning = false;
                    }
                    break;
            }
        }
    }

    // Turntable Test
    @TeleOp(name = "TurntableTest", group = "Test")
    public static class TurntableTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private TableServo tableServo;
        private IntakeMotor intakeMotor;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private boolean testPassed = false;
        private int testStep = 0;
        private double velocityThreshold = 0.1; // Velocity threshold to consider "stopped"

        @Override
        public void RevAmpedInit() {
            intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
            intakeMotor.setVoltageCompensation();
            tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intakeMotor));
            testTimer = new ElapsedTime();
            testRunning = true;
            testStep = 0;
            testPassed = false;
            testTimer.reset();
        }

        @Override
        public void RevAmpedLoop() {
            intakeMotor.update();
            tableServo.update();

            // Run automated test
            if (testRunning) {
                runTest();
            }

            // Display telemetry
            telemetry.clear();
            telemetry.addData("Turntable Tester", "");
            telemetry.addData("Table State", tableServo.getCurrentState().toString());
            telemetry.addData("Table Position", tableServo.getCurrentPosition());
            telemetry.addData("Table Velocity", String.format(Locale.US, "%.3f", tableServo.getVelocity()));
            telemetry.addData("Intake State", intakeMotor.getCurrentState().toString());

            if (testRunning) {
                telemetry.addData("Mode", "AUTO TEST - Step " + (testStep + 1) + "/4");
                telemetry.addData("Time", String.format(Locale.US, "%.1f", testTimer.seconds()) + "s");
            } else if (testPassed) {
                telemetry.addData("Mode", "TEST PASSED - Turntable working correctly!");
            } else {
                telemetry.addData("Mode", "TEST FAILED - Target positions not reached!");
            }

            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return Robot.OpModeType.TEST;
        }

        private void runTest() {
            switch (testStep) {
                case 0:
                    // Move to BALL1
                    tableServo.setCurrentState(Types.TableState.BALL1);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 1:
                    // Check if velocity has reached 0 (target reached)
                    if (Math.abs(tableServo.getVelocity()) < velocityThreshold || testTimer.milliseconds() > 3000) {
                        boolean targetReached = Math.abs(tableServo.getVelocity()) < velocityThreshold;
                        telemetry.addData("Target Test", targetReached ? "BALL1 position reached" : "BALL1 position FAILED");
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 2:
                    // Move to BALL2
                    tableServo.setCurrentState(Types.TableState.BALL2);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 3:
                    // Check if velocity has reached 0 (target reached)
                    if (Math.abs(tableServo.getVelocity()) < velocityThreshold || testTimer.milliseconds() > 3000) {
                        boolean targetReached = Math.abs(tableServo.getVelocity()) < velocityThreshold;
                        testRunning = false;
                        testPassed = targetReached;
                        telemetry.addData("Target Test", targetReached ? "BALL2 position reached" : "BALL2 position FAILED");
                        telemetry.addData("Test", testPassed ? "PASSED - Turntable working correctly!" : "FAILED - Target positions not reached!");
                    }
                    break;
            }
        }
    }

    // Motor Test
    @TeleOp(name = "MotorTest", group = "Test")
    public static class MotorTest extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private Flywheel flywheel;
        private TeleOpMecanumDrive drivetrain;
        private IntakeMotor intakeMotor;
        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private int testStep = 0;

        // Current readings
        private double flywheelCurrentStart = 0;
        private double flywheelCurrentEnd = 0;
        private double driveCurrentStart = 0;
        private double driveCurrentEnd = 0;
        private double intakeCurrentStart = 0;
        private double intakeCurrentEnd = 0;

        @Override
        public void RevAmpedInit() {
            flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
            drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD
            ));
            intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
            intakeMotor.setVoltageCompensation();
            testTimer = new ElapsedTime();
            testRunning = true;
            testStep = 0;
            testTimer.reset();
        }

        @Override
        public void RevAmpedLoop() {
            flywheel.update();
            intakeMotor.update();

            // Run automated test
            if (testRunning) {
                runTest();
            }

            // Display telemetry
            telemetry.clear();
            telemetry.addData("MOTOR TESTER", "");
            telemetry.addData("Flywheel State", flywheel.getCurrentState().toString());
            telemetry.addData("Flywheel Current", String.format(Locale.US, "%.2f", flywheel.getCurrentAmps()) + "A");
            telemetry.addData("Flywheel Power", String.format(Locale.US, "%.2f", flywheel.getPower()));
            telemetry.addData("Drive Current", String.format(Locale.US, "%.2f",
                    (drivetrain.leftFront.getCurrent(CurrentUnit.AMPS) +
                            drivetrain.leftRear.getCurrent(CurrentUnit.AMPS) +
                            drivetrain.rightFront.getCurrent(CurrentUnit.AMPS) +
                            drivetrain.rightRear.getCurrent(CurrentUnit.AMPS)) / 4.0) + "A");
            telemetry.addData("Intake State", intakeMotor.getCurrentState().toString());
            telemetry.addData("Intake Current", String.format(Locale.US, "%.2f", intakeMotor.getCurrentAmps()) + "A");
            telemetry.addData("Intake Power", String.format(Locale.US, "%.2f", intakeMotor.getPower()));

            if (testRunning) {
                telemetry.addData("Test", "Running Step " + (testStep + 1) + "/4");
                telemetry.addData("Time", String.format(Locale.US, "%.1f", testTimer.seconds()) + "s");
            } else {
                telemetry.addData("Test", "Test Complete");
            }

            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return Robot.OpModeType.TEST;
        }

        private void runTest() {
            switch (testStep) {
                case 0:
                    // Test flywheel
                    if (testTimer.milliseconds() < 1000) {
                        // Initial current
                        flywheelCurrentStart = flywheel.getCurrentAmps();
                    } else if (testTimer.milliseconds() < 2000) {
                        // Run flywheel
                        flywheel.setCurrentState(Types.FlywheelState.RUNNING);
                        flywheel.setPower(0.2f);
                    } else if (testTimer.milliseconds() > 3000) {
                        // Final current
                        flywheelCurrentEnd = flywheel.getCurrentAmps();
                        flywheel.setCurrentState(Types.FlywheelState.STOPPED);
                        testStep++;
                        testTimer.reset();
                    }
                    break;

                case 1:
                    // Test drivetrain
                    if (testTimer.milliseconds() < 1000) {
                        // Initial current
                        driveCurrentStart = (drivetrain.leftFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.leftRear.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightRear.getCurrent(CurrentUnit.AMPS)) / 4.0;
                    } else if (testTimer.milliseconds() < 2000) {
                        // Drivetrain power
                        drivetrain.setPowers(0.2f, 0.2f, 0.2f, 0.2f);
                    } else if (testTimer.milliseconds() > 3000) {
                        // Final current
                        driveCurrentEnd = (drivetrain.leftFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.leftRear.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightRear.getCurrent(CurrentUnit.AMPS)) / 4.0;
                        drivetrain.setPowers(0f, 0f, 0f, 0f);
                        testStep++;
                        testTimer.reset();
                    }
                    break;

                case 2:
                    // Test intake motor
                    if (testTimer.milliseconds() < 1000) {
                        // Initial current
                        intakeCurrentStart = intakeMotor.getCurrentAmps();
                    } else if (testTimer.milliseconds() < 2000) {
                        // Run intake motor
                        intakeMotor.setCurrentState(Types.IntakeState.INTAKE);
                    } else if (testTimer.milliseconds() > 3000) {
                        // Final current
                        intakeCurrentEnd = intakeMotor.getCurrentAmps();
                        intakeMotor.setCurrentState(Types.IntakeState.STOP);
                        testStep++;
                        testTimer.reset();
                    }
                    break;

                case 3:
                    // Results - Display and validate collected data
                    if (testTimer.milliseconds() > 2000) {
                        testRunning = false;
                        telemetry.addData("Test", "Complete - Results:");
                        telemetry.addData("Flywheel Start Current", String.format(Locale.US, "%.2f", flywheelCurrentStart) + "A");
                        telemetry.addData("Flywheel End Current", String.format(Locale.US, "%.2f", flywheelCurrentEnd) + "A");
                        telemetry.addData("Flywheel Current Change", String.format(Locale.US, "%.2f", flywheelCurrentEnd - flywheelCurrentStart) + "A");
                        telemetry.addData("Drive Start Current", String.format(Locale.US, "%.2f", driveCurrentStart) + "A");
                        telemetry.addData("Drive End Current", String.format(Locale.US, "%.2f", driveCurrentEnd) + "A");
                        telemetry.addData("Drive Current Change", String.format(Locale.US, "%.2f", driveCurrentEnd - driveCurrentStart) + "A");
                        telemetry.addData("Intake Start Current", String.format(Locale.US, "%.2f", intakeCurrentStart) + "A");
                        telemetry.addData("Intake End Current", String.format(Locale.US, "%.2f", intakeCurrentEnd) + "A");
                        telemetry.addData("Intake Current Change", String.format(Locale.US, "%.2f", intakeCurrentEnd - intakeCurrentStart) + "A");

                        // Basic validation - check if motors are drawing reasonable current
                        boolean flywheelWorking = Math.abs(flywheelCurrentEnd - flywheelCurrentStart) > 0.1;
                        boolean driveWorking = Math.abs(driveCurrentEnd - driveCurrentStart) > 0.1;
                        boolean intakeWorking = Math.abs(intakeCurrentEnd - intakeCurrentStart) > 0.1;

                        telemetry.addData("Flywheel Status", flywheelWorking ? "PASS" : "FAIL - No current change");
                        telemetry.addData("Drive Status", driveWorking ? "PASS" : "FAIL - No current change");
                        telemetry.addData("Intake Status", intakeWorking ? "PASS" : "FAIL - No current change");
                    }
                    break;
            }
        }
    }

    // Full Test Sequence - Runs all tests automatically
    @TeleOp(name = "FullTestSequence", group = "Test")
    public static class FullTestSequence extends org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode {
        private Popper popper;
        private IntakeColor leftColorSensor;
        private IntakeColor rightColorSensor;
        private TableServo tableServo;
        private IntakeMotor intakeMotor;
        private Flywheel flywheel;
        private TeleOpMecanumDrive drivetrain;

        private ElapsedTime testTimer;
        private boolean testRunning = false;
        private int currentTest = 0;
        private int testStep = 0;
        private boolean testPassed = false;
        private boolean allTestsPassed = true;

        // Test results
        private boolean popperTestPassed = false;
        private boolean colorTestPassed = false;
        private boolean turntableTestPassed = false;
        private boolean motorTestPassed = false;

        // Motor test variables
        private double flywheelCurrentStart = 0;
        private double flywheelCurrentEnd = 0;
        private double driveCurrentStart = 0;
        private double driveCurrentEnd = 0;
        private double intakeCurrentStart = 0;
        private double intakeCurrentEnd = 0;

        @Override
        public void RevAmpedInit() {
            // Initialize all components
            popper = new Popper(hardwareMap, false);
            leftColorSensor = new IntakeColor(hardwareMap, "color_left", 0.0);
            rightColorSensor = new IntakeColor(hardwareMap, "color_right", 0.0);
            intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
            intakeMotor.setVoltageCompensation();
            tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intakeMotor));
            flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
            drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD,
                    DcMotorSimple.Direction.FORWARD
            ));

            testTimer = new ElapsedTime();
            testRunning = true;
            currentTest = 0;
            testStep = 0;
            allTestsPassed = true;
            testTimer.reset();
        }

        @Override
        public void RevAmpedLoop() {
            // Update all components
            popper.update();
            leftColorSensor.update();
            rightColorSensor.update();
            intakeMotor.update();
            tableServo.update();
            flywheel.update();

            // Run test sequence
            if (testRunning) {
                runTestSequence();
            }

            // Display telemetry
            telemetry.clear();
            telemetry.addData("FULL TEST SEQUENCE", "");

            if (testRunning) {
                String[] testNames = {"Popper Test", "Color Sensor Test", "Turntable Test", "Motor Test"};
                telemetry.addData("Current Test", testNames[currentTest] + " - Step " + (testStep + 1));
                telemetry.addData("Time", String.format(Locale.US, "%.1f", testTimer.seconds()) + "s");
            } else if (allTestsPassed && testTimer.milliseconds() > 0) {
                telemetry.addData("Status", "ALL TESTS PASSED!");
                telemetry.addData("Popper Test", popperTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Color Test", colorTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Turntable Test", turntableTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Motor Test", motorTestPassed ? "PASS" : "FAIL");
            } else if (testTimer.milliseconds() > 0) {
                telemetry.addData("Status", "SOME TESTS FAILED!");
                telemetry.addData("Popper Test", popperTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Color Test", colorTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Turntable Test", turntableTestPassed ? "PASS" : "FAIL");
                telemetry.addData("Motor Test", motorTestPassed ? "PASS" : "FAIL");
            } else {
                telemetry.addData("Status", "All tests completed.");
            }

            telemetry.update();
        }

        @Override
        public void update() {}

        @Override
        public org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType getOpModeType() {
            return Robot.OpModeType.TEST;
        }

        private void runTestSequence() {
            switch (currentTest) {
                case 0: // Popper Test
                    runPopperTest();
                    break;
                case 1: // Color Sensor Test
                    runColorTest();
                    break;
                case 2: // Turntable Test
                    runTurntableTest();
                    break;
                case 3: // Motor Test
                    runMotorTest();
                    break;
            }
        }

        private void runPopperTest() {
            switch (testStep) {
                case 0:
                    popper.setCurrentState(Types.PopperState.POP);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 1:
                    boolean upVoltageReached = popper.reached(Types.PopperVoltageState.FULLY_UP);
                    if (upVoltageReached || testTimer.milliseconds() > 3000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 2:
                    popper.setCurrentState(Types.PopperState.NEUTRAL);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 3:
                    boolean downVoltageReached = popper.reached(Types.PopperVoltageState.FULLY_DOWN);
                    if (downVoltageReached || testTimer.milliseconds() > 3000) {
                        popperTestPassed = downVoltageReached;
                        allTestsPassed &= popperTestPassed;
                        currentTest++;
                        testStep = 0;
                        testTimer.reset();
                    }
                    break;
            }
        }

        private void runColorTest() {
            switch (testStep) {
                case 0:
                    if (testTimer.milliseconds() > 2000) {
                        boolean leftTest = leftColorSensor.testResponse();
                        boolean rightTest = rightColorSensor.testResponse();
                        colorTestPassed = leftTest && rightTest;
                        allTestsPassed &= colorTestPassed;
                        currentTest++;
                        testStep = 0;
                        testTimer.reset();
                    }
                    break;
            }
        }

        private void runTurntableTest() {
            switch (testStep) {
                case 0:
                    tableServo.setCurrentState(Types.TableState.BALL1);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 1:
                    if (Math.abs(tableServo.getVelocity()) < 0.1 || testTimer.milliseconds() > 3000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 2:
                    tableServo.setCurrentState(Types.TableState.BALL2);
                    if (testTimer.milliseconds() > 2000) {
                        testStep++;
                        testTimer.reset();
                    }
                    break;
                case 3:
                    if (Math.abs(tableServo.getVelocity()) < 0.1 || testTimer.milliseconds() > 3000) {
                        turntableTestPassed = Math.abs(tableServo.getVelocity()) < 0.1;
                        allTestsPassed &= turntableTestPassed;
                        currentTest++;
                        testStep = 0;
                        testTimer.reset();
                    }
                    break;
            }
        }

        private void runMotorTest() {
            switch (testStep) {
                case 0:
                    if (testTimer.milliseconds() < 1000) {
                        flywheelCurrentStart = flywheel.getCurrentAmps();
                        driveCurrentStart = (drivetrain.leftFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.leftRear.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightRear.getCurrent(CurrentUnit.AMPS)) / 4.0;
                        intakeCurrentStart = intakeMotor.getCurrentAmps();
                    } else if (testTimer.milliseconds() < 2000) {
                        flywheel.setCurrentState(Types.FlywheelState.RUNNING);
                        flywheel.setPower(0.2f);
                        drivetrain.setPowers(0.2f, 0.2f, 0.2f, 0.2f);
                        intakeMotor.setCurrentState(Types.IntakeState.INTAKE);
                    } else if (testTimer.milliseconds() > 3000) {
                        flywheelCurrentEnd = flywheel.getCurrentAmps();
                        driveCurrentEnd = (drivetrain.leftFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.leftRear.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightFront.getCurrent(CurrentUnit.AMPS) +
                                drivetrain.rightRear.getCurrent(CurrentUnit.AMPS)) / 4.0;
                        intakeCurrentEnd = intakeMotor.getCurrentAmps();

                        flywheel.setCurrentState(Types.FlywheelState.STOPPED);
                        drivetrain.setPowers(0f, 0f, 0f, 0f);
                        intakeMotor.setCurrentState(Types.IntakeState.STOP);

                        boolean flywheelWorking = Math.abs(flywheelCurrentEnd - flywheelCurrentStart) > 0.1;
                        boolean driveWorking = Math.abs(driveCurrentEnd - driveCurrentStart) > 0.1;
                        boolean intakeWorking = Math.abs(intakeCurrentEnd - intakeCurrentStart) > 0.1;

                        motorTestPassed = flywheelWorking && driveWorking && intakeWorking;
                        allTestsPassed &= motorTestPassed;
                        testRunning = false;
                    }
                    break;
            }
        }
    }
}
