package org.firstinspires.ftc.teamcode.revAmped.components.math;

/**
 * A class representing a finite dimension vector in a finite field.
 * @param <T> the type of parameters that define the finite field and vector dimension
 */
public class FieldVector<T extends FieldVector.Parameters> extends Vector {
    /**
     * The parameters defining the finite field and vector dimension.
     */
    private final T parameters;

    /**
     * An interface for parameters that define the finite field and vector dimension.
     */
    public interface Parameters {
        int getFiniteField();
        int getDimension();
    }

    /**
     * Constructs a FieldVector with the specified parameters and values.
     *
     * @param parameters the parameters defining the finite field and vector dimension
     * @param values the values of the vector
     * @throws IllegalArgumentException if the number of values does not match the dimension defined by parameters
     */
    public FieldVector(T parameters, double... values) {
        super(values);
        if (values.length != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimension does not match field parameters");
        }
        this.parameters = parameters;
        applyFiniteField();
    }

    /**
     * Applies the finite field operation to each value in the vector.
     */
    private void applyFiniteField() {
        for (int i = 0; i < dim(); i++) {
            set(i, mod(get(i), parameters.getFiniteField()));
        }
    }

    private double mod(double value, int field) {
        return ((value % field) + field) % field;
    }

    /**
     * Sets the values of the vector to the specified array, applying the finite field operation.
     * @param values the new values for the vector
     */
    @Override
    public void setValues(double[] values) {
        if (values.length != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimension does not match field parameters");
        }
        super.setValues(values);
        applyFiniteField();
    }

    /**
     * Sets the value at the specified index in the vector, applying the finite field operation.
     * @param i the index at which to set the value
     * @param value the value to set at the specified index
     */
    @Override
    public void set(int i, double value) {
        super.set(i, mod(value, parameters.getFiniteField()));
    }

    @Override
    public FieldVector<T> add(Vector vector) {
        if (vector.dim() != dim()) {
            throw new IllegalArgumentException("Vector dimensions must match for addition");
        }
        FieldVector<T> result = new FieldVector<>(parameters, super.add(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FieldVector<T> subtract(Vector vector) {
        if (vector.dim() != dim()) {
            throw new IllegalArgumentException("Vector dimensions must match for subtraction");
        }
        FieldVector<T> result = new FieldVector<>(parameters, super.subtract(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public double dotProduct(Vector vector) {
        if (vector.dim() != dim()) {
            throw new IllegalArgumentException("Vector dimensions must match for dot product");
        }
        return mod(super.dotProduct(vector), parameters.getFiniteField());
    }

    @Override
    public FieldVector<T> scalarMult(double scalar) {
        FieldVector<T> result = new FieldVector<>(parameters, super.scalarMult(mod(scalar, parameters.getFiniteField())).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FieldVector<T> crossProduct(Vector vector) {
        if (dim() != 3 || vector.dim() != 3) {
            throw new IllegalArgumentException("Vector dimensions must be 3 for cross product");
        }
        FieldVector<T> result = new FieldVector<>(parameters, super.crossProduct(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FieldVector<T> transform(Matrix matrix) {
        FieldVector<T> result = new FieldVector<>(parameters, super.transform(matrix).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FieldVector<T> normalize() {
        throw new UnsupportedOperationException("Normalization is not supported in finite fields");
    }

    @Override
    public FieldVector<T> clone() {
        return new FieldVector<>(parameters, values());
    }

    @Override
    public String toString() {
        return "FieldVector{" +
                "values=" + super.toString() +
                ", finiteField=" + parameters.getFiniteField() +
                '}';
    }
}
