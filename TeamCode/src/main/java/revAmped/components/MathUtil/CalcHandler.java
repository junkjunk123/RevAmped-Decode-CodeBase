package revAmped.components.MathUtil;
import com.qualcomm.robotcore.util.ElapsedTime;

public class CalcHandler {
    private double integral = 0;
    private int count = 0;
    private double prevVal = 0;
    private double derivative = 0;
    private final ElapsedTime timer = new ElapsedTime();

    public void update(double newVal) {
        double dt = timer.nanoseconds() / Math.pow(10.0, 9.0);
        timer.reset();
        integral += newVal * dt;
        derivative = (newVal - prevVal) / dt;
        prevVal = newVal;
        count++;
    }

    public double getVal() {
        return prevVal;
    }

    public double getIntegral() {
        return integral;
    }

    public double getDerivative() {
        return derivative;
    }

    public void reset() {
        integral = 0;
        prevVal = 0;
        derivative = 0;
        timer.reset();
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAvg() {
        return integral / count;
    }
}
