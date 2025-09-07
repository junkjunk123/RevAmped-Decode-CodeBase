package org.firstinspires.ftc.teamcode.RevAmped.Components;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import revAmped.components.Hardware.HwDevice;
import revAmped.components.Misc.Globals;

public class DecodeLimelight implements HwDevice {
    private final Limelight3A limelight;
    private final String id;
    private LLResult latestResult;

    private enum Pipeline {
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

    public DecodeLimelight(HardwareMap hardwareMap, String name) {
        limelight = hardwareMap.get(Limelight3A.class, name);
        limelight.setPollRateHz(10);
        this.id = name;
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

    private void setCurrentPipeline(Pipeline pipeline) {
        if (pipeline != Pipeline.NONE) limelight.pipelineSwitch(pipeline.getPipeline());
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
                List<LLResultTypes.FiducialResult> result = latestResult.getFiducialResults();
                for (LLResultTypes.FiducialResult tag : result) {
                    if (validateTag(tag)) {
                        int id = tag.getFiducialId();
                        for (Globals.RandomizationState state : Globals.RandomizationState.values()) {
                            if (id == state.getID()) {
                                Globals.randomizationState = state;
                                currentPipeline = Pipeline.NONE;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean validateTag(LLResultTypes.FiducialResult tag) {
        //placeholder
        return true;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.CAMERA;
    }
}
