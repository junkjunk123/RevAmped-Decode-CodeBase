package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import revAmped.components.CommandScheduler.Commands.Delay;
import revAmped.components.CommandScheduler.Scheduler;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor.Color;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableInternals.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableInternals.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTableInternals.TurnTableState;
import java.util.Arrays;

public class TurnTable {
    private final Scheduler scheduler;
    public TableCompartmentManager compartments;

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
                    intake();
                    setCurrentState(TurnTableInternalState.values()[ball]);
                },
                ball -> {
                    setCycleState(TurnTableCycleState.POP);
                    setCurrentState(TurnTableInternalState.values()[ball]);
                    hardware.intakeMotor.stop();
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

    private void setCycleState(TurnTableCycleState newState){
        if (newState != currentState.getCycleState()) {
            currentState.setCycleState(newState);
            compartments.resetPopHistory();
        }
    }

    private void setCycleState(TurnTableCycleState newState, boolean init){
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

    public void update() {
        hardware.update();

        if (currentState.getCycleState() == TurnTableCycleState.INTAKE) compartments.intake(hardware.intakeColor.getColor());
    }

    public void pop() {
        setCycleState(TurnTableCycleState.POP);
        hardware.popper().setCurrentState(Popper.PopperState.POP);
        scheduler.add(new Delay(Popper.POP_TIME_MILLIS, () -> {
            hardware.popper().setCurrentState(Popper.PopperState.NEUTRAL);
            compartments.pop();
        }));
    }

    public void intake() {
        setCycleState(TurnTableCycleState.INTAKE);
        hardware.intakeMotor().setCurrentState(IntakeMotor.IntakeState.INTAKE);
    }

    public void toggleMotifMode() {
        compartments.setMotifMode(!compartments.isMotifMode());
    }

    public void setMotifMode(boolean mode) {
        compartments.setMotifMode(mode);
    }

    public Color[] getBallColors() {
        return Arrays.stream(compartments.getCompartments())
                .map(TableCompartmentManager.TableCompartment::getColor)
                .toArray(Color[]::new);
    }

    public void prepareGreen() {
        compartments.prepareGreenForPop();
    }

    public void preparePurple() {
        compartments.preparePurpleForPop();
    }

    public boolean canPop() {
        return compartments.getEmptyIndices().length == 3;
    }

    public boolean full() {
        return compartments.getEmptyIndices().length == 0;
    }
}