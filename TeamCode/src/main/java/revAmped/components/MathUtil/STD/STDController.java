package revAmped.components.MathUtil.STD;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.util.Range;

public class STDController {
    private int targetPosition = 0;
    private CustomSTDCoefficients coefficients;
    private int prevError = 0;
    private ElapsedTime timer = new ElapsedTime();
    private int maxPos;

    public STDController(CustomSTDCoefficients coefficients, int maxPos) {
        this.coefficients = coefficients;
        this.maxPos = maxPos;
    }

    public void updateConstants(CustomSTDCoefficients constants) {
        coefficients = constants;
    }

    public void runToPosition(int pos) {
        setTargetPosition(pos);
    }

    public void setTargetPosition(int pos) {
        targetPosition = pos;
    }

    public int getTargetPosition() {
        timer.reset();
        return targetPosition;
    }

    public double run(int currentPos) {
        int error = targetPosition - currentPos;
        double seconds = timer.seconds();
        timer.reset();
        double deltaError = (double) (error - prevError)/seconds;
        prevError = error;
        double correction = deltaError * coefficients.D;
        double sigmoid = Range.scale(1 - (double) Math.abs(error) / maxPos, 0, 1, -6, 6) - coefficients.C;
        sigmoid = 1 / (1 + Math.exp(-sigmoid));
        double power = coefficients.K * Math.sqrt(Math.abs(error * (1 - sigmoid))) * Math.signum(error);
        double feedforward = Math.signum(targetPosition - currentPos) * coefficients.F;
        if (targetPosition == 0) feedforward = 0;
        return power + feedforward + correction;
    }

    public double getError() {
        return prevError;
    }
}
