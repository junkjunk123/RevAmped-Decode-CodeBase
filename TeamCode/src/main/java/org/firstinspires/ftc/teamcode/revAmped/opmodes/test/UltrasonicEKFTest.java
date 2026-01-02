package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.pedropathing.follower.Follower;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwUltrasonicSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FusionLocalizer;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@TeleOp
public class UltrasonicEKFTest extends DecodeRevAmpedOpMode {
    private Follower follower;
    private HwUltrasonicSensor leftSensor;
    private HwUltrasonicSensor rightSensor;
    private static double TRACK_WIDTH;

    @Override
    public void RevAmpedInit() {
        follower = new FollowerBuilder(Constants.followerConstants, hardwareMap)
                .mecanumDrivetrain(Constants.driveConstants)
                .pathConstraints(Constants.pathConstraints)
                .setLocalizer(new FusionLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants),
                        Constants.fusionConstants))
                .build();
        leftSensor = new HwUltrasonicSensor(hardwareMap, "leftUltrasonic");
        rightSensor = new HwUltrasonicSensor(hardwareMap, "rightUltrasonic");
    }

    @Override
    public void RevAmpedLoop() {
        double leftXDist = leftSensor.getDistance();
        double rightXDist = rightSensor.getDistance();
        double sensorXPos = (leftXDist + rightXDist) / 2;
        double sensorHeading = Math.atan2(TRACK_WIDTH, leftXDist - rightXDist);

        ((FusionLocalizer) follower.getPoseTracker().getLocalizer()).addMeasurement(
                new Pose(sensorXPos, Double.NaN, sensorHeading),
                System.nanoTime()
        );

        telemetry.addData("ultrasonicX", sensorXPos);
        telemetry.addData("ultrasonicHeading", sensorHeading);
        telemetry.addData("pinpointX", follower.getPose().getX());
        telemetry.addData("pinpointHeading", follower.getPose().getHeading());
    }

    @Override
    public void update() {
        follower.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
