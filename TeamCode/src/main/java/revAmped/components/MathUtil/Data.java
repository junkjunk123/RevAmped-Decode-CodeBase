package revAmped.components.MathUtil;

import com.qualcomm.robotcore.util.ElapsedTime;

public class Data {
    private ElapsedTime timer = new ElapsedTime();
    private double prevData = 0;
    private double prevDeriv = 0;
    private double curData = 0;
    private double curDeriv = 0;
    private double secondDeriv = 0;
    private double elapsedTime = 0;
    private double prevDiff = 0;
    private double curDiff = 0;

    public void update(double num) {
        elapsedTime = timer.time() / 1000.0;
        timer.reset();
        prevData = curData;
        curData = num;
        prevDeriv = curDeriv;
        curDeriv = (curData - prevData)/elapsedTime;
        secondDeriv = (curDeriv - prevDeriv)/elapsedTime;
        prevDiff = curDiff;
        curDiff = curData - prevData;
    }

    public double getCurData() {
        return curData;
    }

    public double getCurDeriv() {
        return curDeriv;
    }

    public double getPrevData() {
        return prevData;
    }

    public double getPrevDeriv() {
        return prevDeriv;
    }

    public double getSecondDeriv() {
        return secondDeriv;
    }

    public double getCurDiff() {
        return curDiff;
    }

    public double getSecondDiff() {
        return curDiff - prevDiff;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public double getPrevDiff() {
        return prevDiff;
    }
}

