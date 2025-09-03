package org.firstinspires.ftc.teamcode.UG.Depo;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.UG.Intake.Intake;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.Globals;

public class Depo {
    private final Scheduler scheduler;
    public final FlywheelMotorLeft flywheelMotorLeft;
    public final FlywheelMotorRight flywheelMotorRight;

    public record Servos(GateServo gate){
        public void update(){
            gate.update();
        }
    }
    public final Servos servos;
    private Globals.AllianceColor allianceColor;

    public enum DepoState{
        LAUNCH((servos,scheduler) -> {
            servos.gate().setCurrentState(GateServo.GateState.OPEN);
        }),
        NEUTRAL((servos, scheduler) -> {
            servos.gate().setCurrentState(GateServo.GateState.CLOSE);
        });


        private BiConsumer<Servos,Scheduler> initialize;

        DepoState(BiConsumer<Servos,Scheduler> initialize){
            this.initialize = initialize;
        }

        public void initialize(Servos servos, Scheduler scheduler){
            initialize.accept(servos, scheduler);
        }
    }

    private DepoState currentState;

    public Depo(Scheduler scheduler, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        GateServo gate = new GateServo(hardwareMap, "",true);
        flywheelMotorRight = new FlywheelMotorRight(hardwareMap, FlywheelMotorRight.FlywheelMotorState.STOPPED); //Check with havish
        flywheelMotorLeft = new FlywheelMotorLeft(hardwareMap, FlywheelMotorLeft.FlywheelMotorState.STOPPED); //Check with havish
        servos = new Servos(gate);
        currentState = DepoState.NEUTRAL;
    }

    public void setCurrentState(Depo.DepoState currentState){
        this.currentState = currentState;
        currentState.initialize(servos, scheduler);
    }

    public void setCurrentState(DepoState currentState, boolean init){
        this.currentState = currentState;
        if (init) currentState.initialize(servos, scheduler);
    }

    public DepoState getCurrentState(){
        return currentState;
    }

    public void update(){
        servos.update();
        flywheelMotorLeft.update();
        flywheelMotorRight.update();
    }
}
