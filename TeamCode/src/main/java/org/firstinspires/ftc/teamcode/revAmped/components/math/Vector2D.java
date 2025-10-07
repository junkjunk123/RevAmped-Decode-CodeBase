package org.firstinspires.ftc.teamcode.revAmped.components.math;

/**
 * A class representing a 2D vector with various operations.
 */
public class Vector2D extends Vector {
    /**
     * Constructs a Vector2D with the specified x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Vector2D(double x, double y) {
        super(x, y);
    }

    public Vector2D() {
        this(0, 0);
    }

    /**
     * Rotates the vector by an angle
     * @param angle the angle in degrees to rotate the vector
     * @return a new Vector2D that is the rotated version of this vector
     */
    public Vector2D rotate(double angle) {
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x = get(0) * cos - get(1) * sin;
        double y = get(0) * sin + get(1) * cos;
        return new Vector2D(x, y);
    }

    /**
     * Returns the angle between this vector and another vector
     * @param other the other vector to calculate the angle with
     * @return the angle in degrees between this vector and the other vector
     */
    public double angle(Vector2D other) {
        double dot = dotProduct(other);
        double magnitude = magnitude() * other.magnitude();
        return Math.toDegrees(Math.acos(dot / magnitude));
    }

    /**
     * Converts the vector to polar coordinates
     * @return an array containing the radius and angle in degrees
     */
    public double[] toPolar() {
        double r = magnitude();
        double theta = Math.toDegrees(Math.atan2(get(1), get(0)));
        return new double[]{r, theta};
    }

    /**
     * Creates a Vector2D from polar coordinates
     * @param r the radius
     * @param theta the angle in degrees
     * @return a new Vector2D representing the point in Cartesian coordinates
     */
    public static Vector2D fromPolar(double r, double theta) {
        double thetaRadians = Math.toRadians(theta); // Convert angle to radians

        double x = r * Math.cos(thetaRadians);
        double y = r * Math.sin(thetaRadians);

        return new Vector2D(x, y);
    }

    /**
     * Returns the angle of the vector in radians
     * @return the angle in radians
     */
    public double getTheta() {
        return Math.atan2(get(1), get(0));
    }

    public double getX() {
        return get(0);
    }

    public double getY() {
        return get(1);
    }
}
