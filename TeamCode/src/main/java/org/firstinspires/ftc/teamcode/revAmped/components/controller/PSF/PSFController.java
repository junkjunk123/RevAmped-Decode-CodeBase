package org.firstinspires.ftc.teamcode.revAmped.components.controller.PSF;

public class PSFController {
    private final PSFCoefficients coefficients;

    public PSFController(PSFCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    public double run(double currentPos, double targetPos) {
        double error = targetPos - currentPos;
        return coefficients.P() * error + targetPos * coefficients.K_V() + coefficients.K_static() * Math.signum(error);
    }
}
