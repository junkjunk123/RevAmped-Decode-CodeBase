package org.firstinspires.ftc.teamcode.revAmped.components.controller;

public interface SlideController {
    /**
     * Updates the constants used by the slide controller.
     * @param constants the new constants to use
     */
    void updateConstants(SlideConstants constants);

    /**
     * Runs the slide to a specified position.
     * @param pos the target position to run to
     */
    void runToPosition(int pos);

    /**
     * Runs the slide to a specified position with a parameter.
     * @param pos the target position to run to
     * @param parameter an additional parameter for the run
     */
    default void runToPosition(int pos, double parameter) {runToPosition(pos);}

    /**
     * Gets the current target position of the slide.
     * @return the target position
     */
    default void setHomed() {}

    default int getTargetPosition() {return 0;}
    default void setTargetPosition(int pos) {runToPosition(pos);}

    interface SlideConstants {

    }
}
