package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Config
public class TableCompartmentManager {
    public static int defaultRandomizationStateGreenIndex = 1;
    private final ColorManager colorManager;
    private final IntakeUltrasonic ultrasonicSensor;
    private int currentIndex = 0;
    private DecodeColorSensor.ArtifactColor lastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private DecodeColorSensor.ArtifactColor prevLastPoppedColor = DecodeColorSensor.ArtifactColor.NONE;
    private boolean motifMode = false;
    private final Consumer<Integer> switchPopState;
    private final BiConsumer<Integer, TableCompartment[]> uponIntake;
    private final Runnable toggleCycleState;
    private final IntakeThread intakeThread;

    public TableCompartmentManager(ColorManager colorManager, IntakeUltrasonic ultrasonicSensor, BiConsumer<Integer, TableCompartment[]> uponIntake, Consumer<Integer> switchPopState, Runnable toggleCycleState) {
        this.colorManager = colorManager;
        this.ultrasonicSensor = ultrasonicSensor;
        this.switchPopState = switchPopState;
        this.uponIntake = uponIntake;
        this.toggleCycleState = toggleCycleState;
        this.intakeThread = new IntakeThread(colorManager, ultrasonicSensor, compartments);
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
            if (!isUndetermined()) return;

            if (green >= 1) color = new Vector2D(Math.sqrt(0.9), Math.sqrt(0.1));
            else if (purple == 2) color = new Vector2D(Math.sqrt(0.1), Math.sqrt(0.9));
            else if (purple == 1) color = new Vector2D(Math.sqrt(0.5), Math.sqrt(0.5));
        }

        public static Vector2D greenAmplitudes() {
            return new Vector2D(0, 1);
        }

        public static Vector2D purpleAmplitudes() {
            return new Vector2D(1, 0);
        }
    }

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
        java.util.Arrays.sort(sorted, TableCompartment.getComparator(color).reversed());

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

            int nextPopIndex = motifMode ? getNextMotifPopIndex() : findNextPopIndex();

            if (nextPopIndex != currentIndex) {
                advancePopIndex(nextPopIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public void intake() {
        intakeThread.run();

        if (getEmptyIndices().length == 3)
            toggleCycleState.run();
    }

    public void manualTransfer() {
        uponIntake.accept(currentIndex, compartments);
        if (switchToPop()) toggleCycleState.run();
    }

    public void decrementPopIndex() {
        advancePopIndex((currentIndex + 2) % compartments.length);
    }

    public void incrementPopIndex() {
        advancePopIndex((currentIndex + 1) % compartments.length);
    }

    public boolean switchToPop() {
        return getEmptyIndices().length == 0;
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
        if (sorted[0].isNone()) return currentIndex;
        if (sorted[0].color.getX() < compartments[currentIndex].color.getX() + 0.03) return currentIndex;

        return sorted[0].index; // No purple ball found
    }

    private int nextGreen() {
        if (compartments[currentIndex].isGreen()) return currentIndex;

        TableCompartment[] sorted = sort(DecodeColorSensor.ArtifactColor.GREEN);
        if (sorted[0].isNone()) return currentIndex;
        if (sorted[0].color.getY() < compartments[currentIndex].color.getY() + 0.03) return currentIndex;

        return sorted[0].index; // No green ball found
    }

    public DecodeColorSensor.ArtifactColor getNextPopColor() {
        List<DecodeColorSensor.ArtifactColor> targetPopColors = Globals.randomizationState != null? Globals.randomizationState.getColors()
                : getDefaultState().getColors();

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

        if (targetColor == DecodeColorSensor.ArtifactColor.GREEN) {
            int index = nextGreen();
            if (index != currentIndex) return index;
            return nextPurple();
        }

        else {
            int index = nextPurple();
            if (index != currentIndex) return index;
            return nextGreen();
        }
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

    public TableCompartment currentCompartment() {
        return compartments[currentIndex];
    }

    private static Globals.RandomizationState getDefaultState() {
        if (defaultRandomizationStateGreenIndex == 1) return Globals.RandomizationState.GPP;
        else if (defaultRandomizationStateGreenIndex == 2) return Globals.RandomizationState.PGP;
        return Globals.RandomizationState.PPG;
    }

    public void populateAndShoot() {
        for (TableCompartment compartment : compartments) {
            if (compartment.isNone()) compartment.setColor(new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3)));
        }
        toggleCycleState.run();
    }

    public void fillWithPurple() {
        for (TableCompartment compartment : compartments) {
            compartment.setColor(DecodeColorSensor.ArtifactColor.PURPLE);
        }
    }
}