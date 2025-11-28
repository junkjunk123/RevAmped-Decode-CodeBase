package org.firstinspires.ftc.teamcode.revAmped.util;

public class Mutatable<T> {
    private T val;

    public Mutatable() {}

    public Mutatable(T val) {
        set(val);
    }

    public T get() {
        return val;
    }

    public void set(T val) {
        this.val = val;
    }
}
