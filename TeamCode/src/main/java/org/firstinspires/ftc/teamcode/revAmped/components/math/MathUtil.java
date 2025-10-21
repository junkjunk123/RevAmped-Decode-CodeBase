package org.firstinspires.ftc.teamcode.revAmped.components.math;

import com.pedropathing.math.Matrix;

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
    public static BooleanSupplier booleanSupplierfromTimeDelay(double millis) {
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

    /**
     * Create a square matrix that has 1's in the diagonal while 0's everywhere else
     * @param dim row/column count of the new matrix
     * @return the identity matrix of NxN size
     */
    public static com.pedropathing.math.Matrix identity(int dim){
        com.pedropathing.math.Matrix output = new com.pedropathing.math.Matrix(dim, dim);
        for (int i = 0; i < dim; i++) {
            output.set(i, i, 1);
        }
        return output;
    }

    /**
     * Creates a square matrix where all elements are 0
     * @param dim row/column count of the new matrix
     * @return a zero matrix of NxN size
     */
    public static com.pedropathing.math.Matrix zeros(int dim){
        return new com.pedropathing.math.Matrix(dim, dim);
    }

    /**
     * Creates a matrix where all elements are 0's
     * @param rows number of rows
     * @param cols number of columns
     * @return zero matrix of MxN size
     */
    public static com.pedropathing.math.Matrix zeros(int rows, int cols){
        return new com.pedropathing.math.Matrix(rows, cols);
    }

    /**
     * Takes in an N length 1d array and returns a square matrix of NxN size
     * that has the diagonal elements be the passed in array values while the rest
     * of the elements are 0's
     * @param elements 1d double array
     * @return Matrix of NxN size
     */
    public static com.pedropathing.math.Matrix diag(double... elements){
        com.pedropathing.math.Matrix output = new com.pedropathing.math.Matrix(elements.length, elements.length);
        for (int i = 0; i < elements.length; i++) {
            output.set(i, i, elements[i]);
        }
        return output;
    }

    /**
     * Returns an affine translation matrix of 3x3 size
     * @param x x translation
     * @param y y translation
     * @return Matrix of 3x3 size
     */
    public static com.pedropathing.math.Matrix translation(double x, double y){
        return new com.pedropathing.math.Matrix(new double[][]{
                {1, 0, x},
                {0, 1, y},
                {0, 0, 1}
        });
    }

    /**
     * Create a 3x3 matrix with a 2d rotation minor matrix on the top left
     * @param angle radians; + = CCW, - = CW
     * @return 3x3 affine rotation matrix
     */
    public static com.pedropathing.math.Matrix createRotation(double angle){
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        return new com.pedropathing.math.Matrix(new double[][]{
                {cos, -sin, 0.0},
                {sin,  cos, 0.0},
                {0.0,  0.0, 1.0}
        });
    }

    /**
     * Returns an affine transformation of 3x3 matrix. This matrix represents a rotation and then a translation
     * @param x x translation
     * @param y y translation
     * @param angle radians; + = CCW, - = CW
     * @return 3x3 transformation matrix
     */
    public static com.pedropathing.math.Matrix createTransformation(double x, double y, double angle){
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        return new com.pedropathing.math.Matrix(new double[][]{
                {cos, -sin,   x},
                {sin,  cos,   y},
                {0.0,  0.0, 1.0}
        });
    }


    /**
     * Creates a 3x3 diagonal matrix with the given diagonal entries.
     *
     * @param a00 element at (0,0)
     * @param a11 element at (1,1)
     * @param a22 element at (2,2)
     * @return a 3x3 diagonal matrix
     */
    public static com.pedropathing.math.Matrix diagonal3(double a00, double a11, double a22) {
        com.pedropathing.math.Matrix m = new com.pedropathing.math.Matrix(3, 3);
        m.set(0, 0, a00);
        m.set(1, 1, a11);
        m.set(2, 2, a22);
        return m;
    }

    public static com.pedropathing.math.Matrix invert3x3(com.pedropathing.math.Matrix m) {
        double a = m.get(0,0), b = m.get(0,1), c = m.get(0,2);
        double d = m.get(1,0), e = m.get(1,1), f = m.get(1,2);
        double g = m.get(2,0), h = m.get(2,1), i = m.get(2,2);

        double det = a*(e*i - f*h) - b*(d*i - f*g) + c*(d*h - e*g);
        if (Math.abs(det) < 1e-9) throw new RuntimeException("Singular matrix");

        com.pedropathing.math.Matrix inv = new Matrix(3,3);
        inv.set(0,0, (e*i - f*h)/det);
        inv.set(0,1, (c*h - b*i)/det);
        inv.set(0,2, (b*f - c*e)/det);
        inv.set(1,0, (f*g - d*i)/det);
        inv.set(1,1, (a*i - c*g)/det);
        inv.set(1,2, (c*d - a*f)/det);
        inv.set(2,0, (d*h - e*g)/det);
        inv.set(2,1, (b*g - a*h)/det);
        inv.set(2,2, (a*e - b*d)/det);
        return inv;
    }
}
