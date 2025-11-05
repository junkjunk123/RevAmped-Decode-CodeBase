package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.util.Name;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedSupplier;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TableState;

import java.util.function.BooleanSupplier;

public class TableServo extends HwCRServo2<TableState> {
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    private final PIDFController controller;
    public static int BALL1;
    public static int BALL2;
    public static int BALL3;
    public static int TICKS_PER_ROTATION;
    private final Differentiator velocityComputer;
    private int finetuneTicks = 0;
    private int absoluteRotationTarget = 0;

    @Override
    public int ticksPerRotation() {
        return TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TableState.REST, ServoPower.constant(0f));
        statePowerMap.put(TableState.BALL1, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL1 + finetuneTicks)), -1, 1));
        statePowerMap.put(TableState.BALL2, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL2 + finetuneTicks)), -1, 1));
        statePowerMap.put(TableState.BALL3, () -> (float) MathFunctions.clamp(controller.runWithError(getError(BALL3 + finetuneTicks)), -1, 1));
        statePowerMap.put(TableState.ABSOLUTE_ROTATION, () -> (float) MathFunctions.clamp(controller.runWithError(absoluteRotationTarget - getCurrentPosition()), -1, 1));
    }

    public TableServo(HardwareMap hardwareMap, TableState state, Encoder encoder) throws IllegalArgumentException{
        super(hardwareMap, "table", DcMotorSimple.Direction.REVERSE, state, encoder);
        controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
        resetPosition();
        velocityComputer = new Differentiator(encoder);
    }

    @Override
    public void setCurrentState(TableState servoState) {
        if (getCurrentState() != servoState) {
            controller.reset();
            velocityComputer.reset();
            finetuneTicks = 0;
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

    @Override
    public double getVelocity() {
        return velocityComputer.calculate();
    }

    @Override
    public void reverse() {
        int temp = BALL2;
        BALL2 = BALL3;
        BALL3 = temp;
    }

    @Override
    public void finetune(int ticks) {
        finetuneTicks += ticks;
    }

    @Override
    public void resetPosition() {
        finetuneTicks = 0;
        super.resetPosition();
    }

    @Override
    public DelayedResult<Boolean> testResponse() {
        setCurrentState(TableState.BALL2);
        BooleanSupplier delay = () -> (Math.abs(getLastError()) < 50 || (getVelocity() < 10 && Math.abs(getLastError()) < 500));
        return new DelayedSupplier<>(delay, delay::getAsBoolean).setMaxTime(1500).get();
    }

    public boolean reached() {
        return getVelocity() < 10 && Math.abs(getLastError()) < 100;
    }

    public void setNextState() {
        TableState[] values = Types.TableState.values();
        setCurrentState(values[(servoState.ordinal() + 1) % values.length]);
    }

    public void setPreviousState() {
        TableState[] values = Types.TableState.values();
        int index = (servoState.ordinal() - 1) % values.length;
        if (index < 0)
            index += values.length;
        setCurrentState(values[index]);
    }

    public void absoluteRotate(double rotations) {
        setCurrentState(TableState.ABSOLUTE_ROTATION);
        absoluteRotationTarget = getCurrentPosition() + (int) (TICKS_PER_ROTATION * rotations);
    }

    public Types.TableState closestState(int pos) {
        int dist1 = Math.abs(pos - TableState.BALL1.getPosition());
        int dist2 = Math.abs(pos - TableState.BALL2.getPosition());
        int dist3 = Math.abs(pos - TableState.BALL3.getPosition());

        int min = Math.min(Math.min(dist1, dist2), dist3);
        if (min == dist1) return TableState.BALL1;
        else if (min == dist2) return TableState.BALL2;
        else return TableState.BALL3;
    }

    public void resetToRelativeRotation() {
        int normalizedTarget = absoluteRotationTarget % TICKS_PER_ROTATION;
        if (normalizedTarget < 0) normalizedTarget += TICKS_PER_ROTATION;
        setCurrentState(closestState(normalizedTarget));
    }
}