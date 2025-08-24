package revAmped.components.SlideController.STD;

import com.qualcomm.robotcore.util.ElapsedTime;

import revAmped.util.Range;

/**
 * The STDController class implements a slide controller that uses a custom set of coefficients
 * to control the movement of a slide mechanism in a robot.
 * It calculates the power output based on the current position, target position, and coefficients.
 */
public class STDController {

    /**
     * The target position for the controller.
     * This is the position that the controller will try to reach.
     */
    private int targetPosition = 0;

    /**
     * The coefficients used for the controller.
     */

    /** The coefficients used for the controller, which include parameters for feedforward, derivative, and sigmoid scaling. */
    private CustomSTDCoefficients coefficients;

    /**
     * The previous error value, used for calculating the derivative of the error.
     * This is initialized to 0 and updated during each run.
     */
    private int prevError = 0;

    /**
     * A timer used to measure the elapsed time between runs.
     * This is used to calculate the rate of change of the error.
     */
    private ElapsedTime timer = new ElapsedTime();

    /**
     * The maximum position for the controller.
     * This is used to scale the sigmoid function and ensure that the output remains within a reasonable range.
     */
    private final int maxPos;

    /**
     * Constructs a new STDController with the specified coefficients and maximum position.
     *
     * @param coefficients the coefficients to use for the controller
     * @param maxPos the maximum position for the controller
     */
    public STDController(CustomSTDCoefficients coefficients, int maxPos) {
        this.coefficients = coefficients;
        this.maxPos = maxPos;
    }

    /**
     * Updates the coefficients used by the controller.
     * This allows for dynamic adjustment of the controller's behavior.
     *
     * @param constants the new coefficients to use
     */
    public void updateConstants(CustomSTDCoefficients constants) {
        coefficients = constants;
    }

    /**
     * Runs the slide to a specified position.
     * This method sets the target position and resets the timer and previous error.
     *
     * @param pos the target position to run to
     */
    public void runToPosition(int pos) {
        setTargetPosition(pos);
    }

    /**
     * Sets the target position for the controller.
     * This method resets the timer and previous error when setting a new target position.
     *
     * @param pos the target position to set
     */
    public void setTargetPosition(int pos) {
        timer.reset();
        prevError = 0;
        targetPosition = pos;
    }

    /**
     * Gets the current target position of the controller.
     * @return the target position
     */
    public int getTargetPosition() {
        return targetPosition;
    }

    /**
     * Runs the controller to calculate the power output based on the current position.
     * This method computes the error, derivative of the error, and applies the coefficients to determine the output power.
     *
     * @param currentPos the current position of the slide
     * @return the calculated power output for the slide
     */
    public double run(int currentPos) {
        int error = targetPosition - currentPos;
        double seconds = timer.seconds();
        timer.reset();
        double deltaError = (double) (error - prevError)/seconds;
        prevError = error;
        double correction = deltaError * coefficients.D;
        double sigmoid = Range.scale(1 - (double) Math.abs(error) / maxPos, 0, 1, -6, 6) - coefficients.C;
        sigmoid = 1 / (1 + Math.exp(-sigmoid));
        double power = coefficients.K * Math.sqrt(Math.abs(error * (1 - sigmoid))) * Math.signum(error);
        double feedforward = Math.signum(error) * coefficients.F;
        if (targetPosition == 0) feedforward = 0;
        return power + feedforward + correction;
    }

    /**
     * Gets the previous error value.
     * This is used to retrieve the last calculated error for debugging or further calculations.
     *
     * @return the previous error value
     */
    public double getError() {
        return prevError;
    }
}
