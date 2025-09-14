package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Artifact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ArtifactQueue {
    private final List<Artifact> artifacts = new ArrayList<>();

    private enum CurrentPriority {
        INDEX,
        PURPLE_FIRST,
        GREEN_FIRST;

        public Color.ArtifactColor getColor() {
            if (this == INDEX) return Color.ArtifactColor.NONE;
            if (this == PURPLE_FIRST) return Color.ArtifactColor.PURPLE;
            if (this == GREEN_FIRST) return Color.ArtifactColor.GREEN;
            return Color.ArtifactColor.NONE;
        }
    }
    private CurrentPriority currentPriority = CurrentPriority.INDEX;

    public void preparePurple() {
        currentPriority = CurrentPriority.PURPLE_FIRST;
        artifacts.sort(comparator());
    }

    public void prepareGreen() {
        currentPriority = CurrentPriority.GREEN_FIRST;
        artifacts.sort(comparator());
    }

    public void reset() {
        currentPriority = CurrentPriority.INDEX;
        artifacts.sort(comparator());
    }

    public void pop() {
        artifacts.remove(artifacts.size() - 1);
    }

    public void intake(Color.ArtifactColor color) {
        artifacts.add(new Artifact(color, artifacts.size() + 1));
        if (currentPriority != CurrentPriority.INDEX) artifacts.sort(comparator());
    }

    private Comparator<Artifact> comparator() {
        return Artifact.getComparator(currentPriority.getColor());
    }
}
