package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;
import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.IntakeColor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableState;

public class TurnTable {
    private final Scheduler scheduler;
    private HashMap <TurnTableInternalState, IntakeColor.Color> tableMap = new HashMap<>();

    public record HardwareComponents(TableServo turntable, Popper popper, IntakeMotor intakeMotor, IntakeColor intakeColor) {
        public void update() {
            intakeColor.update();
            intakeMotor.update();
            turntable.update();
            popper.update();
        }
    }

    public final HardwareComponents hardware;

    private TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        TableServo turntable = new TableServo(hardwareMap, setPositions ? TableServo.TableState.BALL1 : TableServo.TableState.REST);
        Popper popper = new Popper(hardwareMap,setPositions);
        IntakeColor intakeColor = new IntakeColor(hardwareMap,Color.NEUTRAL);
        IntakeMotor intakeMotor = new IntakeMotor(hardwareMap, IntakeMotor.IntakeState.STOP);
        hardware = new HardwareComponents(turntable,popper, intakeMotor, intakeColor);
        currentState = new TurnTableState(TurnTableCycleState.INTAKE, TurnTableInternalState.BALL1, hardware, scheduler);
    }

    public void setCurrentState(TurnTableInternalState currentState) {
        this.currentState.setInternalState(currentState);
        currentState.initialize(hardware,scheduler);
    }

    public void setCurrentState(TurnTableInternalState currentState, boolean init){
        this.currentState.setInternalState(currentState);
        if (init) currentState.initialize(hardware, scheduler);
    }

    public void setCycleState(TurnTableCycleState newState){
        if (newState != currentState.getCycleState()) {
            currentState.setCycleState(newState);
            currentState.getInternalState().initialize(hardware, scheduler);
        }
    }

    public void setCycleState(TurnTableCycleState newState, boolean init){
        if (newState != currentState.getCycleState()) {
            currentState.setCycleState(newState);
            if (init) currentState.getInternalState().initialize(hardware, scheduler);
        }
    }

    public TurnTableState getCurrentState(){
        return currentState;
    }

    public TurnTableInternalState getCurrentInternalState(){
        return currentState.getInternalState();
    }

    public TurnTableCycleState getCurrentCycleState(){
        return currentState.getCycleState();
    }

    public void update(){
        hardware.update();
    }
}