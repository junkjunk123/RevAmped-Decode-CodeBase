package revAmped.components.MathUtil.PIDF;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.function.BooleanSupplier;

public class PDLFController {
    private double kP, kD, kL, kF, homedConstant;
    private int deadzoneTolerance, targetPosition = 0;
    private double previousError = 0;
    private ElapsedTime timer;
    private BooleanSupplier homed;

    public PDLFController(double kP, double kD, double kL, double kF, BooleanSupplier homed, double homedConstant) {
        this.kP = kP;
        this.kD = kD;
        this.kL = kL;
        this.kF = kF;
        this.homedConstant = homedConstant;
        this.homed = homed;
    }

    public void updateConstants(double kP, double kD, double kL, double kF) {
        this.kP = kP;
        this.kD = kD;
        this.kL = kL;
        this.kF = kF;
    }

    public double run(double currentPosition) {
        double error = targetPosition - currentPosition;

        if (homed.getAsBoolean()) {
            return homedConstant;
        }

        double p = proportional(error);
        double d = derivative(error);
        double f = kF;
        double l;

        if (Math.abs(error) < deadzoneTolerance){
            l = 0;
        } else {
            l = lowerLimit(error);
        }

        previousError = error;
        return p + d + f + l;
    }

    public void runToPosition(int position, int deadzoneTolerance) {
        this.deadzoneTolerance = deadzoneTolerance;
        targetPosition = position;
        reset();
    }

    private double proportional(double error) {
        return kP * error;
    }

    private double derivative(double error) {
        double deltaTime = timer.milliseconds();
        timer.reset();
        double derivative = (error - previousError)/deltaTime;

        return kD * derivative;
    }

    private double lowerLimit(double error) {
        return Math.signum(error) * kL;
    }

    public void reset() {
        previousError = 0;
        timer.reset();
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }
}
