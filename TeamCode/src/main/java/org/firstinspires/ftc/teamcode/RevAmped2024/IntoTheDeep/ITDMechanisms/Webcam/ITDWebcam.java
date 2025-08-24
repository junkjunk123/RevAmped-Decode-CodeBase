package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Webcam;

import com.pedropathing.pathgen.Vector;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.Hardware.HwDevice;

public class ITDWebcam implements HwDevice {
    public final Limelight3A webcam;
    private String id;

    public enum DetectionTarget {
        Blue,
        Red,
        Yellow,
        BlueAndYellow,
        RedAndYellow,
        NONE
    }

    private DetectionTarget targetColor;
    private Vector detectionVector;
    private LLResult results;

    public ITDWebcam(HardwareMap hardwareMap, String name) {
        id = name;
        webcam = hardwareMap.get(Limelight3A.class, name);
        webcam.setPollRateHz(100);
        webcam.pause();
        results = null;
        detectionVector = new Vector();
    }

    private void detectBlueSamples() {
        webcam.pipelineSwitch(0);
        targetColor = DetectionTarget.Blue;
        webcam.start();
    }

    private void detectRedSamples() {
        webcam.pipelineSwitch(1);
        targetColor = DetectionTarget.Red;
        webcam.start();
    }

    private void detectYellowSamples() {
        webcam.pipelineSwitch(2);
        targetColor = DetectionTarget.Yellow;
        webcam.start();
    }

    private void detectBlueAndYellowSamples() {
        webcam.pipelineSwitch(4);
        targetColor = DetectionTarget.BlueAndYellow;
        webcam.start();
    }

    private void detectRedAndYellowSamples() {
        webcam.pipelineSwitch(3);
        targetColor = DetectionTarget.RedAndYellow;
        webcam.start();
    }

    public DetectionTarget getTargetColor() {
        return targetColor;
    }

    public void pause() {
        webcam.pause();
        targetColor = DetectionTarget.NONE;
        detectionVector = new Vector();
    }

    @Override
    public String getId() {
        return id;
    }

    public void update() {
        update(false, false);
    }

    public void update(boolean avoidOtherAllianceCrossover, boolean left) {
        if (targetColor != DetectionTarget.NONE) {
            results = webcam.getLatestResult();

            if (results != null && results.isValid()) {
                Vector targetVector = new Vector(0, Math.PI);

                for (LLResultTypes.ColorResult cr : results.getColorResults()) {
                    Vector detectionVector = vectorToTarget(cr);

                    if (Math.abs(angleNormalizer(detectionVector.getTheta())) < Math.abs(angleNormalizer(targetVector.getTheta())) && detectionVector.getMagnitude() > 7 && detectionVector.getMagnitude() * Math.cos(getNormalizedAngle(detectionVector)) < 24) {
                        if (avoidOtherAllianceCrossover) {
                            if ((getNormalizedAngle(detectionVector) <= 0 && left) || (getNormalizedAngle(detectionVector) >= 0 && !left)) {
                                targetVector = detectionVector;
                            }
                        } else {
                            targetVector = detectionVector;
                        }
                    }
                }

                detectionVector = targetVector;
            }
        }
    }

    private double angleNormalizer(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    public boolean turnNeeded(Vector targetVector) {
        return !(targetVector.getMagnitude() * Math.abs(Math.sin(targetVector.getTheta())) < 4.88f);
    }

    public LLResult getLatestResults() {
        return results;
    }

    public Vector vectorToTarget(LLResultTypes.ColorResult colorResult) {
        if (colorResult != null) {
            double angleX = Math.toRadians(colorResult.getTargetXDegrees());
            double angleY = Math.toRadians(colorResult.getTargetYDegrees());
            double distance = (ITDConstants.HEIGHT_OF_SAMPLE / 2 - ITDConstants.HEIGHT_ABOVE_GROUND) / Math.tan((angleY - Math.toRadians(Math.abs(ITDConstants.MOUNT_ANGLE))));

            return new Vector(distance, angleX);
        } else {
            return new Vector();
        }
    }

    public void start(DetectionTarget detectionTarget) {
        if (detectionTarget == DetectionTarget.Blue) {
            detectBlueSamples();
        } else if (detectionTarget == DetectionTarget.Red) {
            detectRedSamples();
        } else if (detectionTarget == DetectionTarget.NONE) {
            pause();
        } else if (detectionTarget == DetectionTarget.BlueAndYellow) {
            detectBlueAndYellowSamples();
        } else if (detectionTarget == DetectionTarget.RedAndYellow) {
            detectRedAndYellowSamples();
        } else if (detectionTarget == DetectionTarget.Yellow) {
            detectYellowSamples();
        }
    }

    public boolean periodCompleted() {
        return detectionVector.getMagnitude() > 5;
    }

    public Vector getDetectionVector() {
        return detectionVector;
    }

    public double getNormalizedVectorAngle() {
        return getNormalizedAngle(getDetectionVector());
    }

    private double getNormalizedAngle(Vector vector) {
        return Math.atan2(Math.sin(vector.getTheta()), Math.cos(vector.getTheta()));
    }
}
