package revAmped.components.MathUtil.SquID;

import java.util.function.BooleanSupplier;

public class SquIDController {
    private double kP, kF, kL;
    private int deadzoneTolerance, targetPosition = 0;
    private boolean homed = false;
    private BooleanSupplier homedTrue;
    private int zeroThreshold;
    private double homedConstant;
    private boolean deadZoned = false;
    private double previousError = 0;

    public SquIDController(double kP, double kL, double kF, int zeroThreshold) {
        this.kP = kP;
        this.kF = kF;
        this.kL = kL;
        this.zeroThreshold = zeroThreshold;
        homedConstant = -kL;
    }

    public SquIDController(double kP, double kL, double kF, int zeroThreshold, double homedConstant, BooleanSupplier homedTrue) {
        this.kP = kP;
        this.kF = kF;
        this.kL = kL;
        this.zeroThreshold = zeroThreshold;
        this.homedConstant = homedConstant;
        this.homedTrue = homedTrue;
    }

    public SquIDController(CustomSquIDConstants constants, int zeroThreshold, double homedConstant, BooleanSupplier homedTrue) {
        this.kP = constants.getkP();
        this.kF = constants.getkF();
        this.kL = constants.getkL();
        this.zeroThreshold = zeroThreshold;
        this.homedConstant = homedConstant;
        this.homedTrue = homedTrue;
    }

    public void updateConstants(double kP, double kL, double kF) {
        this.kP = kP;
        this.kF = kF;
        this.kL = kL;
    }

    public void updateConstants(CustomSquIDConstants constants) {
        this.kP = constants.getkP();
        this.kF = constants.getkF();
        this.kL = constants.getkL();
    }

    public double run(double currentPosition) {
        return run(currentPosition, false);
    }

    public double run(double currentPosition, boolean usePForDeadzone) {
        if (homedTrue != null) {
            homed = homedTrue.getAsBoolean();
        }

        double error = targetPosition - currentPosition;
        previousError = error;

        if (homed) {
            if (currentPosition > zeroThreshold) {
                return homedConstant;
            } else {
                return 0;
            }
        }

        if (Math.abs(error) < deadzoneTolerance) {
            deadZoned = true;

            if (usePForDeadzone) {
                return sqrt(error);
            } else {
                return kF;
            }
        }

        double p = sqrt(error);

        deadZoned = false;
        double f = kF;
        double l = lowerLimit(error);

        return p + f + l;
    }

    public double lowerLimit(double error) {
        return Math.signum(error) * kL;
    }

    public void runToPosition(int position, int deadzoneTolerance) {
        this.deadzoneTolerance = deadzoneTolerance;
        targetPosition = position;
        homed = false;
    }

    public double sqrt(double error) {
        return kP*Math.sqrt(Math.abs(error))*Math.signum(error);
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;

        if (targetPosition > zeroThreshold) {
            homed = false;
        }
    }

    public int getDeadzoneTolerance() {
        return deadzoneTolerance;
    }

    public double getkL() {
        return kL;
    }

    public double getkF() {
        return kF;
    }

    public void setHomed() {
        homed = true;
        targetPosition = 0;
    }

    public boolean isDeadZoned() {
        return deadZoned;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public double getError() {
        return previousError;
    }

    public void setHomedConstant(double homedConstant) {
        this.homedConstant = homedConstant;
    }

    public void setDeadzoneTolerance(int deadzoneTolerance) {
        this.deadzoneTolerance = deadzoneTolerance;
    }

    public void setZeroThreshold(int zeroThreshold) {
        this.zeroThreshold = zeroThreshold;
    }

    public boolean isHomed() {
        return homed;
    }
}
