package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import java.util.ArrayList;
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
        private IntakeColor.Color color;

        public TableCompartment(IntakeColor.Color color) {
            this.color = color;
        }

        public IntakeColor.Color getColor() {
            return color;
        }

        public void setColor(IntakeColor.Color color) {
            this.color = color;
        }

        public void releaseBall() {
            color = IntakeColor.Color.NEUTRAL;
        }
    }

    private int currentIntakeIndex = 0;
    private int currentPopIndex = 0;
    private IntakeColor.Color lastPoppedColor = IntakeColor.Color.NEUTRAL;
    private IntakeColor.Color prevLastPoppedColor = IntakeColor.Color.NEUTRAL;
    private boolean motifMode = false;
    private final Consumer<Integer> switchIntakeState;
    private final Consumer<Integer> switchPopState;
    private final Runnable toggleCycleState;

    private final TableCompartment[] compartments = {
        new TableCompartment(IntakeColor.Color.NEUTRAL),
        new TableCompartment(IntakeColor.Color.NEUTRAL),
        new TableCompartment(IntakeColor.Color.NEUTRAL)
    };

    public TableCompartment getCompartment(int index) {
        if (index < 0 || index >= compartments.length) {
            throw new IndexOutOfBoundsException("Invalid compartment index: " + index);
        }
        return compartments[index];
    }

    public int[] getGreenBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == IntakeColor.Color.GREEN)
            .toArray();
    }

    public int[] getPurpleBallIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
            .filter(i -> compartments[i].getColor() == IntakeColor.Color.PURPLE)
            .toArray();
    }

    public void pop() {
        if (compartments[currentPopIndex].getColor() != IntakeColor.Color.NEUTRAL) {
            prevLastPoppedColor = lastPoppedColor;
            lastPoppedColor = compartments[currentPopIndex].getColor();
            compartments[currentPopIndex].releaseBall();

            int nextPopIndex = motifMode ? getNextMotifPopIndex() : findNextPopIndex();

            if (nextPopIndex != currentPopIndex) {
                advancePopIndex(nextPopIndex);
            } else {
                toggleCycleState.run();
            }
        }
    }

    public void intake(IntakeColor.Color color) {
        if (color == IntakeColor.Color.NEUTRAL) return;
        if (compartments[currentIntakeIndex].getColor() == IntakeColor.Color.NEUTRAL) {
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
            if (compartments[nextIndex].getColor() == IntakeColor.Color.NEUTRAL) {
                return nextIndex;
            }
        }

        return currentIntakeIndex; // All compartments are full
    }

    public int findNextPopIndex() {
        for (int i = 0; i <= compartments.length; i++) {
            int nextIndex = (currentPopIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() != IntakeColor.Color.NEUTRAL) {
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
            if (compartments[i].getColor() == IntakeColor.Color.PURPLE) {
                return i;
            }
        }

        return currentPopIndex; // No purple ball found
    }

    private int nextGreen() {
        for (int i = 0; i < compartments.length; i++) {
            if (compartments[i].getColor() == IntakeColor.Color.GREEN) {
                return i;
            }
        }

        return currentPopIndex; // No green ball found
    }

    public IntakeColor.Color getNextPopColor() {
        List<IntakeColor.Color> targetPopColors = Globals.randomizationState.getColors();

        if (lastPoppedColor == IntakeColor.Color.NEUTRAL) {
            return targetPopColors.get(0);
        }

        if (prevLastPoppedColor == IntakeColor.Color.NEUTRAL) {
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
        IntakeColor.Color targetColor = getNextPopColor();

        if (targetColor == IntakeColor.Color.GREEN) return nextGreen();
        else return nextPurple();
    }

    public void resetPopHistory() {
        lastPoppedColor = IntakeColor.Color.NEUTRAL;
        prevLastPoppedColor = IntakeColor.Color.NEUTRAL;
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