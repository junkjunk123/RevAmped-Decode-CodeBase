package org.firstinspires.ftc.teamcode.revAmped.components.architecture;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AutonomousActionExecutor is an abstract class that manages a list of autonomous actions
 */
public abstract class AutonomousActionExecutor {
    /**
     * ElapsedTime objects to track the overall time and preload drive time.
     */
    public ElapsedTime overallTimer;
    public ElapsedTime preloadDriveTimer;

    /**
     * List of AutonomousEnumeratedAction objects representing the actions to be executed.
     */
    protected List<AutonomousEnumeratedAction> actions;

    /**
     * List of identifiers for the actions, used to track and manage the actions.
     */
    private final ArrayList<String> identifiers;

    /**
     * The current state of the autonomous actions, represented as an index in the actions list.
     */
    private int autoState;

    /**
     * A boolean indicating whether the robot is currently driving.
     */
    public boolean driving;

    /**
     * The size of the actions list, used to determine the number of actions available.
     */
    private int size;

    /**
     * Constructor for AutonomousActionExecutor.
     * Initializes the overall timer, preload drive timer, actions list, identifiers list,
     * size, autoState, and driving state.
     */
    public AutonomousActionExecutor() {
        overallTimer = new ElapsedTime();
        preloadDriveTimer = new ElapsedTime();
        actions = Collections.emptyList();
        identifiers = new ArrayList<>();
        size = 0;
        autoState = 0;
        driving = false;
    }

    /**
     * Sets the autoState to the specified state and marks the robot as driving.
     * @param state the new state to set for autoState
     */
    public void setAutoState(int state) {
        if (state < actions.size()) {
            autoState = state;
            driving = true;
        }
    }

    /**
     * Abstract method to be implemented by subclasses to build the actions.
     * This method should populate the actions list with AutonomousEnumeratedAction objects.
     */
    protected abstract void buildActions();

    /**
     * Builds the actions by calling the buildActions method, sets the size of the actions list,
     * and populates the identifiers list with the identifiers of each action.
     */
    public void build() {
        buildActions();
        size = actions.size();

        for (AutonomousEnumeratedAction action : actions) {
            identifiers.add(action.identifier());
        }
    }

    /**
     * Returns the current list of actions.
     * @return the list of AutonomousEnumeratedAction objects
     */
    public AutonomousEnumeratedAction getAutoStateAction(int autoState) {
        return actions.get(autoState);
    }

    /**
     * Updates the current action based on the autoState.
     * @return true if the action was updated successfully, false otherwise
     */
    public boolean update() {
        return actions.get(autoState).update();
    }

    /**
     * Returns the current autoState.
     * @return the current autoState index
     */
    public int getAutoState() {
        return autoState;
    }

    /**
     * Checks if the current autoState is the final state.
     * @return true if the autoState is the last action, false otherwise
     */
    public boolean finalState() {
        return autoState == size - 1;
    }

    /**
     * Gets the list of identifiers for the actions.
     * @return the list of identifiers as Strings
     */
    public List<String> getIdentifiers() {
        return identifiers;
    }

    /**
     * Returns the first instance of the specified action in the identifiers list.
     * @param action the action index to find in the identifiers list
     * @return the index of the first occurrence of the action in the identifiers list
     */
    public int firstIndex(int action) {
        return identifiers.indexOf(identifiers.get(action));
    }

    /**
     * Returns the first instance of the specified identifier in the identifiers list.
     * @param identifier the identifier to find in the identifiers list
     * @return the index of the first occurrence of the identifier in the identifiers list
     */
    public int firstIndex(String identifier) {
        return identifiers.indexOf(identifier);
    }

    /**
     * Returns the first index of the current autoState's identifier in the identifiers list.
     * @return the index of the first occurrence of the current autoState's identifier
     */
    public int firstIndex() {
        return firstIndex(autoState);
    }

    /**
     * Returns the iteration number of the specified action based on its identifier.
     * @param action the action index to find the iteration number for
     * @return the iteration number of the specified action
     */
    public int iterationNumber(int action) {
        int count = 0;
        String identifier = identifiers.get(action);
        for (int i = 0; i < action; i++) {
            if (identifiers.get(i).equals(identifier)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the iteration number of the specified identifier.
     * @return the iteration number of the specified identifier
     */
    public int currentIterationNumber() {
        return iterationNumber(autoState);
    }

    /**
     * Checks if the current autoState is the final iteration of its identifier.
     * @return true if the current autoState is the last iteration of its identifier, false otherwise
     */
    public boolean finalIteration() {
        String identifier = identifiers.get(autoState);
        for (int i = autoState; i < identifiers.size(); i++) {
            if (identifiers.get(i).equals(identifier)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the next index of the specified identifier in the identifiers list.
     * @param identifier the identifier to find the next index for
     * @return the index of the next occurrence of the identifier in the identifiers list
     * @throws RuntimeException if the identifier is not found after the specified action
     */
    public int nextIndex(String identifier) {
        for (int i = autoState; i < identifiers.size(); i++) {
            if (identifiers.get(i).equals(identifier)) {
                return i;
            }
        }

        throw new RuntimeException("AutonomousActionExecutor.nextIndex(): Identifier not found after specified action");
    }

    /**
     * Returns the next index of the current autoState's identifier in the identifiers list.
     * @return the index of the next occurrence of the current autoState's identifier
     * @throws RuntimeException if the identifier is not found after the current autoState
     */
    public int nextIndex() {
        return nextIndex(identifiers.get(autoState));
    }

    public int getSize() {
        return size;
    }
}
