package org.firstinspires.ftc.teamcode.revAmped.util;

import revAmped.components.math.MovingAverage;

public class LoopTimer {
    private long previousTime;
    private long interval = 1;
    private long maxInterval = 1;
    private MovingAverage average;

    public LoopTimer(int period) {
        average = new MovingAverage(period);
    }

    public void begin() {
        previousTime = System.currentTimeMillis();
    }

    public long update() {
        long temp = System.currentTimeMillis();
        interval = temp - previousTime;
        previousTime = temp;

        if (interval > maxInterval) {
            maxInterval = interval;
        }

        average.addData(interval);
        return interval;
    }

    public long getMaxLoopTime() {
        return maxInterval;
    }

    public double getAverageLoopTime() {
        return average.getMovingAverageForNum();
    }

    public long getInterval() {
        return interval;
    }
}
