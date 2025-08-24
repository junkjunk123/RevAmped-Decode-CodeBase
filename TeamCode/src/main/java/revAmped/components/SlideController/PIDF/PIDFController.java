package revAmped.components.SlideController.PIDF;

import com.pedropathing.util.CustomPIDFCoefficients;

public class PIDFController {
    private CustomPIDFCoefficients coefficients;
    private double previousError;
    private double error;
    private double position;
    private double targetPosition;
    private double errorIntegral;
    private double errorDerivative;
    private double feedForwardInput;
    private long previousUpdateTimeNano;
    private long deltaTimeNano;

    public PIDFController(CustomPIDFCoefficients set) {
        this.setCoefficients(set);
        this.reset();
    }

    public double runPIDF() {
        return this.error * this.P() + this.errorDerivative * this.D() + this.errorIntegral * this.I() + this.F();
    }

    public void updatePosition(double update) {
        this.position = update;
        this.previousError = this.error;
        this.error = this.targetPosition - this.position;
        this.deltaTimeNano = System.nanoTime() - this.previousUpdateTimeNano;
        this.previousUpdateTimeNano = System.nanoTime();
        this.errorIntegral += this.error * ((double)this.deltaTimeNano / Math.pow(10.0, 9.0));
        this.errorDerivative = (this.error - this.previousError) / ((double)this.deltaTimeNano / Math.pow(10.0, 9.0));
    }

    public void updateError(double error) {
        this.previousError = this.error;
        this.error = error;
        this.deltaTimeNano = System.nanoTime() - this.previousUpdateTimeNano;
        this.previousUpdateTimeNano = System.nanoTime();
        this.errorIntegral += error * ((double)this.deltaTimeNano / Math.pow(10.0, 9.0));
        this.errorDerivative = (error - this.previousError) / ((double)this.deltaTimeNano / Math.pow(10.0, 9.0));
    }

    public void updateFeedForwardInput(double input) {
        this.feedForwardInput = input;
    }

    public void reset() {
        this.previousError = 0.0;
        this.error = 0.0;
        this.position = 0.0;
        this.targetPosition = 0.0;
        this.errorIntegral = 0.0;
        this.errorDerivative = 0.0;
        this.previousUpdateTimeNano = System.nanoTime();
    }

    public void setTargetPosition(double set) {
        this.targetPosition = set;
    }

    public double getTargetPosition() {
        return this.targetPosition;
    }

    public void setCoefficients(CustomPIDFCoefficients set) {
        this.coefficients = set;
    }

    public CustomPIDFCoefficients getCoefficients() {
        return this.coefficients;
    }

    public void setP(double set) {
        this.coefficients.P = set;
    }

    public double P() {
        return this.coefficients.P;
    }

    public void setI(double set) {
        this.coefficients.I = set;
    }

    public double I() {
        return this.coefficients.I;
    }

    public void setD(double set) {
        this.coefficients.D = set;
    }

    public double D() {
        return this.coefficients.D;
    }

    public void setF(double set) {
        this.coefficients.F = set;
    }

    public double F() {
        return this.coefficients.F * feedForwardInput;
    }

    public double getError() {
        return this.error;
    }

    public void updateConstants(CustomPIDFCoefficients coefficients) {
        setCoefficients(coefficients);
    }

    public void runToPosition(int position) {
        setTargetPosition(position);
    }

    public double run(int currentPos) {
        updateFeedForwardInput(Math.signum(getTargetPosition() - currentPos));
        updatePosition(currentPos);
        return runPIDF();
    }

    public double getErrorDerivative() {
        return errorDerivative;
    }

    public double getErrorIntegral() {
        return errorIntegral;
    }
}
