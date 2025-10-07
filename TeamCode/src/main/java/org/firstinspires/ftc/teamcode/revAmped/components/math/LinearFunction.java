package org.firstinspires.ftc.teamcode.revAmped.components.math;

import java.util.function.Function;

/**
 * A class representing a linear function of the form y = mx + b.
 * It implements the Function interface to allow evaluation at a given input.
 */
public class LinearFunction implements Function<Double, Double> {
    private double slope;
    private double y_intercept;

    /**
     * Constructs a LinearFunction with the specified slope and y-intercept.
     *
     * @param slope the slope of the linear function
     * @param y_intercept the y-intercept of the linear function
     */
    public LinearFunction(double slope, double y_intercept) {
        this.slope = slope;
        this.y_intercept = y_intercept;
    }


    public double getSlope() {
        return slope;
    }

    public double getY_intercept() {
        return y_intercept;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public void setY_intercept(double y_intercept) {
        this.y_intercept = y_intercept;
    }

    /**
     * Finds the root of the linear function, which is the input value where the output is zero.
     *
     * @return the input value where the output of the linear function is zero
     */
    public double findRoot() {
        return findInput(0);
    }

    /**
     * Evaluates the linear function at a given input x.
     *
     * @param x the input value
     * @return the output value of the linear function at x
     */
    public double evaluateAtInput(double x) {
        return slope*x+y_intercept;
    }

    /**
     * Finds the input value corresponding to a given output y.
     *
     * @param y the output value
     * @return the input value that produces the specified output
     */
    public double findInput(double y) {return (y-y_intercept)/slope;}

    /**
     * Evaluates the linear function at a given input x.
     *
     * @param aDouble the input value
     * @return the output value of the linear function at aDouble
     */
    @Override
    public Double apply(Double aDouble) {
        return evaluateAtInput(aDouble);
    }
}
