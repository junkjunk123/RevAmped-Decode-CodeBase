package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import static revAmped.components.Misc.Globals.allianceColor;

import android.util.Log;

import com.pedropathing.localization.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import revAmped.components.Hardware.HwServo2;
import revAmped.components.MathUtil.MathUtil;
import revAmped.util.Range;

public class Limelight {
    public Limelight3A limelight;
    private boolean detecting;
    private Pose samplePosition;
    private double turretAngle;
    private double hslidePosInches;
    private float wristAngle;
    private final double armLengthInches = 65.0/16.0;

    public Limelight(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(7);
        limelight.setPollRateHz(10);
    }

    public void start() {
        limelight.start();
        detecting = true;
    }

    public boolean isDetecting() {
        return detecting;
    }

    public void update() {
        if (detecting) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                List<LLResultTypes.DetectorResult> output = result.getDetectorResults();
                output.removeIf(i -> !Objects.equals(i.getClassName(), allianceColor.name().toLowerCase()));
                HashMap<LLResultTypes.DetectorResult, Pose> potentialResults = new HashMap<>();

                for (LLResultTypes.DetectorResult u : output) {
                    double[] inches = MathUtil.homographyTransformation(Constants.HOMOGRAPHY_MATRIX.getMatrix(), u.getTargetXPixels(), u.getTargetYPixels());
                    potentialResults.put(u, new Pose(11 - inches[1], 7 - inches[0]));
                }

                LLResultTypes.DetectorResult sample = potentialResults.keySet().stream()
                        .filter(u -> potentialResults.get(u).getX() > 4.25 && potentialResults.get(u).getY() > 0)
                        .reduce((w, z) -> {
                            Pose u = potentialResults.get(w);
                            Pose v = potentialResults.get(z);
                            return u.getY() * u.getY() + u.getX() * u.getX() < v.getX() * v.getX() + v.getY() * v.getY() ? w : z;
                        })
                        .orElse(null);
                samplePosition = potentialResults.get(sample);

                if (samplePosition != null && !Double.isNaN(samplePosition.getY()) && !Double.isNaN(samplePosition.getX())) {
                    turretAngle = -Math.asin(samplePosition.getY() / armLengthInches);
                    boolean rotateWrist = computeWristRotation(sample);
                    wristAngle = rotateWrist ? 0 : (float) -Math.PI / 2;
                    wristAngle = HwServo2.getPositionFromRad(MathUtil.normalizeAngleHalfPi(wristAngle - turretAngle), Math.toRadians(-90), Math.toRadians(90), Constants.INTAKE_WRIST_INTAKE, Constants.WRIST_INVERTED);
                    hslidePosInches = samplePosition.getX() - Math.cos(turretAngle) * armLengthInches;
                    hslidePosInches += hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION) + hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION) * hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION);
                    if (hslidePosInches > 6.75 && !rotateWrist)
                        hslidePosInches += MathUtil.sigmoid(Range.scale(hslidePosInches, 6.75, 8, -5.5, 6)) * 3.5;
                    else if (rotateWrist && hslidePosInches > 6.75)
                        hslidePosInches += MathUtil.sigmoid(Range.scale(hslidePosInches, 6.75, 8, -5.5, 6)) * 1.75;
                    if (!Double.isNaN(turretAngle) && !Double.isNaN(wristAngle) && !Double.isNaN(hslidePosInches)) {
                        detecting = false;
                        limelight.pause();
                    }
                }
            }
        }
    }

    private boolean computeWristRotation(LLResultTypes.DetectorResult result) {
        List<List<Double>> corners = result.getTargetCorners();
        Double[] xVals = corners.stream().map(u -> u.get(0)).toArray(Double[]::new);
        Double[] yVals = corners.stream().map(u -> u.get(1)).toArray(Double[]::new);
        if (corners.isEmpty()) throw new RuntimeException("no corners");
        double maxX = Arrays.stream(xVals).max(Double::compareTo).orElse(Double.NaN);
        double maxY = Arrays.stream(yVals).max(Double::compareTo).orElse(Double.NaN);
        double minX = Arrays.stream(xVals).min(Double::compareTo).orElse(Double.NaN);
        double minY = Arrays.stream(yVals).min(Double::compareTo).orElse(Double.NaN);
        if (Double.isNaN(maxX)) throw new RuntimeException("corners aint cornering");
        return Math.abs(maxY - minY) < Math.abs(maxX - minX);
    }

    public float wristAngle() {
        return wristAngle;
    }

    public double hslidePosInches() {
        return hslidePosInches;
    }

    public double turretAngle() {
        return turretAngle;
    }

    public boolean isDone() {
        return detecting = false;
    }
}
