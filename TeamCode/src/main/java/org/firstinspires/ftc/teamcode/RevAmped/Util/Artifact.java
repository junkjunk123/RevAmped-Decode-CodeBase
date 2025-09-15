package org.firstinspires.ftc.teamcode.RevAmped.Util;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;

import java.util.Comparator;

public record Artifact(Color.ArtifactColor color, int index) implements Comparable<Artifact> {
    private static Comparator<Artifact> lastComparator;

    public static int compare(Artifact a, Artifact b, Color.ArtifactColor preferredColor) {
        if (preferredColor == Color.ArtifactColor.NONE) return Integer.compare(a.index, b.index);
        if (a.color == preferredColor) {
            if (b.color == preferredColor) return Integer.compare(a.index, b.index);
            else return 1;
        }

        if (b.color == preferredColor) return 1;
        return Integer.compare(a.index, b.index);
    }

    public static Comparator<Artifact> getComparator(Color.ArtifactColor preferredColor) {
        return (o1, o2) -> compare(o1, o2, preferredColor);
    }

    @Override
    public int compareTo(Artifact o) {
        return lastComparator.compare(this, o);
    }
}
