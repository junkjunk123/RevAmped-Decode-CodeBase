package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1;
import com.qualcomm.robotcore.hardware.HardwareMap;
import revAmped.components.CommandScheduler.Commands.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ArtifactQueuer;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor.ArtifactColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TurnTableInternals.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TurnTableInternals.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TurnTableInternals.TurnTableState;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types.QueuerHardwareComponents;

import java.util.Arrays;

public class TurnTable implements ArtifactQueuer {
    private final Scheduler scheduler;
    public TableCompartmentManager compartments;

    public final QueuerHardwareComponents hardware;

    private final TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap) {
        this.scheduler = scheduler;
        TableServo turntable = new TableServo(hardwareMap, setPositions ? Types.TableState.BALL1 : Types.TableState.REST);
        Popper popper = new Popper(hardwareMap,setPositions);
        DualIntakeColor intakeColor = new DualIntakeColor(hardwareMap);
        IntakeMotor intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP);
        hardware = new QueuerHardwareComponents(turntable,popper, intakeMotor, intakeColor);
        currentState = new TurnTableState(TurnTableCycleState.INTAKE, TurnTableInternalState.BALL1, hardware, scheduler);
        compartments = new TableCompartmentManager(
                ball -> {
                    intake();
                    setCurrentState(TurnTableInternalState.values()[ball]);
                },
                ball -> {
                    setCycleState(TurnTableCycleState.POP);
                    setCurrentState(TurnTableInternalState.values()[ball]);
                    hardware.intakeMotor().stop();
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

        if (currentState.getCycleState() == TurnTableCycleState.INTAKE) compartments.intake(hardware.intakeColor().getColor());
    }

    @Override
    public boolean isMotifMode() {
        return compartments.isMotifMode();
    }

    public void pop() {
        setCycleState(TurnTableCycleState.POP);
        hardware.popper().setCurrentState(Types.PopperState.POP);
        scheduler.add(new Delay(Popper.POP_TIME_MILLIS, () -> {
            hardware.popper().setCurrentState(Types.PopperState.NEUTRAL);
            compartments.pop();
        }));
    }

    public void intake() {
        setCycleState(TurnTableCycleState.INTAKE);
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
    }

    public void toggleMotifMode() {
        compartments.setMotifMode(!compartments.isMotifMode());
    }

    @Override
    public QueuerHardwareComponents hardware() {
        return hardware;
    }

    public void setMotifMode(boolean mode) {
        compartments.setMotifMode(mode);
    }

    public ArtifactColor[] getBallColors() {
        return Arrays.stream(compartments.getCompartments())
                .map(TableCompartmentManager.TableCompartment::getColor)
                .toArray(ArtifactColor[]::new);
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

    @Override
    public int getNumberOfBalls() {
        return 3 - compartments.getEmptyIndices().length;
    }

    public int[] getGreenBallIndices() {
        return compartments.getGreenBallIndices();
    }

    public int[] getPurpleBallIndices() {
        return compartments.getPurpleBallIndices();
    }

    public int[] getEmptyIndices() {
        return compartments.getEmptyIndices();
    }

    public ArtifactColor getNextPopColor() {
        return compartments.getNextPopColor();
    }
}