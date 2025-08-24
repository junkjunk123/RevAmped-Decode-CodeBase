package revAmped.components.MathUtil;

public class LinearFunction implements SingleVariateFunction {
    private double slope;
    private double y_intercept;

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

    public double findRoot() {
        return findInput(0);
    }

    @Override
    public double evaluateAtInput(double x) {
        return slope*x+y_intercept;
    }

    public double findInput(double y) {return (y-y_intercept)/slope;}
}
