package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;

public interface ArtifactQueuer {
    void preparePurple();
    void prepareGreen();
    void pop();
    DecodeColorSensor.ArtifactColor[] getBallColors();
    void intake();
    void update();
    boolean isMotifMode();
    void setMotifMode(boolean mode);

    default void toggleMotifMode() {
        setMotifMode(!isMotifMode());
    }
}
