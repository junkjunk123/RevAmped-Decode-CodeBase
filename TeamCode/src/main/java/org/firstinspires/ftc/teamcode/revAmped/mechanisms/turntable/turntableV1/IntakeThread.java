package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.util.Lockable;

import java.util.List;
import java.util.function.Supplier;

@Config
public class IntakeThread extends Lockable {
    private final ColorManager colorManager;
    private final IntakeUltrasonic intakeUltrasonic;
    public static int INTAKE_DELAY = 400;
    public final TableCompartmentManager.TableCompartment[] compartments;
    public List<Supplier<Vector2D>> sensors;

    public IntakeThread(ColorManager colorManager, IntakeUltrasonic intakeUltrasonic, TableCompartmentManager.TableCompartment[] compartments) {
        this.colorManager = colorManager;
        this.intakeUltrasonic = intakeUltrasonic;
        this.compartments = compartments;
    }

    @Override
    public void update() {
        if (intakeUltrasonic.hasArtifact()) {
            lock();

            switch (getEmptyIndices().length) {
                case 3 -> Scheduler.getInstance().add(new Delay(INTAKE_DELAY, () -> {
                    updateColors();
                    unlock();
                }));
                case 2 -> {
                    if (compartments[1].isNone()) {
                        Scheduler.getInstance().add(new Delay(() -> sensors.get(1).get().magnitude() > 0.1, () -> {
                            compartments[1].setColor(sensors.get(1).get());
                            unlock();
                        }));
                    }
                }
                case 1 -> compartments[0].setColor(new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3)));
                default -> {}
            }
        }
    }

    public int[] getEmptyIndices() {
        return java.util.stream.IntStream.range(0, compartments.length)
                .filter(i -> compartments[i].isNone())
                .toArray();
    }

    private void updateColors() {
        //Compartment 2
        if (compartments[1].isNone())
            compartments[1].setColor(sensors.get(1).get());

        //Compartment 3
        if (compartments[2].isNone())
            compartments[2].setColor(sensors.get(2).get());

        //Compartment 1
        if (!compartments[1].isNone() && !compartments[2].isNone() && intakeUltrasonic.hasArtifact())
            compartments[0].setColor(new Vector2D(Math.sqrt((double) 1 /3), Math.sqrt((double) 2 / 3)));
    }
}
