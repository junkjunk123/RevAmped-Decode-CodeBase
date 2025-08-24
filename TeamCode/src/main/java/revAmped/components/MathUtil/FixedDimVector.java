package revAmped.components.MathUtil;

/**
 * A class representing a fixed-dimension vector with specified parameters.
 * @param <T> the type of parameters that define the vector's dimension
 */
public class FixedDimVector<T extends FixedDimVector.Parameters> extends Vector {
    /**
     * The parameters defining the vector's dimension.
     */
    private final T parameters;

    /**
     * An interface for parameters that define the vector's dimension.
     */
    public interface Parameters {
        int getDimension();
    }

    /**
     * Constructs a FixedDimVector with the specified parameters and values.
     *
     * @param parameters the parameters defining the vector's dimension
     * @param values the values of the vector
     * @throws IllegalArgumentException if the number of values does not match the dimension defined by parameters
     */
    public FixedDimVector(T parameters, double... values) {
        super(values);
        if (values.length != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimension does not match fixed parameters");
        }
        this.parameters = parameters;
    }

    /**
     * Returns the parameters defining the vector's dimension.
     * @return the parameters defining the vector's dimension
     */
    public int getDimension() {
        return parameters.getDimension();
    }

    @Override
    public void setValues(double[] values) {
        if (values.length != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimension does not match fixed parameters");
        }
        super.setValues(values);
    }

    @Override
    public FixedDimVector<T> add(Vector vector) {
        if (vector.dim() != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimensions must match for addition");
        }
        return new FixedDimVector<>(parameters, super.add(vector).values());
    }

    @Override
    public FixedDimVector<T> subtract(Vector vector) {
        if (vector.dim() != parameters.getDimension()) {
            throw new IllegalArgumentException("Vector dimensions must match for subtraction");
        }
        return new FixedDimVector<>(parameters, super.subtract(vector).values());
    }

    @Override
    public FixedDimVector<T> scalarMult(double scalar) {
        return new FixedDimVector<>(parameters, super.scalarMult(scalar).values());
    }

    @Override
    public FixedDimVector<T> crossProduct(Vector vector) {
        if (parameters.getDimension() != 3 || vector.dim() != 3) {
            throw new IllegalArgumentException("Cross product is only defined for 3-dimensional vectors");
        }
        return new FixedDimVector<>(parameters, super.crossProduct(vector).values());
    }

    @Override
    public FixedDimVector<T> transform(Matrix matrix) {
        if (matrix.getRows() != parameters.getDimension() || matrix.getColumns() != parameters.getDimension()) {
            throw new IllegalArgumentException("Matrix dimensions must match the vector dimension for transformation");
        }
        return new FixedDimVector<>(parameters, super.transform(matrix).values());
    }

    @Override
    public FixedDimVector<T> clone() {
        return new FixedDimVector<>(parameters, values());
    }

    @Override
    public String toString() {
        return "FixedDimVector{" +
                "dimension=" + parameters.getDimension() +
                ", values=" + super.toString() +
                '}';
    }
}