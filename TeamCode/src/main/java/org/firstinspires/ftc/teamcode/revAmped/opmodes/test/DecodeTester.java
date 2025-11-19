package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Logger;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Disabled
@TeleOp(name = "DecodeTester")
public class DecodeTester extends DecodeRevAmpedOpMode {
    private Robot robot;
    private Follower follower;

    private List<HwDevice> devicesToTest;
    private int currentDeviceIndex = -1;
    private HwDevice currentDevice;
    private boolean testRunning = false;
    private Boolean testResult = null;
    private String testStatusMessage = "";

    private boolean driveTestComplete = false;
    private int driveTestState = 0;
    private boolean case1Executed = false;
    private double forwardDisp = 0;
    private double lateralDisp = 0;
    private Pose prevPose = new Pose();

    // Button state tracking for detecting button presses
    private boolean prevA = false;
    private boolean prevB = false;
    private boolean prevX = false;

    private enum TestPhase {
        DEVICE_TESTING,
        DRIVE_TESTING,
        COMPLETE
    }
    private TestPhase currentPhase = TestPhase.DEVICE_TESTING;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        follower = Constants.createFollower(hardwareMap);

        // Build list of all devices to test
        devicesToTest = new ArrayList<>();
        for (HwDevice.DeviceType type : robot.allDevices.keySet()) {
            devicesToTest.addAll(Objects.requireNonNull(robot.allDevices.get(type)));
        }

