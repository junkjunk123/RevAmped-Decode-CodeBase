package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import com.pedropathing.pathgen.Point;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import revAmped.components.Hardware.HwDevice;
import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.Vector3;

public class ITDWebcamNew implements HwDevice {
    public enum DetectionTarget {
        Red,
        Blue,
        Yellow,
        RedAndYellow,
        BlueAndYellow,
        None
    }

    private DetectionTarget targetColor = DetectionTarget.None;
    private Point targetSample = new Point(0,0,1);
    private LLResult results = null;
    private String id;
    public Limelight3A webcam;
    private List<String> currentTarget = new ArrayList<>();

    private static final double CONFIDENCE_THRESHOLD = 0.3;    // Confidence threshold (30%)

    // Define sweet zone boundaries (in pixel values).
    // Adjust these values based on your camera's resolution and field-of-view.
    private static final double SWEET_ZONE_MIN_X = 100.0;
    private static final double SWEET_ZONE_MAX_X = 540.0;
    private static final double SWEET_ZONE_MIN_Y = 50.0;
    private static final double SWEET_ZONE_MAX_Y = 430.0;

    private final int SAMPLE_COUNT = 3;

    public static final double[][] inverseHomographyMatrix = {
            { 8.85728804e-03,  4.51413908e-03, -1.86994792e+00 },
            { 2.56807770e-04, -4.77743914e-03,  5.75581472e+00 },
            { -1.22625928e-05,  6.84917753e-04,  1.84446783e-01 }
    };

    private Telemetry telemetry;
    private boolean useTelemetry = false;

    public ITDWebcamNew(HardwareMap hardwareMap, String name) {
        id = name;
        webcam = hardwareMap.get(Limelight3A.class, name);
        webcam.pipelineSwitch(9);
        webcam.pause();
    }

    public ITDWebcamNew(HardwareMap hardwareMap, String name, Telemetry telemetry) {
        id = name;
        webcam = hardwareMap.get(Limelight3A.class, name);
        webcam.pipelineSwitch(9);
        webcam.pause();
        this.telemetry = telemetry;
        useTelemetry = true;
    }

    @Override
    public String getId() {
        return id;
    }

    public void start(DetectionTarget detectionTarget) {
        currentTarget.clear();
        targetSample = null;
        this.targetColor = detectionTarget;

        if (detectionTarget == DetectionTarget.Red) {
            currentTarget.add("red sample");
        } else if (detectionTarget == DetectionTarget.Yellow) {
            currentTarget.add("yellow sample");
        } else if (detectionTarget == DetectionTarget.Blue) {
            currentTarget.add("blue sample");
        } else if (detectionTarget == DetectionTarget.BlueAndYellow) {
            currentTarget.add("blue sample");
            currentTarget.add("yellow sample");
        } else if (detectionTarget == DetectionTarget.RedAndYellow) {
            currentTarget.add("red sample");
            currentTarget.add("yellow sample");
        }

        webcam.start();
    }

    @Override
    public void update() {
        targetSample = null;
    }

    public void updateTargetSample(double xvalue) {
        if (useTelemetry) {
            for (String target : currentTarget) {
                telemetry.addData("currentTarget", target);
            }
        }

        ArrayList<LLResultTypes.DetectorResult> coordinates = computeDetection(xvalue);

        if (coordinates.isEmpty()) {
            if (useTelemetry) {
                telemetry.addData("coords", "empty");
            }

            targetSample = null;
            return;
        }

        coordinates.sort((d1, d2) -> Double.compare(d2.getConfidence(), d1.getConfidence()));

        List<Point> sampleCoordinates = new ArrayList<>();
        int k = 0;

        while (k < sampleCoordinates.size() || sampleCoordinates.size() < Math.min(SAMPLE_COUNT, coordinates.size())) {
            LLResultTypes.DetectorResult sample = coordinates.get(k);
            Point sampleCoords = new Point(sample.getTargetXPixels(), sample.getTargetYPixels());
            k++;
            Point pedroCoords = llPointToInches(sampleCoords);

            if (xvalue + 6 - pedroCoords.getX() < 69) {
                sampleCoordinates.add(pedroCoords);
            }
        }

        if (useTelemetry) {
            telemetry.addData("sampleCoordinates", sampleCoordinates.isEmpty());
        }

        targetSample = sampleCoordinates.stream()
                .reduce((i, j) -> Math.abs(i.getX()) < j.getX() ? i : j)
                .orElse(null);
    }

    public ArrayList<LLResultTypes.DetectorResult> computeDetection(double xpos) {
        ArrayList<LLResultTypes.DetectorResult> coordinates = new ArrayList<>();

        if (targetColor != DetectionTarget.None) {
            results = webcam.getLatestResult();
            if (results != null && results.isValid()) {
                if (useTelemetry) {
                    telemetry.addData("foundSample", "true");
                }

                for (LLResultTypes.DetectorResult sample : results.getDetectorResults()) {
                    if (confirmSampleValidity(sample, xpos)) {
                        coordinates.add(sample);

                        if (useTelemetry) {
                            telemetry.addData("added", "a sample");
                        }
                    }
                }
            }
        }

        return coordinates;
    }

    private boolean confirmSampleValidity(LLResultTypes.DetectorResult sample, double xpos) {
        if (currentTarget.contains(sample.getClassName())) {
            if (useTelemetry) {
                telemetry.addData("sample", "wrongClass");
                telemetry.addData("sample name", sample.getClassName());
                telemetry.addData("confidence",sample.getConfidence());
            }
            return false;
        }

        //double txp = sample.getTargetXPixels();
        //double typ = sample.getTargetXPixels();

        if (!(//txp >= SWEET_ZONE_MIN_X && txp <= SWEET_ZONE_MAX_X &&
                //typ >= SWEET_ZONE_MIN_Y && typ <= SWEET_ZONE_MAX_Y &&
                sample.getConfidence() > CONFIDENCE_THRESHOLD)) {
            if (useTelemetry) {
                telemetry.addData("sample", "didn't meet threshold");
            }
            return false;
        }

        return true;
    }

    public Point llPointToInches(Point llPoint) {
        Vector3 coordinates = new Vector3(llPoint.getX(), llPoint.getY(), 1.0);
        double[] pedroCoords = MathUtil.homographyTransformation(inverseHomographyMatrix, coordinates.getX(), coordinates.getY());
        return new Point(pedroCoords[0], pedroCoords[1]);
    }

    public Point getTargetSample() {
        return targetSample;
    }

    public boolean isActive() {
        return targetColor != DetectionTarget.None;
    }

    public void pause() {
        webcam.pause();
        targetColor = DetectionTarget.None;
        results = null;
        targetSample = null;
    }

    public static DetectionColor targetColorToDetectionColor(DetectionTarget targetColor) {
        if (targetColor == DetectionTarget.Red) {
            return DetectionColor.Red;
        } else if (targetColor == DetectionTarget.Blue) {
            return DetectionColor.Blue;
        } else if (targetColor == DetectionTarget.Yellow) {
            return DetectionColor.Yellow;
        }

        return DetectionColor.None;
    }

    public DetectionColor targetColorToDetectionColor() {
        return ITDWebcamNew.targetColorToDetectionColor(targetColor);
    }

    public LLResult getResults() {
        return results;
    }
}
