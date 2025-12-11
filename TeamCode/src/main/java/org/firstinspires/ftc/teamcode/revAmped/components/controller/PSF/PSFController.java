package org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF;

import com.pedropathing.control.LowPassFilter;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

public class PSFController {
    private PSFLCoefficients coefficients;
    private final LowPassFilter lowPassFilter;
    private boolean useFilter;
    private double prevError = 0;
    private double prevPrevError = 0;

    public PSFController(PSFLCoefficients coefficients) {
        lowPassFilter = new LowPassFilter(coefficients.L());
        this.coefficients = coefficients;
    }

    public double run(double currentPos, double targetPos) {
        double error = targetPos - currentPos;
        double sign = Math.signum(error);
        error = sign * Math.min(Math.abs(error), targetPos);

        if (useFilter) {
            lowPassFilter.update(error, 2 * prevError - prevPrevError);
            error = lowPassFilter.getState();
            prevPrevError = prevError;
            prevError = error;
        }

        Globals.telemetry.addData("filteredError", error);
        return coefficients.P() * error + targetPos * coefficients.K_V() + coefficients.K_static() * Math.signum(error);
    }

    public void setCoefficients(PSFLCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    public void setUseFilter(boolean useFilter) {
        this.useFilter = useFilter;
    }

    public void reset(double error) {
        lowPassFilter.reset(error, 0, 0);
        prevError = error;
        prevPrevError = error;
    }

    public double getPrevError() {
        return prevError;
    }
}