        // Initialize pose tracking
        Pose initialPose = follower.getPose();
        prevPose = new Pose(initialPose.getX(), initialPose.getY(), initialPose.getHeading());
    }

    @Override
    public void RevAmpedStart() {
        moveToNextDevice();
    }

    @Override
    public void RevAmpedLoop() {
        if (currentPhase == TestPhase.DEVICE_TESTING) {
            handleDeviceTesting();
        }
        else if (currentPhase == TestPhase.DRIVE_TESTING) {
            handleDriveTesting();
        }
        else {
            displayCompleteMessage();
        }

        // Update button states at the end for next iteration
        updateButtonStates();
    }

    private void updateButtonStates() {
        prevA = gamepad1.a;
        prevB = gamepad1.b;
        prevX = gamepad1.x;
    }

    private boolean aWasPressed() {
        return gamepad1.a && !prevA;
    }

    private boolean bWasPressed() {
        return gamepad1.b && !prevB;
    }

    private boolean xWasPressed() {
        return gamepad1.x && !prevX;
    }

    private void handleDeviceTesting() {
        if (currentDeviceIndex >= devicesToTest.size()) {
            // Wait for user to confirm before moving to drive test
            if (aWasPressed()) {
                currentPhase = TestPhase.DRIVE_TESTING;
                driveTestState = 0;
                case1Executed = false;
                driveTestComplete = false;
                forwardDisp = 0;
                lateralDisp = 0;
                // Reset pose tracking for drive test
                Pose initialPose = follower.getPose();
                prevPose = new Pose(initialPose.getX(), initialPose.getY(), initialPose.getHeading());
            }
            displayDeviceTestTelemetry();
            return;
        }

        if (currentDevice == null) {
            currentDevice = devicesToTest.get(currentDeviceIndex);
        }

        if (!testRunning && testResult == null) {
            if (aWasPressed()) {
                startCurrentDeviceTest();
            } else if (bWasPressed() && currentDeviceIndex > 0) {
                moveToNextDevice();
            }
        } else if (testRunning) {
            // Test is running, we wait for completion
        } else {
            // testResult != null at this point
            if (aWasPressed()) {
                moveToNextDevice();
            } else if (xWasPressed()) {
                testResult = null;
                testStatusMessage = "";
            }
        }

        displayDeviceTestTelemetry();
    }

    private void startCurrentDeviceTest() {
        if (currentDevice == null) return;

        testRunning = true;
        testResult = null;
        testStatusMessage = "Testing";

        currentDevice.testResponse(s -> {
            testStatusMessage = s;
            Logger.permaLog("Test Status", s);
        }).andThen(result -> {
            testRunning = false;
            testResult = result;
            if (result != null) {
                testStatusMessage = result ? "PASSED" : "FAILED";
                Logger.permaLog(currentDevice.getId() + " Result", result ? "PASSED" : "FAILED");

                if (currentDevice instanceof Popper) {
                    ((Popper) currentDevice).setCurrentState(Types.PopperState.NEUTRAL);
                }
            } else {
                testStatusMessage = "INCOMPLETE";
            }
        });
    }

    private void moveToNextDevice() {
        currentDeviceIndex++;
        currentDevice = null;
        testRunning = false;
        testResult = null;
        testStatusMessage = "";

        if (currentDeviceIndex < devicesToTest.size()) {
            currentDevice = devicesToTest.get(currentDeviceIndex);
        }
    }

    private void displayDeviceTestTelemetry() {
        telemetry.clear();
        telemetry.addLine("MANUAL DEVICE TESTER");

        if (currentDeviceIndex < devicesToTest.size()) {
            telemetry.addData("Device", String.format(Locale.US, "%d/%d", currentDeviceIndex + 1, devicesToTest.size()));
            telemetry.addData("Device Type", currentDevice != null ? currentDevice.getDeviceType().name() : "N/A");
            telemetry.addData("Device ID", currentDevice != null ? currentDevice.getId() : "N/A");

            if (!testRunning && testResult == null) {
                telemetry.addLine("Press A to START TEST");
                telemetry.addLine("Press B to SKIP this device");
            } else if (testRunning) {
                telemetry.addLine("TEST RUNNING");
                telemetry.addData("Status", testStatusMessage);
                telemetry.addLine("Please wait");
            } else {
                // testResult != null at this point
                telemetry.addLine("TEST COMPLETE");
                telemetry.addData("Result", testResult ? "PASSED" : "FAILED");
                telemetry.addData("Status", testStatusMessage);
                telemetry.addLine("Press A to continue to next device");
                telemetry.addLine("Press X to retry this test");
            }
        } else {
            telemetry.addLine("All device tests complete!");
            telemetry.addLine("Press A to start drive test");
        }

        Logger.update(telemetry);
        telemetry.update();
    }

    private void handleDriveTesting() {
        switch (driveTestState) {
            case 0:
                if (aWasPressed()) {
                    telemetry.addLine("Starting drive test");
                    follower.startTeleopDrive(false);
                    follower.update();
                    follower.setTeleOpDrive(1, 1, 1, true);
                    driveTestState++;
                }
                break;
            case 1:
                // Let robot move for 2 seconds, then stop it
                if (!case1Executed) {
                    case1Executed = true;
                    scheduler.add(new Delay(2000, () -> {
                        follower.startTeleopDrive(true);
                        follower.setTeleOpDrive(0, 0, 0, true);
                        driveTestState++;
                        scheduler.add(new Delay(() -> follower.getVelocity().getMagnitude() < PathConstraints.defaultConstraints.getVelocityConstraint(),
                                () -> driveTestComplete = true)
                                .setMaxTimeToRun(5000)); // 5 second timeout if robot never fully stops
                    }));
                }
                break;
            case 2:
                if (!driveTestComplete) {
                    Pose curPose = follower.getPose();
                    forwardDisp += (curPose.getX() - prevPose.getX()) * Math.cos(curPose.getHeading());
                    lateralDisp += (curPose.getY() - prevPose.getY()) * Math.sin(curPose.getHeading());
                    prevPose = new Pose(curPose.getX(), curPose.getY(), curPose.getHeading());
                } else {
                    driveTestState++;
                }
                break;
            case 3:
                Pose currentPose = follower.getPose();
                boolean headingLocalization = Math.abs(currentPose.getHeading()) > Math.toRadians(3);
                boolean forwardLocalization = forwardDisp > 0.5;
                boolean lateralLocalization = lateralDisp > 0.5;

                if (aWasPressed()) {
                    // Log drive test results
                    Logger.permaLog("Heading Localization", headingLocalization ? "PASSED" : "FAILED");
                    Logger.permaLog("Forward Localization", forwardLocalization ? "PASSED" : "FAILED");
                    Logger.permaLog("Lateral Localization", lateralLocalization ? "PASSED" : "FAILED");
                    Logger.permaLog("Forward Displacement", String.format(Locale.US, "%.3f", forwardDisp));
                    Logger.permaLog("Lateral Displacement", String.format(Locale.US, "%.3f", lateralDisp));
                    Logger.permaLog("Final Heading", String.format(Locale.US, "%.3f", Math.toDegrees(currentPose.getHeading())));
                    currentPhase = TestPhase.COMPLETE;
                }
                break;
        }

        displayDriveTestTelemetry();
    }

    private void displayDriveTestTelemetry() {
        telemetry.clear();
        telemetry.addLine("DRIVE/LOCALIZATION TEST");

        switch (driveTestState) {
            case 0:
                telemetry.addLine("Press A to START drive test");
                telemetry.addLine("The robot will move forward, strafe, and rotate.");
                telemetry.addLine("Then it will stop and measure displacement.");
                break;
            case 1:
                telemetry.addLine("DRIVE TEST RUNNING");
                telemetry.addLine("Robot is moving");
                break;
            case 2:
                telemetry.addLine("STOPPING ROBOT");
                telemetry.addLine("Waiting for robot to stop");
                telemetry.addData("Forward Displacement", String.format(Locale.US, "%.3f", forwardDisp));
                telemetry.addData("Lateral Displacement", String.format(Locale.US, "%.3f", lateralDisp));
                break;
            case 3:
                Pose currentPose = follower.getPose();
                boolean headingLocalization = Math.abs(currentPose.getHeading()) > Math.toRadians(3);
                boolean forwardLocalization = forwardDisp > 0.5;
                boolean lateralLocalization = lateralDisp > 0.5;

                telemetry.addLine("DRIVE TEST COMPLETE");
                telemetry.addData("Heading Localization", headingLocalization ? "✓ PASSED" : "✗ FAILED");
                telemetry.addData("Forward Localization", forwardLocalization ? "✓ PASSED" : "✗ FAILED");
                telemetry.addData("Lateral Localization", lateralLocalization ? "✓ PASSED" : "✗ FAILED");
                telemetry.addData("Forward Displacement", String.format(Locale.US, "%.3f", forwardDisp));
                telemetry.addData("Lateral Displacement", String.format(Locale.US, "%.3f", lateralDisp));
                telemetry.addData("Heading", String.format(Locale.US, "%.3f", Math.toDegrees(currentPose.getHeading())));
                telemetry.addLine("Press A to finish all tests");
                break;
        }

        telemetry.update();
    }

    private void displayCompleteMessage() {
        telemetry.clear();
        telemetry.addLine("ALL TESTS COMPLETE");
        telemetry.addLine("Device tests: " + devicesToTest.size() + " devices tested");
        telemetry.addLine("Drive test: Completed");
        telemetry.addLine("Check Logger output for detailed results.");
        Logger.update(telemetry);
        telemetry.update();
    }

    @Override
    public void update() {
        robot.update();
        follower.update();
        Logger.update(telemetry);
        // telemetry.update() is called in display methods, so not needed here
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
