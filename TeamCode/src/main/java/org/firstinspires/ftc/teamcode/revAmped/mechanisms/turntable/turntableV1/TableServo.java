package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwSwitch;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Util.DelayedResult;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TableState;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Config
public class TableServo extends HwCRServo2<TableState> {
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    public static PIDFCoefficients SINGLE_SERVO_COEFFICIENTS;
    private final PIDFController controller;
    public static int BALL1;
    public static int BALL2;
    public static int BALL3;
    public static int TICKS_PER_ROTATION;
    private final Differentiator velocityComputer;
    private int finetuneTicks = 0;
    private int absoluteRotationTarget = 0;
    public static DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;
    public float mult = 1f;
    public final HwSwitch limitSwitch;

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
        statePowerMap.put(TableState.ABSOLUTE_ROTATION, () -> mult * (float)  MathFunctions.clamp(controller.runWithError(absoluteRotationTarget - getCurrentPosition()), -1, 1));
    }

    public TableServo(HardwareMap hardwareMap, TableState state, Encoder encoder) throws IllegalArgumentException{
        super(hardwareMap, "table", direction, state, encoder);
        this.limitSwitch = new HwSwitch(hardwareMap, "table_switch", true, true);
        controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
        resetPosition();
        velocityComputer = new Differentiator(encoder);
    }

    @Override
    public void update() {
        limitSwitch.update();
        super.update();
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
        if (getCurrentState() == TableState.ABSOLUTE_ROTATION) return super.getCurrentPosition();
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
    public void resetPosition(int currentPos) {
        finetuneTicks = 0;
        super.resetPosition(currentPos);
    }

    @Override
    public DelayedResult<Boolean> testResponse(Consumer<String> telemetry) {
        absoluteRotate(2.0/3.0);
        AtomicBoolean reset = new AtomicBoolean(false);
        BooleanSupplier timeout1 = MathUtil.booleanSupplierfromTimeDelay(2500);
        BooleanSupplier timeout2 = MathUtil.booleanSupplierfromTimeDelay(5000);
        AtomicBoolean passed = new AtomicBoolean(false);
        AtomicBoolean passedSwitch = new AtomicBoolean(false);

        return new DelayedResult<>(() -> {
            if (!reset.get()) {
                if (!passed.get() && reached()) {
                    passed.set(true);
                }

                if (timeout1.getAsBoolean()) {
                    reset.set(true);
                    resetToRelativeRotation();
                }
                return null;
            } else if (!passedSwitch.get() && limitSwitch.isTouch()) {
                passedSwitch.set(true);
                telemetry.accept("table switch -> " + passedSwitch.get());
                return passed.get();
            } else if (!timeout2.getAsBoolean()) {
                return null;
            } else {
                telemetry.accept("table switch -> " + passedSwitch.get());
                return passed.get();
            }
        });
    }

    public boolean reached() {
        return (getVelocity() < 10 && Math.abs(getLastError()) < 500) || Math.abs(getLastError()) < 50;
    }

    public boolean stopped() {
        return (getVelocity() < 10 && Math.abs(getLastError()) < 500);
    }

    public void setNextState() {
        List<TableState> values = Arrays.asList(TableState.relativeStates());
        setCurrentState(values.get((values.indexOf(servoState) + 1) % values.size()));
    }

    public void setPreviousState() {
        List<TableState> values = Arrays.asList(TableState.relativeStates());
        setCurrentState(values.get((values.indexOf(servoState) - 1 + values.size()) % values.size()));
    }

    public void absoluteRotate(double rotations) {
        setCurrentState(TableState.ABSOLUTE_ROTATION);
        int currentPos = super.getCurrentPosition();
        absoluteRotationTarget = currentPos + (int) (TICKS_PER_ROTATION * rotations);
    }

    private int getClosestFullRotation(int currentPos) {
        int revolutions = currentPos / TICKS_PER_ROTATION;
        if (currentPos <= (revolutions + 0.5) * TICKS_PER_ROTATION)
            return revolutions * TICKS_PER_ROTATION;
        return (revolutions + 1) * TICKS_PER_ROTATION;
    }

    private int nextFullRotation(int currentPos) {
        int revolutions = currentPos / TICKS_PER_ROTATION;
        return (revolutions + 1) * TICKS_PER_ROTATION;
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
        setCurrentState(TableState.ABSOLUTE_ROTATION);
        setSplitState(SplitTableServo.SplitState.RESETTING);
        absoluteRotationTarget = nextFullRotation(super.getCurrentPosition());
        Scheduler.getInstance().add(new Delay(limitSwitch::isTouch, () -> {
                resetPosition(absoluteRotationTarget);
                setCurrentState(TableState.BALL1);
                setSplitState(SplitTableServo.SplitState.USING_ONE);
        }).setCancellation(1500, () -> {setCurrentState(TableState.BALL1); setSplitState(SplitTableServo.SplitState.USING_ONE);}));
    }

    public int getAbsoluteRotationTarget() {
        return absoluteRotationTarget;
    }

    public void updatePIDFCoefficients(PIDFCoefficients coefficients) {
        controller.updateConstants(coefficients);
    }

    public void setAbsoluteRotationTarget(int absoluteRotationTarget) {
        setCurrentState(TableState.ABSOLUTE_ROTATION);
        this.absoluteRotationTarget = absoluteRotationTarget;
    }

    public void setMult(float mult) {
        this.mult = mult;
    }

    public SplitTableServo.SplitState getSplitState() {return SplitTableServo.SplitState.USING_BOTH;}

    public void setSplitState(SplitTableServo.SplitState splitState) {}

    public float getMult() {
        return mult;
    }

    public void updateF(double feedforward) {
        TABLE_PIDF_COEFFICIENTS.F = feedforward;
    }


}