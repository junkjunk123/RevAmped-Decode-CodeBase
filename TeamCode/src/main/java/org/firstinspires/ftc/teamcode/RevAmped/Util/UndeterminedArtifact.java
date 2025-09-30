package org.firstinspires.ftc.teamcode.RevAmped.Util;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;

import java.util.List;

import revAmped.components.MathUtil.Vector2D;

public class UndeterminedArtifact extends Artifact {
    private Vector2D probabilityAmplitudes = new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3));

    public UndeterminedArtifact(int index) {
        super(DecodeColorSensor.ArtifactColor.NONE, index);
    }

    @Override
    public Vector2D getProbabilityAmplitudes() {
        return probabilityAmplitudes;
    }

    public void setProbabilityAmplitudes(Vector2D probabilityAmplitudes) {
        this.probabilityAmplitudes = probabilityAmplitudes;
    }

    @Override
    public void update(List<Artifact> artifacts, int green, int purple, boolean motifMode) {
        if (!motifMode) return;

        if (green >= 1) setProbabilityAmplitudes(new Vector2D(Math.sqrt(0.9), Math.sqrt(0.1)));
        else if (purple == 2) setProbabilityAmplitudes(new Vector2D(Math.sqrt(0.1), Math.sqrt(0.9)));
        else if (purple == 1) setProbabilityAmplitudes(new Vector2D(Math.sqrt(0.5), Math.sqrt(0.5)));
    }
}
