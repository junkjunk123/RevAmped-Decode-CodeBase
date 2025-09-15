package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;

import java.util.List;
import java.util.function.Consumer;
import revAmped.components.Misc.Globals;

public class TableCompartmentManager {
    public TableCompartmentManager(Consumer<Integer> switchIntakeState, Consumer<Integer> switchPopState, Runnable toggleCycleState) {
        this.switchIntakeState = switchIntakeState;
        this.switchPopState = switchPopState;
        this.toggleCycleState = toggleCycleState;
    }

    public static final class TableCompartment {
        private Color.ArtifactColor color;

        public TableCompartment(Color.ArtifactColor color) {
            this.color = color;
        }

        public Color.ArtifactColor getColor() {
            return color;
        }

        public void setColor(Color.ArtifactColor color) {
            this.color = color;
        }

        public void releaseBall() {
            color = Color.ArtifactColor.NONE;
        }
    }

    private int currentIntakeIndex = 0;
    private int currentPopIndex = 0;
    private Color.ArtifactColor lastPoppedColor = Color.ArtifactColor.NONE;
    private Color.ArtifactColor prevLastPoppedColor = Color.ArtifactColor.NONE;
    private boolean motifMode = false;
    private final Consumer<Integer> switchIntakeState;
    private final Consumer<Integer> switchPopState;
    private final Runnable toggleCycleState;

    private final TableCompartment[] compartments = {
        new TableCompartment(Color.ArtifactColor.NONE),
        new TableCompartment(Color.ArtifactColor.NONE),
        new TableCompartment(Color.ArtifactColor.NONE)
    };

    public TableCompartment getCompartment(int index) {
        if (index < 0 || index >= compartments.length) {
            throw new IndexOutOfBoundsException("Invalid compartment index: " + index);
        }
        return compartments[index];
    }

    public int[] getGreenBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == Color.ArtifactColor.GREEN)
            .toArray();
    }

    public int[] getPurpleBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == Color.ArtifactColor.PURPLE)
            .toArray();
    }

    public int[] getEmptyIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
                .filter(i -> compartments[i].getColor() == Color.ArtifactColor.NONE)
                .toArray();
    }

    public void pop() {
        if (compartments[currentPopIndex].getColor() != Color.ArtifactColor.NONE) {
            prevLastPoppedColor = lastPoppedColor;
            lastPoppedColor = compartments[currentPopIndex].getColor();
            compartments[currentPopIndex].releaseBall();

            int nextPopIndex = motifMode && Globals.randomizationState != null ? getNextMotifPopIndex() : findNextPopIndex();

            if (nextPopIndex != currentPopIndex) {
                advancePopIndex(nextPopIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public void intake(Color.ArtifactColor color) {
        if (color == Color.ArtifactColor.NONE) return;
        if (compartments[currentIntakeIndex].getColor() == Color.ArtifactColor.NONE) {
            compartments[currentIntakeIndex].setColor(color);

            int nextIntakeIndex = findNextIntakeIndex();
            if (nextIntakeIndex != currentIntakeIndex) {
                advanceIntakeIndex(nextIntakeIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public int findNextIntakeIndex() {
        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentIntakeIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() == Color.ArtifactColor.NONE) {
                return nextIndex;
            }
        }

        return currentIntakeIndex; // All compartments are full
    }

    public int findNextPopIndex() {
        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentPopIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() != Color.ArtifactColor.NONE) {
                return nextIndex;
            }
        }

        return currentPopIndex; // All compartments are empty
    }

    public void advanceIntakeIndex(int newIndex) {
        currentIntakeIndex = newIndex % compartments.length;
        switchIntakeState.accept(currentIntakeIndex);
    }

    public void advancePopIndex(int newIndex) {
        currentPopIndex = (newIndex) % compartments.length;
        switchPopState.accept(currentPopIndex);
    }

    public void prepareGreenForPop() {
        int index = nextGreen();
        if (index != currentIntakeIndex) advancePopIndex(index);
    }

    public void preparePurpleForPop() {
        int index = nextPurple();
        if (index != currentIntakeIndex) advancePopIndex(index);
    }

    private int nextPurple() {
        for (int i = 0; i < compartments.length; i++) {
            if (compartments[i].getColor() == Color.ArtifactColor.PURPLE) {
                return i;
            }
        }

        return currentPopIndex; // No purple ball found
    }

    private int nextGreen() {
        for (int i = 0; i < compartments.length; i++) {
            if (compartments[i].getColor() == Color.ArtifactColor.GREEN) {
                return i;
            }
        }

        return currentPopIndex; // No green ball found
    }

    public Color.ArtifactColor getNextPopColor() {
        List<Color.ArtifactColor> targetPopColors = Globals.randomizationState.getColors();

        if (lastPoppedColor == Color.ArtifactColor.NONE) {
            return targetPopColors.get(0);
        }

        if (prevLastPoppedColor == Color.ArtifactColor.NONE) {
            return targetPopColors.get((targetPopColors.indexOf(lastPoppedColor) + 1) % targetPopColors.size());
        }

        int lastIndex = targetPopColors.indexOf(lastPoppedColor);
        int prevIndex = -1;

        for (int i = 0; i < targetPopColors.size(); i++) {
            if (targetPopColors.get(i) == prevLastPoppedColor) {
                if (i != lastIndex) {
                    prevIndex = i;
                    break;
                }
            }
        }

        if (prevIndex == -1) {
            return targetPopColors.get((lastIndex + 1) % targetPopColors.size());
        }

        if (lastIndex + 1 == prevIndex || (lastIndex == 0 && prevIndex == targetPopColors.size() - 1)) {
            return targetPopColors.get((lastIndex + 1) % targetPopColors.size());
        } else {
            return targetPopColors.get((lastIndex - 1 + targetPopColors.size()) % targetPopColors.size());
        }
    }

    public int getNextMotifPopIndex() {
        Color.ArtifactColor targetColor = getNextPopColor();

        if (targetColor == Color.ArtifactColor.GREEN) return nextGreen();
        else return nextPurple();
    }

    public void resetPopHistory() {
        lastPoppedColor = Color.ArtifactColor.NONE;
        prevLastPoppedColor = Color.ArtifactColor.NONE;
    }

    public void setMotifMode(boolean motifMode) {
        this.motifMode = motifMode;
    }

    public boolean isMotifMode() {
        return motifMode;
    }

    public TableCompartment[] getCompartments() {
        return compartments;
    }
}