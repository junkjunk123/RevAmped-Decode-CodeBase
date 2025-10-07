package org.firstinspires.ftc.teamcode.revAmped.util;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;

import java.util.Comparator;
import java.util.List;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;

public class Artifact implements Comparable<Artifact> {
    private static Comparator<Artifact> lastComparator;
    public final DecodeColorSensor.ArtifactColor color;
    public final int index;

    public Artifact(DecodeColorSensor.ArtifactColor color, int index) {
        this.color = color;
        this.index = index;
    }

    public static int compare(Artifact a, Artifact b, DecodeColorSensor.ArtifactColor preferredColor) {
        if (preferredColor == DecodeColorSensor.ArtifactColor.NONE) return Integer.compare(a.index, b.index);
        if (a.color == preferredColor) {
            if (b.color == preferredColor) return Integer.compare(a.index, b.index);
            else return 1;
        }

        if (b.color == preferredColor) return 1;
        return Integer.compare(a.index, b.index);
    }

    public Vector2D getProbabilityAmplitudes() {
        if (color == DecodeColorSensor.ArtifactColor.PURPLE) return new Vector2D(1, 0);
        if (color == DecodeColorSensor.ArtifactColor.GREEN) return new Vector2D(0, 1);
        return new Vector2D(1/Math.sqrt(2), 1/Math.sqrt(2));
    }

    public void update(List<Artifact> artifacts, int green, int purple, boolean motifMode) {

    }

    public static Comparator<Artifact> getComparator(DecodeColorSensor.ArtifactColor preferredColor) {
        return lastComparator = (o1, o2) -> compare(o1, o2, preferredColor);
    }

    @Override
    public int compareTo(Artifact o) {
        return lastComparator.compare(this, o);
    }
}
