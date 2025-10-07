package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.TurntableV1;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.util.Types.TableState;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;

public class TableServo extends HwCRServo2 <TableState> {
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    private final PIDFController controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
    public static int BALL1;
    public static int BALL2;
    public static int BALL3;
    public static int TICKS_PER_ROTATION;

    @Override
    public int ticksPerRotation() {
        return TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TableState.REST, ServoPower.constant(0f));
        statePowerMap.put(TableState.BALL1, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL1)), -1, 1));
        statePowerMap.put(TableState.BALL2, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL2)), -1, 1));
        statePowerMap.put(TableState.BALL3, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL3)), -1, 1));
    }

    public TableServo(HardwareMap hardwareMap, TableState state, Encoder encoder) throws IllegalArgumentException{
        super(hardwareMap, "table", DcMotorSimple.Direction.FORWARD, state, encoder);
        resetPosition();
    }

    @Override
    public void setCurrentState(TableState servoState) {
        if (getCurrentState() != servoState) controller.reset();
        super.setCurrentState(servoState);
    }

    @Override
    public double getLastError() {
        return controller.getError();
    }
}