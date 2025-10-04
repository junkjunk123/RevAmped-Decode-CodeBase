package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.MathUtil.Vector2D;

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
