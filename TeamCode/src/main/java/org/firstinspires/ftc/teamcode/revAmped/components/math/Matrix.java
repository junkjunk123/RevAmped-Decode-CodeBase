//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.teamcode.revAmped.components.math;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

/**
 * A class representing a mathematical matrix with various operations.
 * This class supports basic matrix operations such as addition, subtraction,
 * multiplication, transposition, and row reduction to reduced row echelon form (RREF).
 */
public class Matrix {
    private double[][] matrix;
    private int rowCount;
    private int colCount;

    /**
     * Constructs an empty Matrix with zero rows and columns.
     */
    public Matrix() {
        this(0, 0);
    }

    /**
     * Constructs a Matrix with the specified number of rows and columns.
     *
     * @param rowCount the number of rows in the matrix
     * @param colCount the number of columns in the matrix
     */
    public Matrix(int rowCount, int colCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.matrix = new double[this.rowCount][this.colCount];
    }

    /**
     * Constructs a Matrix with the specified 2D array.
     *
     * @param m the 2D array representing the matrix
     */
    public Matrix(double[][] m) {
        this.setMatrix(m);
    }

    /**
     * Constructs a Matrix from another Matrix object.
     *
     * @param setMatrix the Matrix object to copy
     */
    public void setMatrix(Matrix setMatrix) {
        this.setMatrix(setMatrix.getMatrix());
    }

    /**
     * Sets the matrix to the specified 2D array.
     * The array must be rectangular (all rows must have the same number of columns).
     *
     * @param setMatrix the 2D array to set as the matrix
     */
    public void setMatrix(double[][] setMatrix) {
        int columns = setMatrix[0].length;

        for (double[] doubles : setMatrix) {
            if (doubles.length != columns) {
                return;
            }
        }

        this.matrix = deepCopy(setMatrix);
        this.rowCount = setMatrix.length;
        this.colCount = setMatrix[0].length;
    }

    /**
     * Creates a copy of the current Matrix object.
     *
     * @return a new Matrix object that is a copy of this matrix
     */
    public Matrix copy() {
        Matrix output = new Matrix(this.rowCount, this.colCount);

        for(int i = 0; i < this.rowCount; ++i) {
            double[] temp;
            temp = Arrays.copyOf(this.getRow(i), this.colCount);
            output.setRow(i, temp);
        }

        return output;
    }

    /**
     * Creates a deep copy of the specified 2D array.
     *
     * @param copyMatrix the 2D array to copy
     * @return a new 2D array that is a deep copy of the input array
     */
    public static double[][] deepCopy(double[][] copyMatrix) {
        double[][] returnMatrix = new double[copyMatrix.length][copyMatrix[0].length];

        for(int i = 0; i < copyMatrix.length; ++i) {
            returnMatrix[i] = Arrays.copyOf(copyMatrix[i], copyMatrix[i].length);
        }

        return returnMatrix;
    }

    /**
     * Returns the underlying 2D array representing the matrix.
     *
     * @return the 2D array of the matrix
     */
    public double[][] getMatrix() {
        return this.matrix;
    }

    /**
     * Returns the number of rows in the matrix.
     *
     * @return the number of rows
     */
    public int getRows() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the matrix.
     *
     * @return the number of columns
     */
    public int getColumns() {
        return this.colCount;
    }

    /**
     * Returns the size of the matrix as an array where index 0 is the number of rows
     * and index 1 is the number of columns.
     *
     * @return an array containing the number of rows and columns
     */
    public int[] getSize() {
        return new int[]{this.rowCount, this.colCount};
    }

    /**
     * Returns the number of columns in the matrix.
     *
     * @return the number of columns
     */
    public double[] get(int row) {
        return Arrays.copyOf(this.matrix[row], this.matrix[row].length);
    }

