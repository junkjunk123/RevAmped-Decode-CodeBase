package org.firstinspires.ftc.teamcode.revAmped.mechanisms;

import androidx.annotation.NonNull;

import com.pedropathing.math.Vector;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

import java.util.List;

public class DecodeLimelight implements HwDevice {
    private final Limelight3A limelight;
    private final String id;
    private LLResult latestResult;
    private Vector detectionResult;

    public enum Pipeline {
        NONE(-1),
        OBELISK(0),
        SHOOTING_ALIGNMENT(1),
        ELEMENT_DETECTION(2);

        final int pipeline;

        Pipeline(int pipeline) {
            this.pipeline = pipeline;
        }

        public int getPipeline() {
            return pipeline;
        }
    }
    private Pipeline currentPipeline = Pipeline.NONE;

    public DecodeLimelight(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(10);
        this.id = "limelight";
    }

    @Override
    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return id;
    }

    public void computeRandomizationState() {
        setCurrentPipeline(Pipeline.OBELISK);
    }

    public void setCurrentPipeline(Pipeline pipeline) {
        if (pipeline != Pipeline.NONE) limelight.pipelineSwitch(pipeline.getPipeline());
        else limelight.pause();
        currentPipeline = pipeline;
    }

    private void computeLatestResult() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) latestResult = result;
    }

    @Override
    public void update() {
        if (currentPipeline == Pipeline.NONE) return;
        computeLatestResult();
        switch (currentPipeline) {
            case OBELISK -> {
                LLResultTypes.FiducialResult result = latestResult.getFiducialResults().get(0);
                int id = result.getFiducialId();
                for (Globals.RandomizationState state : Globals.RandomizationState.values()) {
                    if (id == state.getID()) {
                        Globals.randomizationState = state;
                        currentPipeline = Pipeline.NONE;
                        return;
                    }
                }
            }
            case SHOOTING_ALIGNMENT -> {
                double tagID = Globals.allianceColor.getTagID();
                List<LLResultTypes.FiducialResult> r = limelight.getLatestResult().getFiducialResults();
                LLResultTypes.FiducialResult target = null;
                for (LLResultTypes.FiducialResult i: r) {
                    if (i != null && i.getFiducialId() == tagID) {
                        target = i;
                        break;
                    }
                }

                if (target != null) {
                    double x = target.getCameraPoseTargetSpace().getPosition().x; // right/left from tag
                    double z = target.getCameraPoseTargetSpace().getPosition().z; // forward/back from tag

                    detectionResult = new Vector();
                    detectionResult.setOrthogonalComponents(z, x);
                    return;
                }

                detectionResult = new Vector();
            }
        }
    }

    public Vector getDetectionResult() {
        return detectionResult;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.CAMERA;
    }
}
