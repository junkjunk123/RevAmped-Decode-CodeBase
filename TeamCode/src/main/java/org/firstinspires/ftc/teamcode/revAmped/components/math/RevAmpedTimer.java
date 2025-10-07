package org.firstinspires.ftc.teamcode.revAmped.components.math;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

/**
 * A simple timer class that can be used to measure elapsed time in various units.
 * It supports starting, stopping, pausing, and resetting the timer.
 */
public class RevAmpedTimer {
    private ElapsedTime timer;
    private long pauseTimestamp = 0;
    private boolean running;
    private boolean paused = false;

    /**
     * Constructs a RevAmpedTimer that can be started in a running state.
     *
     * @param startRunning if true, the timer starts in a running state; otherwise, it starts paused.
     */
    public RevAmpedTimer(boolean startRunning) {
        this.running = startRunning;
        timer = new ElapsedTime();
        timer.reset();
    }

    /**
     * Returns the elapsed time in millseconds.
     * @return the elapsed time in milliseconds
     */
    public double millis() {
        return getCurrentTime(TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the elapsed time in nanoseconds.
     * @return the elapsed time in nanoseconds
     */
    public long nanos() {
        return getCurrentTime(TimeUnit.NANOSECONDS);
    }

    /**
     * Returns the elapsed time in seconds.
     * @return the elapsed time in seconds
     */
    public double seconds() {
        return getCurrentTime(TimeUnit.SECONDS);
    }

    /**
     * Resets the timer to zero and stops it.
     */
    public void reset() {
        timer.reset();
        pauseTimestamp = 0;
    }

    /**
     * Returns the current time in the specified unit.
     * @param unit the time unit to convert the elapsed time to
     * @return the elapsed time in the specified unit
     */
    private long getCurrentTime(TimeUnit unit) {
        if (!running) {
            return 0;
        } else if (paused) {
            return pauseTimestamp;
        } else {
            return unit.convert(timer.nanoseconds() - pauseTimestamp, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Starts the timer.
     * @param reset if true, the timer is reset to zero before starting; otherwise, it resumes from the current time.
     */
    public void start(boolean reset) {
        running = true;
        paused = false;
        pauseTimestamp = timer.nanoseconds() - pauseTimestamp;

        if (reset) {
            reset();
        }
    }

    /**
     * Starts the timer without resetting it.
     */
    public void start() {
        start(false);
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        running = false;
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        paused = true;
        pauseTimestamp = timer.nanoseconds();
    }

    /**
     * Checks if the timer is currently running.
     * @return true if the timer is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if the timer is currently paused.
     * @return true if the timer is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }
}
