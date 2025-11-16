package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.MathFunctions;
import com.pedropathing.math.Vector;

import java.util.NavigableMap;
import java.util.TreeMap;

public class LimelightLocalizer implements Localizer {
    private final Localizer deadReckoning;
    private Pose currentPosition;
    private Pose currentVelocity;
    private long lastUpdateTime = -1;

    private final NavigableMap<Long, Pose> poseHistory = new TreeMap<>();
    private final NavigableMap<Long, Pose> twistHistory = new TreeMap<>();

    public LimelightLocalizer(Localizer deadReckoning) {
        this.deadReckoning = deadReckoning;
        this.currentPosition = new Pose();
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        double dt = lastUpdateTime < 0 ? 0 : (now - lastUpdateTime) / 1e9;
        lastUpdateTime = now;

        // --- 1. Predict step via twist integration ---
        Pose twist = deadReckoning.getVelocity();
        currentVelocity = twist.copy();

        double cosH = Math.cos(currentPosition.getHeading());
        double sinH = Math.sin(currentPosition.getHeading());
        double dx = (twist.getX() * cosH - twist.getY() * sinH) * dt;
        double dy = (twist.getX() * sinH + twist.getY() * cosH) * dt;
        double dTheta = twist.getHeading() * dt;

        currentPosition = new Pose(
                currentPosition.getX() + dx,
                currentPosition.getY() + dy,
                MathFunctions.normalizeAngle(currentPosition.getHeading() + dTheta)
        );

        // Store the predicted pose
        poseHistory.put(now, currentPosition.copy());
        twistHistory.put(now, twist.copy());
    }

    /**
     * Correct the pose using the Limelight's estimated pose and forward propagate
     * @param measuredPose Limelight's estimated pose
     * @param timestamp when the Limelight measurement was taken
     */
    public void addVisionMeasurement(Pose measuredPose, long timestamp) {
        // 1. Correct the past pose with the Limelight estimate
        if (!poseHistory.containsKey(timestamp)) return;

        // Get the predicted pose at the time of the Limelight measurement
        Pose predictedPose = poseHistory.get(timestamp);

        // Latency correction: calculate time delta
        long timeDelta = System.nanoTime() - timestamp;
        double timeSec = timeDelta / 1e9;

        // Now apply the Limelight pose correction (based on the time delta)
        currentPosition = new Pose(
                measuredPose.getX(),
                measuredPose.getY(),
                MathFunctions.normalizeAngle(measuredPose.getHeading())
        );

        // Update history with the new corrected pose
        poseHistory.put(timestamp, currentPosition.copy());

        // 2. Forward propagate the corrected pose
        // Propagate forward using twist integration
        long previousTime = timestamp;
        Pose previousPose = currentPosition;
        for (NavigableMap.Entry<Long, Pose> entry : poseHistory.tailMap(timestamp, false).entrySet()) {
            long t = entry.getKey();
            Pose twist = twistHistory.get(previousTime);
            double dt = (t - previousTime) / 1e9;
            Pose nextPose = integrate(previousPose, twist, dt);
            poseHistory.put(t, nextPose);
            previousPose = nextPose;
            previousTime = t;
        }
    }

    private Pose integrate(Pose previousPose, Pose twist, double dt) {
        double cosH = Math.cos(previousPose.getHeading());
        double sinH = Math.sin(previousPose.getHeading());
        double dx = (twist.getX() * cosH - twist.getY() * sinH) * dt;
        double dy = (twist.getX() * sinH + twist.getY() * cosH) * dt;
        double dTheta = twist.getHeading() * dt;

        return new Pose(
                previousPose.getX() + dx,
                previousPose.getY() + dy,
                MathFunctions.normalizeAngle(previousPose.getHeading() + dTheta)
        );
    }

    @Override
    public Pose getPose() { return currentPosition; }

    @Override
    public Pose getVelocity() {
        return currentVelocity != null ? currentVelocity : deadReckoning.getVelocity();
    }

    @Override
    public Vector getVelocityVector() { return getVelocity().getAsVector(); }

    @Override
    public void setStartPose(Pose setStart) {
        deadReckoning.setStartPose(setStart);
    }

    @Override
    public void setPose(Pose setPose) {
        currentPosition = setPose.copy();
        deadReckoning.setPose(setPose);
    }

    @Override
    public double getTotalHeading() { return currentPosition.getHeading(); }

    @Override
    public double getForwardMultiplier() { return deadReckoning.getForwardMultiplier(); }

    @Override
    public double getLateralMultiplier() { return deadReckoning.getLateralMultiplier(); }

    @Override
    public double getTurningMultiplier() { return deadReckoning.getTurningMultiplier(); }

    @Override
    public void resetIMU() throws InterruptedException { deadReckoning.resetIMU(); }

    @Override
    public double getIMUHeading() { return deadReckoning.getIMUHeading(); }

    @Override
    public boolean isNAN() {
        return Double.isNaN(currentPosition.getX()) || Double.isNaN(currentPosition.getY()) || Double.isNaN(currentPosition.getHeading());
    }
}