    /**
     * Sets the specified row of the matrix to the provided input array.
     * The input array must have the same length as the number of columns in the matrix.
     *
     * @param row    the index of the row to set
     * @param input  the array containing the new values for the row
     * @return true if the row was set successfully, false if the input length does not match
     */
    public boolean set(int row, double[] input) {
        if (input.length != this.getColumns()) {
            return false;
        } else {
            this.matrix[row] = Arrays.copyOf(input, input.length);
            return true;
        }
    }

    /**
     * Returns a copy of the specified row of the matrix.
     *
     * @param row the index of the row to retrieve
     * @return a copy of the specified row as an array
     */
    public double[] getRow(int row) {
        return Arrays.copyOf(this.matrix[row], this.colCount);
    }

    /**
     * Sets the specified row of the matrix to the provided elements.
     * The number of elements should not exceed the number of columns in the matrix.
     *
     * @param row       the index of the row to set
     * @param elements  the elements to set in the row
     */
    public void setRow(int row, double... elements) {
        int len = Math.min(elements.length, this.rowCount);

        for(int i = 0; i < len; ++i) {
            this.matrix[row][i] = elements[i];
        }

    }

    /**
     * Returns a copy of the specified column of the matrix.
     *
     * @param col the index of the column to retrieve
     * @return a copy of the specified column as an array
     */
    public double[] getCol(int col) {
        double[] output = new double[this.rowCount];

        for(int i = 0; i < this.rowCount; ++i) {
            output[i] = this.matrix[i][col];
        }

        return output;
    }

    /**
     * Sets the specified column of the matrix to the provided elements.
     * The number of elements should not exceed the number of rows in the matrix.
     *
     * @param col       the index of the column to set
     * @param elements  the elements to set in the column
     */
    public void setCol(int col, double... elements) {
        int len = Math.min(elements.length, this.rowCount);

        for(int i = 0; i < len; ++i) {
            this.matrix[i][col] = elements[i];
        }

    }

    /**
     * Returns the value at the specified row and column in the matrix.
     *
     * @param row the index of the row
     * @param col the index of the column
     * @return the value at the specified position in the matrix
     */
    public double get(int row, int col) {
        return this.matrix[row][col];
    }

    /**
     * Sets the value at the specified row and column in the matrix.
     *
     * @param row   the index of the row
     * @param col   the index of the column
     * @param value the value to set at the specified position
     */
    public void set(int row, int col, double value) {
        this.matrix[row][col] = value;
    }

    /**
     * Adds another matrix to this matrix.
     * The other matrix must have the same dimensions as this matrix.
     *
     * @param other the matrix to add
     * @return a new Matrix that is the sum of this matrix and the other matrix
     * @throws RuntimeException if the dimensions of the matrices do not match
     */
    public Matrix plus(Matrix other) {
        if (other.rowCount == this.rowCount && other.colCount == this.colCount) {
            Matrix output = new Matrix(this.rowCount, this.colCount);
            double[] nRow = new double[output.colCount];

            for(int i = 0; i < output.rowCount; ++i) {
                for(int j = 0; j < nRow.length; ++j) {
                    nRow[j] = this.matrix[i][j] + other.matrix[i][j];
                }

                output.setRow(i, nRow);
            }

            return output;
        } else {
            throw new RuntimeException(String.format("Size is not equal. size(A) = [%d, %d]; size(B) = [%d, %d]", this.rowCount, this.colCount, other.rowCount, other.colCount));
        }
    }

    /**
     * Subtracts another matrix from this matrix.
     * The other matrix must have the same dimensions as this matrix.
     *
     * @param other the matrix to subtract
     * @return a new Matrix that is the result of subtracting the other matrix from this matrix
     * @throws RuntimeException if the dimensions of the matrices do not match
     */
    public Matrix minus(Matrix other) {
        if (other.rowCount == this.rowCount && other.colCount == this.colCount) {
            Matrix output = new Matrix(this.rowCount, this.colCount);
            double[] nRow = new double[output.colCount];

            for(int i = 0; i < output.rowCount; ++i) {
                for(int j = 0; j < nRow.length; ++j) {
                    nRow[j] = this.matrix[i][j] - other.matrix[i][j];
                }

                output.setRow(i, nRow);
            }

            return output;
        } else {
            throw new RuntimeException(String.format("Size is not equal. size(A) = [%d, %d]; size(B) = [%d, %d]", this.rowCount, this.colCount, other.rowCount, other.colCount));
        }
    }

