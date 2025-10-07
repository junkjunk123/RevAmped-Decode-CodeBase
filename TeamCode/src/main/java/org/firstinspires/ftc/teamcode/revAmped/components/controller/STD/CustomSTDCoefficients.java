package org.firstinspires.ftc.teamcode.revAmped.components.controller.STD;

import revAmped.components.controller.SlideController;

public class CustomSTDCoefficients implements SlideController.SlideConstants {
    public double K, F, D, C;

    /**
     * Constructor for CustomSTDCoefficients
     * @param K Multiplier to sqrt -- heavily affects motion, often 10x SquID constant
     * @param F Feedforward added on
     * @param D Derivative value from PID
     * @param C Handles general aggressiveness of overshoot
     */
    public CustomSTDCoefficients(double K, double F, double D, double C) {
        this.K = K;
        this.F = F;
        this.D = D;
        this.C = C;
    }

    public void updateConstants(double K, double F, double D, double C) {
        this.K = K;
        this.F = F;
        this.D = D;
        this.C = C;
    }
}
