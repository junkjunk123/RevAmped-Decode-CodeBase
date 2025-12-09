package org.firstinspires.ftc.teamcode.revAmped.util;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;

public abstract class Locked implements Lockable {
    private boolean locked = false;

    public void lock() {
        locked = true;
    }

    public void lockMillis(double millis) {
        lock();
        Scheduler.getInstance().add(new Delay(millis, this::unlock));
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public abstract void update();

    public void run() {
        if (locked) return;
        update();
    }
}
