package org.firstinspires.ftc.teamcode.UG.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.Globals;

public class Intake {
    private final Scheduler scheduler;
    public final IntakeMotor intakeTop;
    public final IntakeMotor intakeBottom;

    public record Servos(IntakeTiltServo tilt){
        public void update(){
            tilt.update();
        }
    }
    public final Servos servos;
    private Globals.AllianceColor allianceColor;

    public enum IntakeState{
        INTAKE((servos,scheduler) -> {
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.DOWN);
        }),
        NEUTRAL((servos, scheduler) -> {
            servos.tilt().setCurrentState(IntakeTiltServo.TiltState.DOWN);
        });


        private BiConsumer<Servos,Scheduler> initialize;

        IntakeState(BiConsumer<Servos,Scheduler> initialize){
            this.initialize = initialize;
        }

        public void initialize(Servos servos, Scheduler scheduler){
            initialize.accept(servos, scheduler);
        }
    }

    private IntakeState currentState;

    public Intake(Scheduler scheduler, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        IntakeTiltServo intakeTilt = new IntakeTiltServo(hardwareMap, "",true);
        intakeTop = new IntakeMotor(hardwareMap, IntakeMotor.IntakeMotorState.STOPPED); //Check with havish
        intakeBottom = new IntakeMotor(hardwareMap, IntakeMotor.IntakeMotorState.STOPPED); //Check with havish
        servos = new Servos(intakeTilt);
        currentState = IntakeState.NEUTRAL;
    }

    public void setCurrentState(IntakeState currentState){
        this.currentState = currentState;
        currentState.initialize(servos, scheduler);
    }

    public void setCurrentState(IntakeState currentState, boolean init){
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
    }

    public IntakeState getCurrentState(){
        return currentState;
    }

    public void update(){
        servos.update();
        intakeTop.update();
        intakeBottom.update();
    }
}
