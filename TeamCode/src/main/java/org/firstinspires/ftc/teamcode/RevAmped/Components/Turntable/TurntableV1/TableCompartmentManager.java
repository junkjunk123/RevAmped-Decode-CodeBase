package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;

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
        private DecodeColorSensor.ArtifactColor color;

        public TableCompartment(DecodeColorSensor.ArtifactColor color) {
            this.color = color;
        }

        public DecodeColorSensor.ArtifactColor getColor() {
            return color;
        }

        public void setColor(DecodeColorSensor.ArtifactColor color) {
            this.color = color;
        }

        public void releaseBall() {
            color = DecodeColorSensor.ArtifactColor.NONE;
        }
    }

    private int currentIntakeIndex = 0;
    private int currentPopIndex = 0;
    private DecodeColorSensor.ArtifactColor lastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private DecodeColorSensor.ArtifactColor prevLastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private boolean motifMode = false;
    private final Consumer<Integer> switchIntakeState;
    private final Consumer<Integer> switchPopState;
    private final Runnable toggleCycleState;

    private final TableCompartment[] compartments = {
        new TableCompartment(DecodeColorSensor.ArtifactColor.NONE),
        new TableCompartment(DecodeColorSensor.ArtifactColor.NONE),
        new TableCompartment(DecodeColorSensor.ArtifactColor.NONE)
    };

    public TableCompartment getCompartment(int index) {
        if (index < 0 || index >= compartments.length) {
            throw new IndexOutOfBoundsException("Invalid compartment index: " + index);
        }
        return compartments[index];
    }

    public int[] getGreenBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == DecodeColorSensor.ArtifactColor.GREEN)
            .toArray();
    }

    public int[] getPurpleBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == DecodeColorSensor.ArtifactColor.PURPLE)
            .toArray();
    }

    public int[] getEmptyIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
                .filter(i -> compartments[i].getColor() == DecodeColorSensor.ArtifactColor.NONE)
                .toArray();
    }

    public void pop() {
        if (compartments[currentPopIndex].getColor() != DecodeColorSensor.ArtifactColor.NONE) {
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

    public void intake(DecodeColorSensor.ArtifactColor color) {
        if (color == DecodeColorSensor.ArtifactColor.NONE) return;
        if (compartments[currentIntakeIndex].getColor() == DecodeColorSensor.ArtifactColor.NONE) {
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
            if (compartments[nextIndex].getColor() == DecodeColorSensor.ArtifactColor.NONE) {
                return nextIndex;
            }
        }

        return currentIntakeIndex; // All compartments are full
    }

    public int findNextPopIndex() {
        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentPopIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() != DecodeColorSensor.ArtifactColor.NONE) {
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
            if (compartments[i].getColor() == DecodeColorSensor.ArtifactColor.PURPLE) {
                return i;
            }
        }

        return currentPopIndex; // No purple ball found
    }

    private int nextGreen() {
        for (int i = 0; i < compartments.length; i++) {
            if (compartments[i].getColor() == DecodeColorSensor.ArtifactColor.GREEN) {
                return i;
            }
        }

        return currentPopIndex; // No green ball found
    }

    public DecodeColorSensor.ArtifactColor getNextPopColor() {
        List<DecodeColorSensor.ArtifactColor> targetPopColors = Globals.randomizationState.getColors();

        if (lastPoppedColor == DecodeColorSensor.ArtifactColor.NONE) {
            return targetPopColors.get(0);
        }

        if (prevLastPoppedColor == DecodeColorSensor.ArtifactColor.NONE) {
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
        DecodeColorSensor.ArtifactColor targetColor = getNextPopColor();

        if (targetColor == DecodeColorSensor.ArtifactColor.GREEN) return nextGreen();
        else return nextPurple();
    }

    public void resetPopHistory() {
        lastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
        prevLastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
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