package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.TeleOpStateHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DCTeleOpV2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public interface ArtifactQueuer {
    void preparePurple();
    void prepareGreen();
    Vector2D[] getBallColors();
    DecodeColorSensor.ArtifactColor[] getPredictedBallColors();
    void intake();
    void update();
    boolean isMotifMode();
    void setMotifMode(boolean mode);

    void launch(boolean slow, TeleOpStateHandler<DCTeleOpV2.CycleState> tsh, Runnable action);

    void switchStateToPop();

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
    TurnTableInternals.TurnTableState getCurrentState();
    boolean currentIndexHasArtifact();
    void populateAndShoot();
    void incrementPopIndex();
    void decrementPopIndex();
    void fillWithPurple();
    void updateColors();
}
