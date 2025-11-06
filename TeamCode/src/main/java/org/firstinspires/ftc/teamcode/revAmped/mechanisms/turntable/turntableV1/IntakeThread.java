package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;
import com.acmerobotics.dashboard.config.Config;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.util.Lockable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Config
public class IntakeThread extends Lockable {
    private final ColorManager colorManager;
    private final IntakeUltrasonic intakeUltrasonic;
    public static int INTAKE_DELAY;
    private final TableCompartmentManager.TableCompartment[] compartments;
    public List<Supplier<Vector2D>> sensors;
    private boolean intookBall;
    private final BiConsumer<Integer, TableCompartmentManager.TableCompartment[]> uponIntake;
    private TableCompartmentManager.TableCompartment[] transformedCompartments;

    public IntakeThread(ColorManager colorManager, IntakeUltrasonic intakeUltrasonic, TableCompartmentManager.TableCompartment[] compartments, BiConsumer<Integer, TableCompartmentManager.TableCompartment[]> uponIntake) {
        this.colorManager = colorManager;
        this.intakeUltrasonic = intakeUltrasonic;
        this.compartments = compartments;
        this.uponIntake = uponIntake;

        sensors = List.of(
                () -> new Vector2D(Math.sqrt((double) 1 / 3), Math.sqrt((double) 2 / 3)),
                () -> this.colorManager.getCompartmentOneColor().amplitudes(),
                () -> this.colorManager.getCompartmentTwoColor().amplitudes()
        );
    }

    @Override
    public void update() {
        if (intakeUltrasonic.hasArtifact()) {
            intookBall = true;
            lock();

            switch (getEmptyIndices().length) {
                case 3 -> Scheduler.getInstance().add(new Delay(INTAKE_DELAY, () -> {
                    updateColors(transformedCompartments);
                    unlock();
                    intookBall = false;
                }));
                case 2 -> {
                    if (transformedCompartments[1].isNone()) {
                        Scheduler.getInstance().add(new Delay(() -> sensors.get(1).get().magnitude() > 0.1, () -> {
                            transformedCompartments[1].setColor(sensors.get(1).get());
                            unlock();
                            intookBall = false;
                            uponIntake.accept(1, compartments);
                        }));
                    } else {
                        Scheduler.getInstance().add(new Delay(() -> sensors.get(2).get().magnitude() > 0.1, () -> {
                            transformedCompartments[2].setColor(sensors.get(2).get());
                            unlock();
                            intookBall = false;
                            uponIntake.accept(2, compartments);
                        }));
                    }
                }
                case 1 -> {
                    setColor(0, new Vector2D(Math.sqrt((double) 1 / 3), Math.sqrt((double) 2 / 3)));
                    intookBall = false;
                }
                default -> {}
            }
        } else {
            intookBall = false;
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
        if (!compartments[1].isNone() && !compartments[2].isNone() && intakeUltrasonic.hasArtifact())
            setColor(0, new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3)));
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
    }
}
