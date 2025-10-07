package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.TurntableV1;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ColorSensor.DecodeColorSensor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

public class TableCompartmentManager {
    public TableCompartmentManager(Consumer<Integer> switchIntakeState, Consumer<Integer> switchPopState, Runnable toggleCycleState) {
        this.switchIntakeState = switchIntakeState;
        this.switchPopState = switchPopState;
        this.toggleCycleState = toggleCycleState;
    }

    public static final class TableCompartment {
        private Vector2D color = new Vector2D(); //ihat purple, jhat green
        private final int index;

        public TableCompartment(int index) {
            this.index = index;
        }

        public Vector2D getColor() {
            return color;
        }

        public void setColor(Vector2D color) {
            this.color = color;
        }

        public void setColor(DecodeColorSensor.ArtifactColor color) {
            if (color == DecodeColorSensor.ArtifactColor.PURPLE) {
                this.color = new Vector2D(1, 0);
            } else if (color == DecodeColorSensor.ArtifactColor.GREEN) {
                this.color = new Vector2D(0, 1);
            } else {
                this.color = new Vector2D(0, 0);
            }
        }

        public void releaseBall() {
            color = new Vector2D();
        }

        public static Comparator<TableCompartment> getComparator(DecodeColorSensor.ArtifactColor color) {
            if (color == DecodeColorSensor.ArtifactColor.PURPLE) {
                return Comparator.comparingDouble(c -> c.color.getX());
            } else if (color == DecodeColorSensor.ArtifactColor.GREEN) {
                return Comparator.comparingDouble(c -> c.color.getY());
            } else {
                return (a, b) -> 0; // No specific order
            }
        }

        public boolean isGreen() {
            return color.getY() >= 0.95;
        }

        public boolean isPurple() {
            return color.getX() >= 0.95;
        }

        public boolean isNone() {
            return color.magnitude() < 0.1;
        }

        public boolean isUndetermined() {
            return !isGreen() && !isPurple() && !isNone();
        }

        public DecodeColorSensor.ArtifactColor getGuessedColor() {
            if (isNone()) return DecodeColorSensor.ArtifactColor.NONE;
            return color.getX() > color.getY() ? DecodeColorSensor.ArtifactColor.PURPLE : DecodeColorSensor.ArtifactColor.GREEN;
        }

        public void update(boolean motifMode, int green, int purple) {
            if (!motifMode) return;

            if (green >= 1) color = new Vector2D(Math.sqrt(0.9), Math.sqrt(0.1));
            else if (purple == 2) color = new Vector2D(Math.sqrt(0.1), Math.sqrt(0.9));
            else if (purple == 1) color = new Vector2D(Math.sqrt(0.5), Math.sqrt(0.5));
        }
    }

    private int currentIndex = 0;
    private DecodeColorSensor.ArtifactColor lastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private DecodeColorSensor.ArtifactColor prevLastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private boolean motifMode = false;
    private final Consumer<Integer> switchIntakeState;
    private final Consumer<Integer> switchPopState;
    private final Runnable toggleCycleState;

    public void setCurrentIndex(int currentIntakeIndex) {
        this.currentIndex = currentIntakeIndex;
    }

    private final TableCompartment[] compartments = {
        new TableCompartment(0),
        new TableCompartment(1),
        new TableCompartment(2)
    };

    private TableCompartment[] sort(DecodeColorSensor.ArtifactColor color) {
        TableCompartment[] sorted = compartments.clone();
        java.util.Arrays.sort(sorted, TableCompartment.getComparator(color));
        return sorted;
    }

    public TableCompartment getCompartment(int index) {
        if (index < 0 || index >= compartments.length) {
            throw new IndexOutOfBoundsException("Invalid compartment index: " + index);
        }
        return compartments[index];
    }

    public int[] getGreenBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].isGreen())
            .toArray();
    }

    public int[] getPurpleBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].isPurple())
            .toArray();
    }

    public int[] getEmptyIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
                .filter(i -> compartments[i].isNone())
                .toArray();
    }

    public void pop() {
        if (!compartments[currentIndex].isNone()) {
            prevLastPoppedColor = lastPoppedColor;
            lastPoppedColor = compartments[currentIndex].getGuessedColor();
            compartments[currentIndex].releaseBall();

            int nextPopIndex = motifMode && Globals.randomizationState != null ? getNextMotifPopIndex() : findNextPopIndex();

            if (nextPopIndex != currentIndex) {
                advancePopIndex(nextPopIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public void intake(DecodeColorSensor.ArtifactColor color) {
        if (color == DecodeColorSensor.ArtifactColor.NONE) return;
        if (compartments[currentIndex].isNone()) {
            compartments[currentIndex].setColor(color);

            int nextIntakeIndex = findNextIntakeIndex();
            if (nextIntakeIndex != currentIndex) {
                advanceIntakeIndex(nextIntakeIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public void manualTransfer() {
        if (compartments[currentIndex].isNone()) {
            compartments[currentIndex].setColor(new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3)));

            int nextIntakeIndex = findNextIntakeIndex();
            if (nextIntakeIndex != currentIndex) {
                advanceIntakeIndex(nextIntakeIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public int findNextIntakeIndex() {
        if (compartments[currentIndex].isNone()) return currentIndex;

        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentIndex + i) % compartments.length;
            if (compartments[nextIndex].isNone()) {
                return nextIndex;
            }
        }

        return currentIndex; // All compartments are full
    }

    public int findNextPopIndex() {
        if (!compartments[currentIndex].isNone()) return currentIndex;

        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentIndex + i) % compartments.length;
            if (!compartments[nextIndex].isNone()) {
                return nextIndex;
            }
        }

        return currentIndex; // All compartments are empty
    }

    public void advanceIntakeIndex(int newIndex) {
        currentIndex = newIndex % compartments.length;
        switchIntakeState.accept(currentIndex);
    }

    public void advancePopIndex(int newIndex) {
        currentIndex = (newIndex) % compartments.length;
        switchPopState.accept(currentIndex);
    }

    public void prepareGreenForPop() {
        int index = nextGreen();
        if (index != currentIndex) advancePopIndex(index);
    }

    public void preparePurpleForPop() {
        int index = nextPurple();
        if (index != currentIndex) advancePopIndex(index);
    }

    private int nextPurple() {
        if (compartments[currentIndex].isPurple()) return currentIndex;

        TableCompartment[] sorted = sort(DecodeColorSensor.ArtifactColor.PURPLE);
        if (sorted[0].isNone() || sorted[0].isUndetermined()) return currentIndex;

        return sorted[0].index; // No purple ball found
    }

    private int nextGreen() {
        if (compartments[currentIndex].isGreen()) return currentIndex;

        TableCompartment[] sorted = sort(DecodeColorSensor.ArtifactColor.GREEN);
        if (sorted[0].isNone() || sorted[0].isUndetermined()) return currentIndex;

        return sorted[0].index; // No green ball found
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

    public void update() {
        int greenCount = 0;
        int purpleCount = 0;

        for (TableCompartment compartment : compartments) {
            if (compartment.isGreen()) greenCount++;
            else if (compartment.isPurple()) purpleCount++;
        }

        for (TableCompartment compartment : compartments) {
            compartment.update(motifMode, greenCount, purpleCount);
        }
    }
}