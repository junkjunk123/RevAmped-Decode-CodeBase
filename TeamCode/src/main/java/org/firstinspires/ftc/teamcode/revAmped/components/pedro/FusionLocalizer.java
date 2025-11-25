package org.firstinspires.ftc.teamcode.revAmped.components.pedro;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.MathFunctions;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.MatrixUtil;
import com.pedropathing.math.Vector;

import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Full Pedro Pathing FusionLocalizer implementation.
 * Predicts pose with twist integration and corrects with delayed measurements.
 */
public class FusionLocalizer implements Localizer {
    private final Localizer deadReckoning;
    private Pose currentPosition;
    private Pose currentVelocity;
    private Matrix P;      // Covariance
    private final Matrix Q; // Process noise, and in the nonlinear dynamics, twist noise
    private final Matrix R; // Measurement noise
    private long lastUpdateTime = -1;

    private final NavigableMap<Long, Pose> poseHistory = new TreeMap<>();
    private final NavigableMap<Long, Pose> twistHistory = new TreeMap<>();
    private final int bufferSize;
    private final boolean useNonlinearDynamics;

    public FusionLocalizer(
            Localizer deadReckoning,
            double[] P, //initial uncertainty in odometry estimate (inch, rad)
            double[] processVariance, //uncertainty in odometry estimates (drift in inch/s, rad/s)
            double[] measurementVariance, //uncertainty in vision estimates (drift in inch/s, rad/s)
            int bufferSize,
            boolean useNonlinearDynamics
    ) {
        this.deadReckoning = deadReckoning;
        this.useNonlinearDynamics = useNonlinearDynamics;
        this.currentPosition = new Pose();
        this.P = MathUtil.diagonal3(
                P[0],
                P[1],
                P[2]
        );
        this.Q = MathUtil.diagonal3(
                processVariance[0],
                processVariance[1],
                processVariance[2]
        );
        this.R = MathUtil.diagonal3(
                measurementVariance[0],
                measurementVariance[1],
                measurementVariance[2]
        );
        this.bufferSize = bufferSize;
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        double dt = lastUpdateTime < 0 ? 0 : (now - lastUpdateTime) / 1e9;
        lastUpdateTime = now;

        // --- 1. Predict step via twist integration ---
        Pose twist = deadReckoning.getVelocity();
        twistHistory.put(now, twist.copy());
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

        // Covariance propagation
        if (!useNonlinearDynamics)
            P = P.plus(Q.multiply(dt));
        else {
            double theta = currentPosition.getHeading();
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            double xVel = currentVelocity.getX();
            double yVel = currentVelocity.getY();

            Matrix A = new Matrix(new double[][]{
                    {0, 0, -xVel * sin - yVel * cos},
                    {0, 0, xVel * cos - yVel * sin},
                    {0, 0, 0}
            });

            Matrix B = new Matrix(new double[][]{
                    {cos, -sin, 0},
                    {sin, cos, 0},
                    {0, 0, 1}
            });

            Matrix F = MathUtil.identity(3).plus(A.multiply(dt));
            Matrix G = B.multiply(dt);
            Matrix Q_d = G.multiply(Q).multiply(G.transposed());
            P = F.multiply(P).multiply(F.transposed()).plus(Q_d);
        }

        // Add to history
        poseHistory.put(now, currentPosition.copy());
        if (poseHistory.size() > bufferSize) poseHistory.pollFirstEntry();
        if (twistHistory.size() > bufferSize) twistHistory.pollFirstEntry();
    }

    /**
     * Adds a delayed measurement and updates past poses.
     * @param measuredPose measured pose (vision/other sensor)
     * @param timestamp when the measurement was taken
     */
    public void addMeasurement(Pose measuredPose, long timestamp) {
        if (!poseHistory.containsKey(timestamp)) return;

        // --- 1. Compute innovation ---
        Pose pastPose = interpolate(timestamp, poseHistory);
        if (pastPose == null)
            pastPose = getPose();
        Matrix y = new Matrix(new double[][]{
                {measuredPose.getX() - pastPose.getX()},
                {measuredPose.getY() - pastPose.getY()},
                {MathFunctions.normalizeAngle(measuredPose.getHeading() - pastPose.getHeading())}
        });

        // --- 2. Compute Kalman gain ---
        Matrix S = P.plus(R);
        Matrix K = P.multiply(MathUtil.invert3x3(S));

        // --- 3. Update past pose ---
        Matrix K_y = K.multiply(y);
        Pose updatedPast = new Pose(
                pastPose.getX() + K_y.get(0,0),
                pastPose.getY() + K_y.get(1,0),
                MathFunctions.normalizeAngle(pastPose.getHeading() + K_y.get(2,0))
        );
        poseHistory.put(timestamp, updatedPast);

        // --- 4. Propagate update forward using stored twists ---
        long previousTime = timestamp;
        Pose previousPose = updatedPast;
        for (NavigableMap.Entry<Long, Pose> entry : poseHistory.tailMap(timestamp, false).entrySet()) {
            long t = entry.getKey();
            Pose twist = interpolate(timestamp, twistHistory); // use the twist applied at previous time
            if (twist == null)
                twist = getVelocity();
            double dt = (t - previousTime) / 1e9;
            Pose nextPose = integrate(previousPose, twist, dt);
            poseHistory.put(t, nextPose);
            previousPose = nextPose;
            previousTime = t;
        }

        // --- 5. Update current state ---
        currentPosition = poseHistory.lastEntry().getValue();
    }

    private static Pose interpolate(long timestamp, NavigableMap<Long, Pose> history) {
        Long lowerKey = history.floorKey(timestamp);
        Long upperKey = history.ceilingKey(timestamp);

        if (lowerKey == null || upperKey == null) {
            return null; // Cannot interpolate
        }
        if (lowerKey.equals(upperKey)) {
            return history.get(lowerKey).copy(); // Exact match
        }

        Pose lowerPose = history.get(lowerKey);
        Pose upperPose = history.get(upperKey);

        double ratio = (double) (timestamp - lowerKey) / (upperKey - lowerKey);

        double x = lowerPose.getX() + ratio * (upperPose.getX() - lowerPose.getX());
        double y = lowerPose.getY() + ratio * (upperPose.getY() - lowerPose.getY());
        double headingDiff = MathFunctions.getSmallestAngleDifference(upperPose.getHeading(), lowerPose.getHeading());
        double heading = MathFunctions.normalizeAngle(lowerPose.getHeading() + ratio * headingDiff);

        return new Pose(x, y, heading);
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
        poseHistory.lastEntry().setValue(setPose.copy());
    }

    @Override
    public double getTotalHeading() {
        return currentPosition.getHeading();
    }

    @Override
    public double getForwardMultiplier() {
        return deadReckoning.getForwardMultiplier();
    }

    @Override
    public double getLateralMultiplier() {
        return deadReckoning.getLateralMultiplier();
    }

    @Override
    public double getTurningMultiplier() {
        return deadReckoning.getTurningMultiplier();
    }

    @Override
    public void resetIMU() throws InterruptedException {
        deadReckoning.resetIMU();
    }

    @Override
    public double getIMUHeading() {
        return deadReckoning.getIMUHeading();
    }

    @Override
    public boolean isNAN() {
        return Double.isNaN(currentPosition.getX()) || Double.isNaN(currentPosition.getY()) || Double.isNaN(currentPosition.getHeading());
    }

    public boolean isUseNonlinearDynamics() {
        return useNonlinearDynamics;
    }
}