package org.firstinspires.ftc.teamcode.revAmped.components.misc;

import revAmped.components.architecture.AutonomousEnumeratedAction;

/**
 * This class is an abstract base class for autonomous tests in the RevAmped framework.
 * It provides a structure for initializing and looping through an autonomous action.
 */
public abstract class RevAmpedAutoTest extends RevAmpedOpMode {
    public AutonomousEnumeratedAction action;

    @Override
    public void RevAmpedInit() {
        action.update();
    }

    @Override
    public void RevAmpedLoop() {
        action.update();
    }

    /**
     * This method is called to build the autonomous enumerated action.
     * It should be implemented by subclasses to define the specific action.
     * Use the `action` field to access the autonomous action instance.
     */
    public abstract void buildAutonomousEnumeratedAction();
}
