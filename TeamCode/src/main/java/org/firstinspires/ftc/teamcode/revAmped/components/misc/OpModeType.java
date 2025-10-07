package org.firstinspires.ftc.teamcode.revAmped.components.misc;

/**
 * OpModeType interface defines the type of operation mode for a robot.
 * It provides methods to check if the operation mode is TeleOp, Auto, or Test.
 */
public interface OpModeType {
    /**
     * Checks if the operation mode is TeleOp (Teleoperated).
     *
     * @return true if the operation mode is TeleOp, false otherwise.
     */
    boolean isTeleOp();

    /**
     * Checks if the operation mode is Auto (Autonomous).
     *
     * @return true if the operation mode is Auto, false otherwise.
     */
    boolean isAuto();

    /**
     * Checks if the operation mode is Test.
     *
     * @return true if the operation mode is Test, false otherwise.
     */
    boolean isTest();
}