    /**
     * Multiplies this matrix by a scalar value.
     *
     * @param scalar the scalar value to multiply the matrix by
     * @return a new Matrix that is the result of multiplying this matrix by the scalar
     */
    public Matrix multiply(double scalar) {
        Matrix output = new Matrix(this.rowCount, this.colCount);

        for(int i = 0; i < output.rowCount; ++i) {
            for(int j = 0; j < output.colCount; ++j) {
                output.matrix[i][j] = scalar * this.matrix[i][j];
            }
        }

        return output;
    }

    /**
     * Multiplies this matrix by another matrix.
     * The other matrix must have a number of rows equal to the number of columns in this matrix.
     *
     * @param other the matrix to multiply with
     * @return a new Matrix that is the result of multiplying this matrix by the other matrix
     * @throws RuntimeException if the dimensions of the matrices do not match for multiplication
     */
    public Matrix multiply(Matrix other) {
        if (other.rowCount != this.colCount) {
            throw new RuntimeException(String.format("Size mismatch for matrix multiplication. size(A) = [%d, %d]; size(B) = [%d, %d]", this.rowCount, this.colCount, other.rowCount, other.colCount));
        } else {
            Matrix output = new Matrix(this.rowCount, other.colCount);

            for(int i = 0; i < output.rowCount; ++i) {
                double[] rowSample = this.getRow(i);

                for(int j = 0; j < output.colCount; ++j) {
                    double dpSum = 0.0;
                    double[] colSample = other.getCol(j);

                    for(int k = 0; k < rowSample.length; ++k) {
                        dpSum += rowSample[k] * colSample[k];
                    }

                    output.matrix[i][j] = dpSum;
                }
            }

            return output;
        }
    }

    /**
     * Multiplies two matrices together.
     * This is a static method that calls the instance method multiply.
     *
     * @param one the first matrix
     * @param two the second matrix
     * @return a new Matrix that is the result of multiplying the two matrices
     */
    public static Matrix multiply(Matrix one, Matrix two) {
        return one.multiply(two);
    }

    /**
     * Negates the matrix by multiplying all elements by -1.
     * @return a new Matrix that is the negation of this matrix
     */
    public Matrix unaryMinus() {
        return this.multiply(-1.0);
    }

    /**
     * Transposes the matrix, swapping rows and columns.
     *
     * @return a new Matrix that is the transposed version of this matrix
     */
    public Matrix transposed() {
        Matrix output = new Matrix(this.colCount, this.rowCount);

        for(int i = 0; i < this.colCount; ++i) {
            output.setRow(i, this.getCol(i));
        }

        return output;
    }

    /**
     * Swaps two rows in the matrix.
     *
     * @param srcRow the index of the source row to swap
     * @param destRow the index of the destination row to swap
     */
    public void rowSwap(int srcRow, int destRow) {
        double[] cachedRow = Arrays.copyOf(this.matrix[srcRow], this.colCount);
        this.matrix[srcRow] = Arrays.copyOf(this.matrix[destRow], this.colCount);
        this.matrix[destRow] = Arrays.copyOf(cachedRow, cachedRow.length);
    }

    /**
     * Scales a specified row by a scalar value.
     *
     * @param row the index of the row to scale
     * @param scalar the scalar value to multiply the row by
     */
    public void rowScale(int row, double scalar) {
        for(int i = 0; i < this.colCount; ++i) {
            double[] var10000 = this.matrix[row];
            var10000[i] *= scalar;
        }

    }

    /**
     * Adds a scaled version of one row to another row.
     *
     * @param srcRow the index of the source row to add
     * @param destRow the index of the destination row to which the source row will be added
     * @param scalar the scalar value by which the source row will be multiplied before addition
     */
    public void rowAdd(int srcRow, int destRow, double scalar) {
        for(int i = 0; i < this.colCount; ++i) {
            double[] var10000 = this.matrix[destRow];
            var10000[i] += this.matrix[srcRow][i] * scalar;
        }

    }

