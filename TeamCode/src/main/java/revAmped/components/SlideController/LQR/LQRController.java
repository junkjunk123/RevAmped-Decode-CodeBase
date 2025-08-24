package revAmped.components.SlideController.LQR;

import com.qualcomm.robotcore.util.ElapsedTime;

public class LQRController {
    private static final double LOOP_TIME = 0.01;
    private SlideLQR calculator;
    private CustomLQRCoefficients coefficients;
    private int targetPosition;
    private int prevPos = 0;
    private double prevVel = 0;
    private ElapsedTime timer = new ElapsedTime();

    public LQRController(CustomLQRCoefficients customLQRCoefficients) {
        coefficients = customLQRCoefficients;
        calculator = new SlideLQR(customLQRCoefficients.getPosTol(), customLQRCoefficients.getVelTol(), customLQRCoefficients.getRMult(), LOOP_TIME);
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void runToPosition(int targetPosition) {
        setTargetPosition(targetPosition);
    }

    public double run(int currentPosition) {
        int deltaX = currentPosition - prevPos;
        double dt = timer.seconds();
        timer.reset();
        double currentVel = (double) deltaX / dt;
        prevPos = currentPosition;
        prevVel = currentVel;
        double error = targetPosition - currentPosition;

        return calculator.update(error, currentVel);
    }

    public double run(int currentPosition, double velocity) {
        return calculator.update(targetPosition - currentPosition, velocity);
    }

    public void updateCoefficients(CustomLQRCoefficients customLQRCoefficients) {
        if (!coefficients.equals(customLQRCoefficients)) {
            coefficients = customLQRCoefficients;
            calculator = new SlideLQR(customLQRCoefficients.getPosTol(), customLQRCoefficients.getVelTol(), customLQRCoefficients.getRMult(), LOOP_TIME);
        }
    }

    public CustomLQRCoefficients getCoefficients() {
        return coefficients;
    }

    public double getVelocity() {
        return prevVel;
    }
}
