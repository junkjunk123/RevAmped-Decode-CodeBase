package org.firstinspires.ftc.teamcode.RevAmped.Util;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ArtifactQueue;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;

import java.util.Comparator;

public class Artifact {
    public Color.ArtifactColor color;
    public final int index;

    public Artifact(int index) {
        this.index = index;
    }

    public Artifact setColor(Color.ArtifactColor color) {
        this.color = color;
        return this;
    }

    public Color.ArtifactColor getColor() {
        return color;
    }

    private static int compare(Artifact a, Artifact b, Color.ArtifactColor preferredColor) {
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
}
