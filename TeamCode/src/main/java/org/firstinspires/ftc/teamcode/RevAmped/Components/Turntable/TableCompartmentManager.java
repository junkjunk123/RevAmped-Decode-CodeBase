package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import java.util.function.Consumer;

public class TableCompartmentManager {
    public TableCompartmentManager(Consumer<Integer> switchIntakeState, Consumer<Integer> switchPopState) {
        this.switchIntakeState = switchIntakeState;
        this.switchPopState = switchPopState;

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
    private boolean motifMode = false;
    private final Consumer<Integer> switchIntakeState;
    private final Consumer<Integer> switchPopState;

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
            compartments[currentPopIndex].releaseBall();
        }
    }

    public void intake(IntakeColor.Color color) {
        if (color == IntakeColor.Color.NEUTRAL) return;
        if (compartments[currentIntakeIndex].getColor() == IntakeColor.Color.NEUTRAL) {
            compartments[currentIntakeIndex].setColor(color);

            if (findNextIntakeIndex() != currentIntakeIndex) {
                advanceIntakeIndex();
            }


        }
    }

    public int findNextIntakeIndex() {
        for (int i = 1; i <= compartments.length; i++) {
            int nextIndex = (currentIntakeIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() == IntakeColor.Color.NEUTRAL) {
                return nextIndex;
            }
        }

        return currentIntakeIndex; // All compartments are full
    }

    public int findNextPopIndex() {
        for (int i = 1; i <= compartments.length; i++) {
            int nextIndex = (currentPopIndex + i) % compartments.length;
            if (compartments[nextIndex].getColor() != IntakeColor.Color.NEUTRAL) {
                return nextIndex;
            }
        }

        return currentPopIndex; // All compartments are empty
    }

    public void advanceIntakeIndex() {
        currentIntakeIndex = (currentIntakeIndex + 1) % compartments.length;
    }

    public void advancePopIndex() {
        currentPopIndex = (currentPopIndex + 1) % compartments.length;
    }
}
