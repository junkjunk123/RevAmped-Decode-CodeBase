package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import com.pedropathing.localization.GoBildaPinpointDriver;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.NanoTimer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwDevice;
import revAmped.components.MathUtil.MathUtil;

public class PinpointGyro implements HwDevice {
    private String id;
    private double startHeading;
    private double currentHeading;
    private final GoBildaPinpointDriver pinpoint;
    private NanoTimer timer = new NanoTimer();
    private double deltaTime = 0;
    private double headingVelocity = 0;
    private boolean correctForFlying = false;

    public PinpointGyro(HardwareMap hardwareMap, String id, Pose startPose) {
        this.id = id;
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setOffsets(-78, 2.5);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();

        pinpoint.setPosition(new Pose(0,0,startPose.getHeading()));
        this.startHeading = startPose.getHeading();
        currentHeading = startHeading;
        timer.resetTimer();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update() {
        if (startHeading != currentHeading) {
            deltaTime = timer.getElapsedTimeSeconds();
        }
        timer.resetTimer();
        pinpoint.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);

        double pinpointHeadingEstimate = getHeadingEstimate(MathUtil.normalizeAnglePositive(pinpoint.getHeading()), currentHeading, deltaTime);
        double deltaHeading = pinpointHeadingEstimate - currentHeading;

        if (correctForFlying && Math.abs(deltaHeading) > 0.5) {
            pinpointHeadingEstimate = currentHeading;
            deltaHeading = 0;
            pinpoint.setPosition(new Pose(0,0,currentHeading));
        }

        headingVelocity = deltaHeading/deltaTime;
        currentHeading = pinpointHeadingEstimate;
    }

    public void resetHeading(double heading) {
        startHeading = heading - pinpoint.getHeading();
    }

    public void resetHeading() {
        resetHeading(0);
    }

    public double getHeading() {
        return currentHeading;
    }

    private double getHeadingEstimate(double headingEstimate, double previousHeading, double deltaTime) {
        if (Double.isNaN(headingEstimate)) {
            return previousHeading + deltaTime * headingVelocity;
        };

        return headingEstimate + startHeading;
    }

    public void setCorrectForFlying(boolean correctForFlying) {
        this.correctForFlying = correctForFlying;
    }

    public boolean isCorrectForFlying() {
        return correctForFlying;
    }
}
