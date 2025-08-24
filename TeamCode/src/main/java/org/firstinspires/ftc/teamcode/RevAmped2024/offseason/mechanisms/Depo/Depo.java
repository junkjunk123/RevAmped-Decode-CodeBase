package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.VslideMotor;

import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class Depo {
    private final Scheduler scheduler;
    public VslideMotor vslides;

    public Depo(Scheduler scheduler, HardwareMap hardwareMap) {
        this.scheduler = scheduler;
        vslides = new VslideMotor(hardwareMap);
        DepoArmServo armServo = new DepoArmServo(hardwareMap, "depoArm", true);
        DepoClawServo clawServo = new DepoClawServo(hardwareMap, "depoClaw",true);
        DepoMGNServo mgnServo = new DepoMGNServo(hardwareMap, "depoMGN",true);
        servos = new Servos(armServo, mgnServo, clawServo);
        currentState = DepoState.TRANSFER;
    }

    public record Servos(DepoArmServo arm, DepoMGNServo mgn, DepoClawServo claw) {
        public void update() {
            arm.update();
            mgn.update();
            claw.update();
        }
    }
    public final Servos servos;

    public enum DepoState {
        TRANSFER((servos, scheduler) -> {
            servos.arm().setCurrentState(DepoArmServo.ArmState.TRANSFER);
            servos.claw().setCurrentState(DepoClawServo.ClawState.OPEN);
            servos.mgn().setCurrentState(DepoMGNServo.MGNState.TRANSFER);
        }),
        BASKET_DEPO((servos, scheduler) -> {
            servos.arm().setCurrentState(DepoArmServo.ArmState.BASKET_DEPO);
            servos.mgn().setCurrentState(DepoMGNServo.MGNState.BASKET_DEPO);
            servos.claw().setCurrentState(DepoClawServo.ClawState.CLOSED);
        }),
        SPEC_INTAKE((servos, scheduler) -> {
            servos.arm().setCurrentState(DepoArmServo.ArmState.SPEC_INTAKE);
            servos.mgn().setCurrentState(DepoMGNServo.MGNState.SPEC_INTAKE);
            servos.claw().setCurrentState(DepoClawServo.ClawState.OPEN);
        }),
        SPEC_DEPO((servos, scheduler) -> {
            servos.arm().setCurrentState(DepoArmServo.ArmState.SPEC_DEPO);
            servos.claw().setCurrentState(DepoClawServo.ClawState.CLOSED);
            scheduler.add(new Delay(700, () -> servos.mgn().setCurrentState(DepoMGNServo.MGNState.SPEC_DEPO)));
        });

        private final BiConsumer<Servos, Scheduler> initialize;

        DepoState(BiConsumer<Servos, Scheduler> initialize) {
            this.initialize = initialize;
        }

        public void initialize(Servos servos, Scheduler scheduler) {
            initialize.accept(servos, scheduler);
        }
    }

    private DepoState currentState;

    public void update() {
        vslides.update();
        servos.update();
    }

    public void setCurrentState(DepoState currentState) {
        this.currentState = currentState;
        currentState.initialize(servos, scheduler);
    }

    public void setCurrentState(DepoState currentState, boolean init) {
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
    }

    public DepoState getCurrentState() {
        return currentState;
    }
}
