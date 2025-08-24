package revAmped.components.MathUtil;

import java.util.function.BooleanSupplier;

public class MathUtil {
    /**
     * Normalizes an angle in radians to the range [0, 2π).
     *
     * @param angleRadians The angle in radians to normalize.
     * @return The normalized angle in radians, in the range [0, 2π).
     */
    public static double normalizeAnglePositive(double angleRadians) {
        double angle = angleRadians % (2*Math.PI);
        if (angle < 0) {
            return angle + 2*Math.PI;
        }

        return angle;
    }

    /**
     * Normalizes an angle in radians to the range [-π, π).
     *
     * @param angleRadians The angle in radians to normalize.
     * @return The normalized angle in radians, in the range [-π, π).
     */
    public static double normalizeAngleHalfPi(double angleRadians) {
        return Math.atan2(Math.sin(angleRadians), Math.cos(angleRadians));
    }

    /**
     * Multiplies a 3x3 matrix with a 3x1 point (in homogeneous coordinates).
     * The input point is (u, v) with an implicit 1.
     *
     * @param matrix A 3x3 matrix.
     * @param u      The x-coordinate in pixels.
     * @param v      The y-coordinate in pixels.
     * @return A double array where index 0 is the ground x-coordinate (in inches)
     *         and index 1 is the ground y-coordinate (in inches).
     */
    public static double[] homographyTransformation(double[][] matrix, double u, double v) {
        double x = matrix[0][0] * u + matrix[0][1] * v + matrix[0][2];
        double y = matrix[1][0] * u + matrix[1][1] * v + matrix[1][2];
        double w = matrix[2][0] * u + matrix[2][1] * v + matrix[2][2];
        return new double[]{ x / w, y / w };
    }

    /**
     * Returns the current time in milliseconds since the epoch.
     * @return The current time in milliseconds.
     */
    public static long millisTime() {
        return System.nanoTime() / (long) Math.pow(10, 6);
    }

    /**
     * Returns the current wall time in milliseconds since the epoch.
     * This is equivalent to System.currentTimeMillis().
     * @return The current wall time in milliseconds.
     */
    public static long wallTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Calculates the factorial of a non-negative integer n.
     * @param n The non-negative integer for which to calculate the factorial.
     * @return The factorial of n.
     */
    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    /**
     * Converts a boolean value to an integer.
     * @param input The boolean value to convert.
     * @return 1 if input is true, 0 if input is false.
     */
    public static int booleanToBinaryInt(boolean input) {
        if (input) return 1;
        else return 0;
    }

    /**
     * Creates a BooleanSupplier that returns true after a specified delay in milliseconds.
     * @param millis The delay in milliseconds.
     * @return A BooleanSupplier that returns true after the specified delay.
     */
    public static BooleanSupplier boleanSupplierfromTimeDelay(double millis) {
        long startTime = millisTime();
        return () -> (millisTime() - startTime) >= millis;
    }

    /**
     * Applies the sigmoid function to a given input.
     * The sigmoid function is defined as 1 / (1 + exp(-input)).
     *
     * @param input The input value to apply the sigmoid function to.
     * @return The result of the sigmoid function applied to the input.
     */
    public static double sigmoid(double input) {
        return 1.0/(1.0 + Math.exp(-input));
    }
}
