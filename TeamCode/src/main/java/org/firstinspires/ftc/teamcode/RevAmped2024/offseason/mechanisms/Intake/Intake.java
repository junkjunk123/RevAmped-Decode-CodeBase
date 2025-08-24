package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;

import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.Globals;

public class Intake {
    private final Scheduler scheduler;
    public final HslideMotor hslides;
    public final HSlideLockServo lockServo;
    public record Servos(IntakeArmServo arm, IntakeTiltServo tilt, IntakeWristServo wrist, IntakeTurretServo turret, IntakeClawServo claw) {
        public void update() {
            arm.update();
            tilt.update();
            wrist.update();
            turret.update();
            claw.update();
        }
    }
    public final Servos servos;
    private Globals.AllianceColor allianceColor;

    public enum IntakeState {
        INTAKE((servos, scheduler) -> {
            servos.arm().setCurrentState(IntakeArmServo.ArmState.INTAKE);
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.INTAKE);
            servos.turret().setCurrentState(IntakeTurretServo.TurretState.INTAKE);
            servos.claw().setCurrentState(IntakeClawServo.ClawState.OPEN);
            finishedIntaking = false;
        }),

        TRANSFER((servos, scheduler) -> {
            servos.arm().setCurrentState(IntakeArmServo.ArmState.TRANSFER);
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.TRANSFER);
            servos.wrist().setCurrentState(IntakeWristServo.WristState.TRANSFER);
            servos.turret().setCurrentState(IntakeTurretServo.TurretState.TRANSFER);
            servos.claw().setCurrentState(IntakeClawServo.ClawState.CLOSE);
            finishedIntaking = true;
        }),

        DROP_TO_HUMAN_PLAYER((servos, scheduler) -> {
            servos.arm().setCurrentState(IntakeArmServo.ArmState.DROP_TO_HUMAN_PLAYER);
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.DROP_TO_HUMAN_PLAYER);
            servos.wrist().setCurrentState(IntakeWristServo.WristState.DROP_TO_HUMAN_PLAYER);
            servos.turret().setCurrentState(IntakeTurretServo.TurretState.DROP_TO_HUMAN_PLAYER);
            servos.claw().setCurrentState(IntakeClawServo.ClawState.CLOSE);
            finishedIntaking = true;
        }),

        NEUTRAL_HIGHER((servos, scheduler) -> {
            servos.arm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_HIGHER);
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.NEUTRAL);
            servos.wrist().setCurrentState(IntakeWristServo.WristState.TRANSFER);
            servos.turret().setCurrentState(IntakeTurretServo.TurretState.TRANSFER);
        }),

        NEUTRAL_LOWER((servos, scheduler) -> {
            if (servos.arm().getCurrentState() != IntakeArmServo.ArmState.DROP_TO_HUMAN_PLAYER) servos.arm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_LOWER);
            else scheduler.add(new Delay(300, () -> servos.arm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_LOWER)));
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.NEUTRAL);
            servos.wrist().setCurrentState(IntakeWristServo.WristState.TRANSFER);
            servos.turret().setCurrentState(IntakeTurretServo.TurretState.TRANSFER);
            servos.claw().open();
        });

        private BiConsumer<Servos, Scheduler> initialize;

        IntakeState(BiConsumer<Servos, Scheduler> initialize) {
            this.initialize = initialize;
        }

        public void initialize(Servos servos, Scheduler scheduler) {
            initialize.accept(servos, scheduler);
        }
    }

    private static boolean finishedIntaking;
    private IntakeState currentState;

    public Intake(Scheduler scheduler, HardwareMap hardwareMap) {
        finishedIntaking = true;
        this.scheduler = scheduler;
        lockServo = new HSlideLockServo(hardwareMap,true);
        hslides = new HslideMotor(hardwareMap, lockServo);
        IntakeArmServo intakeArm = new IntakeArmServo(hardwareMap, "intakeArm", true);
        IntakeTiltServo intakeTilt = new IntakeTiltServo(hardwareMap, "intakeTilt", true);
        IntakeWristServo intakeWrist = new IntakeWristServo(hardwareMap, "intakeWrist", true);
        IntakeTurretServo intakeTurret = new IntakeTurretServo(hardwareMap, "intakeTurret", true);
        IntakeClawServo intakeClaw = new IntakeClawServo(hardwareMap, "intakeClaw", true);
        servos = new Servos(intakeArm, intakeTilt, intakeWrist, intakeTurret, intakeClaw);
        currentState = IntakeState.INTAKE;
    }

    public void setCurrentState(IntakeState currentState) {
        this.currentState = currentState;
        currentState.initialize(servos, scheduler);
    }

    public void setCurrentState(IntakeState currentState, boolean init) {
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
    }

    public boolean finishedIntaking() {
        return finishedIntaking;
    }

    public IntakeState getCurrentState() {
        return currentState;
    }

    public void clawGrabAuto() {
        scheduler.addOptionalDelay(currentState == IntakeState.INTAKE, new Delay(200, () -> {
            servos.claw.close();
            scheduler.add(new Delay(200, () -> setCurrentState(IntakeState.NEUTRAL_HIGHER)));
        }));

        if (getCurrentState() != IntakeState.INTAKE) {
            servos.arm().setCurrentState(IntakeArmServo.ArmState.INTAKE);
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.INTAKE);
            servos.claw().setCurrentState(IntakeClawServo.ClawState.OPEN);
            finishedIntaking = false;
        }
    }

    public void clawGrab() {
        scheduler.addOptionalDelay(currentState == IntakeState.INTAKE, new Delay(200, () -> {
            servos.claw.close();
            scheduler.add(new Delay(200, () -> setCurrentState(IntakeState.NEUTRAL_HIGHER)));
        }));
        if (getCurrentState() != IntakeState.INTAKE) setCurrentState(IntakeState.INTAKE);
    }

    public void update() {
        lockServo.update();
        hslides.update();
        servos.update();
    }

    public boolean correctColor(ITDColor.DetectionColor color) {
        return color == ITDColor.DetectionColor.Yellow || correctColorSpec(color);
    }

    public boolean correctColorSpec(ITDColor.DetectionColor color) {
        return (color == ITDColor.DetectionColor.Red && allianceColor == Globals.AllianceColor.Red) || (color == ITDColor.DetectionColor.Blue || allianceColor == Globals.AllianceColor.Blue);
    }
}
