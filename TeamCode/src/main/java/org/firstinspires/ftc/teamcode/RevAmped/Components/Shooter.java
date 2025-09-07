package org.firstinspires.ftc.teamcode.RevAmped.Components;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwMotor2;

public class Shooter {
    public final Scheduler scheduler;
    public record HardwareComponents(HoodServo hoodServo) {

    }

    public static double FLYWHEEL_MAX_VELOCITY;

    public Shooter(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
