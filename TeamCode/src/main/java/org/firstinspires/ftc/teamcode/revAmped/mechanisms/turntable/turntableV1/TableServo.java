package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TableState;

public class TableServo extends HwCRServo2<TableState> {
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    private final PIDFController controller;
    public static int BALL1;
    public static int BALL2;
    public static int BALL3;
    public static int TICKS_PER_ROTATION;
    private final Differentiator velocityComputer;

    @Override
    public int ticksPerRotation() {
        return TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TableState.REST, ServoPower.constant(0f));
        statePowerMap.put(TableState.BALL1, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL1)), -1, 1));
        statePowerMap.put(TableState.BALL2, () -> {
                double error = getError(BALL2);
                return (float) MathFunctions.clamp(controller.runWithError(error), -1, 1);
        });
        statePowerMap.put(TableState.BALL3, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL3)), -1, 1));
    }

    public TableServo(HardwareMap hardwareMap, TableState state, Encoder encoder) throws IllegalArgumentException{
        super(hardwareMap, "table", DcMotorSimple.Direction.REVERSE, state, encoder);
        controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
        resetPosition();
        velocityComputer = new Differentiator(encoder);
        //reverse();
    }

    @Override
    public void setCurrentState(TableState servoState) {
        if (getCurrentState() != servoState) {
            controller.reset();
            velocityComputer.reset();
        }
        super.setCurrentState(servoState);
    }

    @Override
    public double getLastError() {
        return controller.getError();
    }

    @Override
    public int getCurrentPosition() {
        return super.getCurrentPosition() % TICKS_PER_ROTATION;
    }

    public double getVelocity() {
        return velocityComputer.calculate();
    }

    @Override
    public void reverse() {
        int temp = BALL2;
        BALL2 = BALL3;
        BALL3 = temp;
    }
}