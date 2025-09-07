package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.IntakeColor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableStateHandler.TurnTableState;

public class TurnTable {
    private final Scheduler scheduler;
    private TableCompartmentManager compartments;

    public record HardwareComponents(TableServo turntable, Popper popper, IntakeMotor intakeMotor, IntakeColor intakeColor) {
        public void update() {
            intakeColor.update();
            intakeMotor.update();
            turntable.update();
            popper.update();
        }
    }

    public final HardwareComponents hardware;

    private final TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap){
        this.scheduler = scheduler;
        TableServo turntable = new TableServo(hardwareMap, setPositions ? TableServo.TableState.BALL1 : TableServo.TableState.REST);
        Popper popper = new Popper(hardwareMap,setPositions);
        IntakeColor intakeColor = new IntakeColor(hardwareMap,Color.NEUTRAL);
        IntakeMotor intakeMotor = new IntakeMotor(hardwareMap, IntakeMotor.IntakeState.STOP);
        hardware = new HardwareComponents(turntable,popper, intakeMotor, intakeColor);
        currentState = new TurnTableState(TurnTableCycleState.INTAKE, TurnTableInternalState.BALL1, hardware, scheduler);
        compartments = new TableCompartmentManager(
                ball -> {
                    setCycleState(TurnTableCycleState.INTAKE);
                    setCurrentState(TurnTableInternalState.values()[ball]);
                },
                ball -> {
                    setCycleState(TurnTableCycleState.POP);
                    setCurrentState(TurnTableInternalState.values()[ball]);
                },
                () -> {
                    setCycleState(currentState.getCycleState().toggle());
                    compartments.resetPopHistory();
                }
        );
    }

    public void setCurrentState(TurnTableInternalState currentState) {
        if (this.currentState.getInternalState() == currentState) return;
        this.currentState.setInternalState(currentState);
    }

    public void setCurrentState(TurnTableInternalState currentState, boolean init){
        if (this.currentState.getInternalState() == currentState) return;
        this.currentState.setInternalState(currentState, init);
    }

    public void setCycleState(TurnTableCycleState newState){
        if (newState != currentState.getCycleState()) {
            currentState.setCycleState(newState);
            compartments.resetPopHistory();
        }
    }

    public void setCycleState(TurnTableCycleState newState, boolean init){
        if (newState != currentState.getCycleState()) {
            currentState.setCycleState(newState, init);
            compartments.resetPopHistory();
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

        if (currentState.getCycleState() == TurnTableCycleState.INTAKE) compartments.intake(hardware.intakeColor.getColor());
    }

    public void pop() {
        setCycleState(TurnTableCycleState.POP);
        setCurrentState(TurnTableInternalState.POP);
        scheduler.add(new Delay(500, compartments::pop));
    }

    public void intake() {
        setCycleState(TurnTableCycleState.INTAKE);
        setCurrentState(TurnTableInternalState.INTAKE);
    }
}