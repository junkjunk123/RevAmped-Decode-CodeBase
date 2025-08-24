package revAmped.components.MathUtil;

import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

public class RevAmpedTimer {
    private ElapsedTime timer;
    private long pauseTimestamp = 0;
    private boolean running;
    private boolean paused = false;

    public RevAmpedTimer(boolean startRunning) {
        this.running = startRunning;
        timer = new ElapsedTime();
        timer.reset();
    }

    public double millis() {
        return getCurrentTime(TimeUnit.MILLISECONDS);
    }

    public long nanos() {
        return getCurrentTime(TimeUnit.NANOSECONDS);
    }

    public double seconds() {
        return getCurrentTime(TimeUnit.SECONDS);
    }

    public void reset() {
        timer.reset();
        pauseTimestamp = 0;
    }

    private long getCurrentTime(TimeUnit unit) {
        if (!running) {
            return 0;
        } else if (paused) {
            return pauseTimestamp;
        } else {
            return unit.convert(timer.nanoseconds() - pauseTimestamp, TimeUnit.NANOSECONDS);
        }
    }

    public void start(boolean reset) {
        running = true;
        paused = false;
        pauseTimestamp = timer.nanoseconds() - pauseTimestamp;

        if (reset) {
            reset();
        }
    }

    public void start() {
        start(false);
    }

    public void stop() {
        running = false;
    }

    public void pause() {
        paused = true;
        pauseTimestamp = timer.nanoseconds();
    }
}
