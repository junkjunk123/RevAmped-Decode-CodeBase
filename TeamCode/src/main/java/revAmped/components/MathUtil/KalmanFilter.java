//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package revAmped.components.MathUtil;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A class that implements a Kalman filter for estimating the state of a system.
 * The Kalman filter uses model covariance and data covariance parameters to update its state.
 */
public class KalmanFilter {
    /**
     * Parameters for the Kalman filter, including model covariance and data covariance.
     */
    public static class KalmanFilterParameters {
        /**
         * The covariance of the model, representing the uncertainty in the model's predictions.
         */
        public double modelCovariance;
        /**
         * The covariance of the data, representing the uncertainty in the measurements.
         */
        public double dataCovariance;

        /**
         * Constructs KalmanFilterParameters with the specified model covariance and data covariance.
         *
         * @param modelCovariance the covariance of the model
         * @param dataCovariance the covariance of the data
         */
        public KalmanFilterParameters(double modelCovariance, double dataCovariance) {
            this.modelCovariance = modelCovariance;
            this.dataCovariance = dataCovariance;
        }
    }

    /**
     * The Kalman filter state, variance, and gain.
     * The state represents the estimated value of the system,
     * variance represents the uncertainty in the estimate,
     * and kalmanGain is used to adjust the estimate based on new data.
     */
    private KalmanFilterParameters parameters;
    private double state;
    private double variance;
    private double kalmanGain;
    private double previousState;
    private double previousVariance;


    /**
     * Constructs a KalmanFilter with the specified parameters.
     *
     * @param parameters the parameters for the Kalman filter, including model covariance and data covariance
     */
    public KalmanFilter(KalmanFilterParameters parameters) {
        this.parameters = parameters;
        this.reset();
    }

    /**
     * Constructs a KalmanFilter with the specified parameters and initial state, variance, and gain.
     *
     * @param parameters the parameters for the Kalman filter, including model covariance and data covariance
     * @param startState the initial state of the filter
     * @param startVariance the initial variance of the filter
     * @param startGain the initial Kalman gain of the filter
     */
    public KalmanFilter(KalmanFilterParameters parameters, double startState, double startVariance, double startGain) {
        this.parameters = parameters;
        this.reset(startState, startVariance, startGain);
    }

    /**
     * Resets the Kalman filter to the specified initial state, variance, and gain.
     *
     * @param startState the initial state of the filter
     * @param startVariance the initial variance of the filter
     * @param startGain the initial Kalman gain of the filter
     */
    public void reset(double startState, double startVariance, double startGain) {
        this.state = startState;
        this.previousState = startState;
        this.variance = startVariance;
        this.previousVariance = startVariance;
        this.kalmanGain = startGain;
    }

    /**
     * Resets the Kalman filter to its default initial state, variance, and gain.
     * The default values are 0.0 for state, 1.0 for variance, and 1.0 for Kalman gain.
     */
    public void reset() {
        this.reset(0.0, 1.0, 1.0);
    }

    /**
     * Updates the Kalman filter with new data and projection.
     * The update adjusts the state and variance based on the new data and projection.
     *
     * @param updateData the new data to update the filter with
     * @param updateProjection the new projection to adjust the state
     */
    public void update(double updateData, double updateProjection) {
        this.state = this.previousState + updateData;
        this.variance = this.previousVariance + this.parameters.modelCovariance;
        this.kalmanGain = this.variance / (this.variance + this.parameters.dataCovariance);
        this.state += this.kalmanGain * (updateProjection - this.state);
        this.variance *= 1.0 - this.kalmanGain;
        this.previousState = this.state;
        this.previousVariance = this.variance;
    }

    /**
     * Returns the current state of the Kalman filter.
     * @return the current state of the filter
     */
    public double getState() {
        return this.state;
    }

    public void debug(Telemetry telemetry) {
        telemetry.addData("state", this.state);
        telemetry.addData("variance", this.variance);
        telemetry.addData("Kalman gain", this.kalmanGain);
    }
}
