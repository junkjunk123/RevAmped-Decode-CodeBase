package revAmped.components.MathUtil;

public class MathUtil {
    public static double normalizeAnglePositive(double angleRadians) {
        double angle = angleRadians % (2*Math.PI);
        if (angle < 0) {
            return angle + 2*Math.PI;
        }

        return angle;
    }

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

    public static long currentTimeMonotonicMillis() {
        return System.nanoTime() / (long) Math.pow(10, 6);
    }

    public static long currentWallTimeMillis() {
        return System.currentTimeMillis();
    }

    public static int fact(int n) {
        if (n <= 1) return 1;
        return n * fact(n - 1);
    }
}
