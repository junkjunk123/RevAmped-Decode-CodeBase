package revAmped.components.MathUtil;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * A class representing a mathematical vector with various operations.
 * This class supports basic vector operations such as addition, subtraction,
 * dot product, magnitude, normalization, and transformation using matrices.
 */
public class Vector {
    /**
     * The values of the vector represented as an array of doubles.
     */
    private double[] values;

    /**
     * Constructs a Vector with the specified values.
     *
     * @param values the values of the vector
     */
    public Vector(double... values) {
        this.values = values;
    }

    /**
     * Constructs an empty Vector with no values.
     */
    public Vector() {
        this.values = new double[0];
    }

    /**
     * Returns the dimension of the vector, which is the number of values it contains.
     * @return the dimension of the vector
     */
    public int dim() {
        return values.length;
    }

    /**
     * Returns the value at the specified index in the vector.
     *
     * @param i the index of the value to retrieve
     * @return the value at the specified index
     */
    public double get(int i) {
        return values[i];
    }

    /**
     * Sets the values of the vector to the specified array.
     *
     * @param values the new values for the vector
     */
    public void setValues(double[] values) {
        this.values = values;
    }

    /**
     * Sets the value at the specified index in the vector.
     *
     * @param i the index at which to set the value
     * @param value the value to set at the specified index
     */
    public void set(int i, double value) {
        values[i] = value;
    }

    /**
     * Returns the values of the vector as an array.
     *
     * @return an array containing the values of the vector
     */
    public Vector add(Vector vector) {
        double[] vals = values;
        for (int i = 0; i < values.length; i++) vals[i] += vector.get(i);
        return new Vector(vals);
    }

    /**
     * Subtracts the specified vector from this vector.
     *
     * @param vector the vector to subtract
     * @return a new Vector representing the result of the subtraction
     */
    public Vector subtract(Vector vector) {
        double[] vals = values;
        for (int i = 0; i < values.length; i++) vals[i] -= vector.get(i);
        return new Vector(vals);
    }

    /**
     * Computes the dot product of this vector with another vector.
     *
     * @param vector the vector to compute the dot product with
     * @return the dot product of the two vectors
     */
    public double dotProduct(Vector vector) {
        double[] vals = values;

        for (int i = 0; i < values.length; i++) {
            vals[i] *= vector.get(i);
        }

        return Arrays.stream(vals).sum();
    }

    /**
     * Computes the dot product of this vector with itself.
     *
     * @return the dot product of the vector with itself
     */
    public double magnitude() {
        return Math.sqrt(dotProduct(this));
    }

    /**
     * Computes the squared magnitude of this vector.
     *
     * @return the squared magnitude of the vector
     */
    public double magSquared() {
        return dotProduct(this);
    }

    /**
     * Computes the distance between this vector and another vector.
     *
     * @param vector the vector to compute the distance to
     * @return the distance between the two vectors
     */
    public double distance(Vector vector) {
        return subtract(vector).magnitude();
    }

    /**
     * Computes the squared distance between this vector and another vector.
     *
     * @param vector the vector to compute the squared distance to
     * @return the squared distance between the two vectors
     */
    public double distSquared(Vector vector) {
        return subtract(vector).magSquared();
    }

    /**
     * Computes the angle in radians between this vector and another vector.
     *
     * @param vector the vector to compute the angle with
     * @return the angle in radians between the two vectors
     */
    public double angle(Vector vector) {
        return Math.acos(dotProduct(vector) / (magnitude() * vector.magnitude()));
    }

    /**
     * Normalizes this vector to have a magnitude of 1.
     *
     * @return a new Vector that is the normalized version of this vector
     */
    public Vector normalize() {
        return scalarMult(1 / magnitude());
    }

    /**
     * Computes the cross product of this vector with another vector.
     * This method is only valid for 3-dimensional vectors.
     *
     * @param vector the vector to compute the cross product with
     * @return a new Vector representing the cross product
     * @throws IllegalArgumentException if either vector is not 3-dimensional
     */
    public Vector crossProduct(Vector vector) {
        if (this.dim() != 3 || vector.dim() != 3) {
            throw new IllegalArgumentException("Vector dimensions must be 3 for cross product");
        }

        double x = this.get(1) * vector.get(2) - this.get(2) * vector.get(1);
        double y = this.get(2) * vector.get(0) - this.get(0) * vector.get(2);
        double z = this.get(0) * vector.get(1) - this.get(1) * vector.get(0);

        return new Vector(x, y, z);
    }

    /**
     * Transforms this vector using the specified matrix.
     * The matrix must have the same number of rows as the dimension of the vector.
     *
     * @param matrix the matrix to transform the vector with
     * @return a new Vector representing the transformed vector
     */
    public Vector transform(Matrix matrix) {
        double[] vals = new double[matrix.getRows()];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < matrix.getRows(); j++) {
                vals[j] += values[i] * matrix.get(i, j);
            }
        }

        return new Vector(vals);
    }

    /**
     * Multiplies this vector by a scalar value.
     *
     * @param scalar the scalar value to multiply the vector by
     * @return a new Vector representing the result of the scalar multiplication
     */
    public Vector scalarMult(double scalar) {
        double[] vals = values;
        for (int i = 0; i < values.length; i++) vals[i] *= scalar;
        return new Vector(vals);
    }

    /**
     * Returns the values of the vector as an array.
     *
     * @return an array containing the values of the vector
     */
    public double[] values() {
        return values;
    }

    /**
     * Returns a string representation of the vector.
     * The string contains the values of the vector in an array format.
     *
     * @return a string representation of the vector
     */
    @NonNull
    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    /**
     * Creates a clone of this vector.
     * @return a new Vector instance with the same values
     */
    @Override
    public Vector clone() {
        return new Vector(values);
    }
}
