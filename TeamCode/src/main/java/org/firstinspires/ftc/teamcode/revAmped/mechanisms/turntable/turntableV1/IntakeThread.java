package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.IntakeDistance;
import org.firstinspires.ftc.teamcode.revAmped.util.Locked;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Config
public class IntakeThread extends Locked {
    private final ColorManager colorManager;
    private final IntakeDistance intakeDistance;
    public static int INTAKE_DELAY;
    private final TableCompartmentManager.TableCompartment[] compartments;
    public List<Supplier<Vector2D>> sensors;
    private boolean intookBall;
    private final BiConsumer<Integer, TableCompartmentManager.TableCompartment[]> uponIntake;
    private TableCompartmentManager.TableCompartment[] transformedCompartments;
    private int curBall = 1;

    public IntakeThread(ColorManager colorManager, IntakeDistance intakeDistance, TableCompartmentManager.TableCompartment[] compartments, BiConsumer<Integer, TableCompartmentManager.TableCompartment[]> uponIntake) {
        this.colorManager = colorManager;
        this.intakeDistance = intakeDistance;
        this.compartments = compartments;
        this.uponIntake = uponIntake;

        sensors = List.of (
                () -> this.colorManager.getCompartmentZeroColor().amplitudes(),
                () -> this.colorManager.getCompartmentOneColor().amplitudes(),
                () -> this.colorManager.getCompartmentTwoColor().amplitudes()
        );

        transformedCompartments = compartments;
    }

    @Override
    public void update() {
        if (intakeDistance.hasArtifact()) {
            intookBall = true;
            lock();

            Scheduler.getInstance().add(new Delay(() -> sensors.get(curBall).get().magnitude() > 0.1, () -> {
                transformedCompartments[curBall].setColor(sensors.get(curBall).get());
                unlock();
                intookBall = false;
                uponIntake.accept(curBall, compartments);

                if (curBall != 0)
                    curBall = (curBall + 1) % 3;
            }));
        } else {
            intookBall = false;
        }
    }

    private void calcCurBall() {
        switch (getEmptyIndices().length) {
            case 2 -> curBall = 1;
            case 3 -> curBall = 2;
            default -> curBall = 0;
        }
    }

    public int[] getEmptyIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
                .filter(i -> compartments[i].isNone())
                .toArray();
    }

    private void updateColors(TableCompartmentManager.TableCompartment[] compartments) {
        //Compartment 2
        if (compartments[1].isNone())
            setColor(1, sensors.get(1).get());

        //Compartment 3
        if (compartments[2].isNone())
            setColor(2, sensors.get(2).get());

        //Compartment 1
        if (!compartments[1].isNone() && !compartments[2].isNone() && intakeDistance.hasArtifact())
            setColor(0, sensors.get(0).get());
    }

    public void updateColors(TurnTableInternals.TurnTableInternalState state) {
        transformedCompartments = getTransformation(state, compartments);
        updateColors(transformedCompartments);
    }

    public boolean intookBall() {
        return intookBall;
    }

    private void setColor(int compartmentIndex, Vector2D color) {
        compartments[compartmentIndex].setColor(color);
        uponIntake.accept(compartmentIndex, compartments);
    }

    public void startIntake(TurnTableInternals.TurnTableInternalState state) {
        unlock();

        transformedCompartments = new TableCompartmentManager.TableCompartment[3];

        for (int i = 0; i < 3; i++) {
            transformedCompartments[i] = compartments[(i + state.ordinal()) % 3];
        }

        calcCurBall();
    }

    public static TableCompartmentManager.TableCompartment[] getTransformation(TurnTableInternals.TurnTableInternalState state, TableCompartmentManager.TableCompartment[] compartments) {
        TableCompartmentManager.TableCompartment[] transformedCompartments = new TableCompartmentManager.TableCompartment[3];

        for (int i = 0; i < 3; i++) {
            transformedCompartments[i] = compartments[(i + state.ordinal()) % 3];
        }

        return transformedCompartments;
    }

    public TableCompartmentManager.TableCompartment[] getCompartments() {
        return compartments;
    }
}
