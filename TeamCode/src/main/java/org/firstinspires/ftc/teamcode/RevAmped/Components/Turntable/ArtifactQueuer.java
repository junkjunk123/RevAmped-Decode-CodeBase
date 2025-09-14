package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.Color;

public interface ArtifactQueuer<T> {
    void preparePurple();
    void prepareGreen();
    void pop();
    Color.ArtifactColor[] getBallColors();
    void intake();
    void update();
    void setCurrentState(T state);
    void setCurrentState(T state, boolean initAction);
    T getCurrentState();
}
