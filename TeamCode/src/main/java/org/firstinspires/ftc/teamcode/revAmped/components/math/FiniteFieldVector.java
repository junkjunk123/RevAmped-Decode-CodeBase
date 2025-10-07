package org.firstinspires.ftc.teamcode.revAmped.components.math;

/**
 * A class representing a finite dimension vector in a finite field.
 * @param <T> the type of parameters that define the finite field
 */
public class FiniteFieldVector<T extends FiniteFieldVector.Parameters> extends Vector {
    /**
     * The parameters defining the finite field.
     */
    private final T parameters;

    /**
     * An interface for parameters that define the finite field.
     */
    public interface Parameters {
        int getFiniteField();
    }

    /**
     * Constructs a FiniteFieldVector with the specified parameters and values.
     *
     * @param parameters the parameters defining the finite field
     * @param values the values of the vector
     * @throws IllegalArgumentException if the number of values does not match the dimension defined by parameters
     */
    public FiniteFieldVector(T parameters, double... values) {
        super(values);
        this.parameters = parameters;
        applyFiniteField();
    }

    /**
     * Applies the finite field operation to each value in the vector.
     */
    private void applyFiniteField() {
        for (int i = 0; i < values().length; i++) {
            values()[i] = mod(values()[i], parameters.getFiniteField());
        }
    }

    private double mod(double value, int field) {
        return ((value % field) + field) % field;
    }

    /**
     * Returns the parameters defining the finite field.
     * @return the parameters of the finite field
     */
    public int getFiniteField() {
        return parameters.getFiniteField();
    }

    @Override
    public void setValues(double[] values) {
        super.setValues(values);
        applyFiniteField();
    }

    @Override
    public void set(int i, double value) {
        super.set(i, mod(value, parameters.getFiniteField()));
    }

    @Override
    public FiniteFieldVector<T> add(Vector vector) {
        FiniteFieldVector<T> result = new FiniteFieldVector<>(parameters, super.add(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FiniteFieldVector<T> subtract(Vector vector) {
        FiniteFieldVector<T> result = new FiniteFieldVector<>(parameters, super.subtract(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public double dotProduct(Vector vector) {
        return mod(super.dotProduct(vector), parameters.getFiniteField());
    }

    @Override
    public FiniteFieldVector<T> scalarMult(double scalar) {
        FiniteFieldVector<T> result = new FiniteFieldVector<>(parameters, super.scalarMult(mod(scalar, parameters.getFiniteField())).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FiniteFieldVector<T> crossProduct(Vector vector) {
        FiniteFieldVector<T> result = new FiniteFieldVector<>(parameters, super.crossProduct(vector).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FiniteFieldVector<T> transform(Matrix matrix) {
        FiniteFieldVector<T> result = new FiniteFieldVector<>(parameters, super.transform(matrix).values());
        result.applyFiniteField();
        return result;
    }

    @Override
    public FiniteFieldVector<T> normalize() {
        throw new UnsupportedOperationException("Normalization is not supported in finite fields");
    }

    @Override
    public FiniteFieldVector<T> clone() {
        return new FiniteFieldVector<>(parameters, values());
    }

    @Override
    public String toString() {
        return "FiniteFieldVector{" +
                "values=" + super.toString() +
                ", finiteField=" + parameters.getFiniteField() +
                '}';
    }

    /**
     * A marker interface for binary vectors, which are vectors in the finite field of size 2.
     */
    public interface BinaryVector extends Parameters {
        @Override
        default int getFiniteField() {
            return 2; // Binary field
        }
    }

    /**
     * Creates a new instance of a BinaryVector.
     * This is a marker interface for vectors in the finite field of size 2.
     *
     * @return a new BinaryVector instance
     */
    public static BinaryVector binaryVector() {
        return new BinaryVector() {
            // No additional methods needed, just a marker interface
        };
    }
}
