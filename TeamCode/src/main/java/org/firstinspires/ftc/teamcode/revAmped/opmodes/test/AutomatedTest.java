package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;
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

import java.util.Objects;

@TeleOp
public class AutomatedTest extends DecodeRevAmpedOpMode {
    private Robot robot;
    private Follower follower;
    private boolean stop = false;
    private boolean driveTestComplete = false;
    private double forwardDisp;
    private double lateralDisp;
    private final Pose prevPose = new Pose();
    int driveTestState = 0;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        follower = Constants.createFollower(hardwareMap);
    }

    @Override
    public void RevAmpedStart() {
        robot.popper().testResponse(s -> Logger.permaLog("passed: ", s)).andThen(r -> {
            Logger.permaLog(robot.popper().getId() + " " + "passed", r);
            robot.popper().setCurrentState(Types.PopperState.NEUTRAL);
            scheduler.add(new Delay(600, () -> {
                for (HwDevice.DeviceType type : robot.allDevices.keySet()) {
                    telemetry.addData("Testing", type.name());
                    telemetry.update();
                    for (HwDevice device : Objects.requireNonNull(robot.allDevices.get(type))) {
                        if (!(device instanceof Popper))
                            device.testResponse(s -> Logger.permaLog("passed: ", s)).andThen(b -> Logger.permaLog(device.getId() + " " + "passed", b));
                    }
                }
            }));
        });
    }

    @Override
    public void RevAmpedLoop() {
        if (stop) driveTestState++;

        switch (driveTestState) {
            case 0:
                if (gamepad1.aWasPressed()) {
                    follower.startTeleopDrive(false);
                    follower.update();
                    follower.setTeleOpDrive(1,1,1,true);
                    stop = true;
                    driveTestState++;
                }
                break;
            case 1:
                if (stop) {
                    follower.startTeleopDrive(true);
                    follower.setTeleOpDrive(0,0,0,true);
                    stop = false;
                    driveTestState++;
                    scheduler.add(new Delay(() -> follower.getVelocity().getMagnitude() < PathConstraints.defaultConstraints.getVelocityConstraint(),
                            () -> driveTestComplete = true));
                }
                break;
            case 2:
                if (!driveTestComplete) {
                    Pose curPose = follower.getPose();
                    forwardDisp += (curPose.getX() - prevPose.getX()) * Math.cos(curPose.getHeading());
                    lateralDisp += (curPose.getY() - prevPose.getY()) * Math.sin(curPose.getHeading());
                } else
                    driveTestState++;
                break;
            case 3:
                Pose currentPose = follower.getPose();
                boolean headingLocalization = Math.abs(currentPose.getHeading()) > Math.toRadians(3);
                telemetry.addData("Heading localization passed", headingLocalization);
                telemetry.addData("Forward localization passed", forwardDisp > 0.5);
                telemetry.addData("Lateral localization passed", lateralDisp > 0.5);
                break;
        }
    }

    @Override
    public void update() {
        robot.update();
        follower.update();
        Logger.update(telemetry);
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}    