    /**
     * Reduces the matrix to its reduced row echelon form (RREF) along with an augment matrix.
     *
     * @param matrix the matrix to reduce
     * @param augment the augment matrix to reduce alongside
     * @return an array containing the reduced matrix and the reduced augment matrix
     */
    public static Matrix[] rref(Matrix matrix, Matrix augment) {
        Matrix outputMatrix = matrix.copy();
        Matrix outputAugment = augment.copy();
        int rowLim = outputMatrix.getRows();
        int colLim = outputMatrix.getColumns();
        int currentCol = 0;

        int r1;
        int pivotCol;
        int r2;
        double scalar;
        for(r1 = 0; r1 < rowLim; ++r1) {
            for(pivotCol = 0; pivotCol < colLim; ++pivotCol) {
                r2 = isPivotInCol(outputMatrix, r1, pivotCol);
                if (r2 != -1) {
                    currentCol = pivotCol;
                    outputMatrix.rowSwap(r1, r2);
                    outputAugment.rowSwap(r1, r2);
                    break;
                }
            }

            for(pivotCol = r1 + 1; pivotCol < rowLim; ++pivotCol) {
                if (outputMatrix.get(pivotCol, currentCol) != 0.0) {
                    scalar = -outputMatrix.get(pivotCol, currentCol) / outputMatrix.get(r1, currentCol);
                    outputMatrix.rowAdd(r1, pivotCol, scalar);
                    outputAugment.rowAdd(r1, pivotCol, scalar);
                }
            }
        }

        for(r1 = 0; r1 < rowLim; ++r1) {
            pivotCol = -1;

            for(r2 = 0; r2 < colLim; ++r2) {
                if (outputMatrix.get(r1, r2) != 0.0) {
                    pivotCol = r2;
                    break;
                }
            }

            if (pivotCol != -1) {
                scalar = 1.0 / outputMatrix.get(r1, pivotCol);
                outputMatrix.rowScale(r1, scalar);
                outputAugment.rowScale(r1, scalar);
            }
        }

        for(r1 = colLim - 1; r1 > 0; --r1) {
            pivotCol = -1;

            for(r2 = 0; r2 < colLim; ++r2) {
                if (outputMatrix.get(r1, r2) != 0.0) {
                    pivotCol = r2;
                    break;
                }
            }

            if (pivotCol != -1) {
                for(r2 = 0; r2 < r1; ++r2) {
                    scalar = -outputMatrix.get(r2, pivotCol);
                    outputMatrix.rowAdd(r1, r2, scalar);
                    outputAugment.rowAdd(r1, r2, scalar);
                }
            }
        }

        return new Matrix[]{outputMatrix, outputAugment};
    }

    /**
     * Checks if there is a pivot in the specified column starting from the given row.
     *
     * @param queriedMatrix the matrix to check for a pivot
     * @param startRow the row to start checking from
     * @param col the column to check for a pivot
     * @return the index of the row containing the pivot, or -1 if no pivot is found
     */
    private static int isPivotInCol(Matrix queriedMatrix, int startRow, int col) {
        int output = -1;

        for(int r1 = startRow; r1 < queriedMatrix.getRows(); ++r1) {
            if (queriedMatrix.get(r1, col) != 0.0) {
                output = r1;
                break;
            }
        }

        return output;
    }

    /**
     * Returns a string representation of the matrix.
     * The string contains the values of the matrix formatted to five decimal places.
     *
     * @return a string representation of the matrix
     */
    @NotNull
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for(int i = 0; i < this.rowCount; ++i) {
            for(int j = 0; j < this.colCount; ++j) {
                builder.append(String.format(Locale.getDefault(), "%.5f, ", this.matrix[i][j]));
            }

            builder.append("\b\b; ");
        }

        builder.append("\b\b]");
        return builder.toString();
    }
}
