package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;

public interface ArtifactQueuer {
    void preparePurple();
    void prepareGreen();
    void pop();
    Vector2D[] getBallColors();
    void intake();
    void update();
    boolean isMotifMode();
    void setMotifMode(boolean mode);

    default void toggleMotifMode() {
        setMotifMode(!isMotifMode());
    }

    Types.QueuerHardwareComponents hardware();

    boolean canPop();
    boolean full();
    int getNumberOfBalls();

    int[] getGreenBallIndices();
    int[] getPurpleBallIndices();
    int[] getEmptyIndices();
    DecodeColorSensor.ArtifactColor getNextPopColor();

    void manualTransfer();
}
