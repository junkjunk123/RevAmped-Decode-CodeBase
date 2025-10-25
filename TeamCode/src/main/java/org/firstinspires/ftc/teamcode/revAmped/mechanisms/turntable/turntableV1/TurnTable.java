package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ArtifactQueuer;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor.ArtifactColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableState;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.QueuerHardwareComponents;

import java.util.Arrays;
import java.util.function.Consumer;

public class TurnTable implements ArtifactQueuer {
    public static int smallIntakeDelay;
    public static int bigIntakeDelay;
    private final Scheduler scheduler;
    public TableCompartmentManager compartments;
    public final QueuerHardwareComponents hardware;
    private final TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap, Consumer<Types.ShooterState> shooterStateConsumer, HwVoltageSensor voltageSensor) {
        this.scheduler = scheduler;
        IntakeMotor intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, voltageSensor);
        TableServo tableServo = new TableServo(hardwareMap, Types.TableState.REST, HwCRServo.Encoder.external(intakeMotor));
        Popper popper = new Popper(hardwareMap,setPositions);
        IntakeColor intakeColor = new IntakeColor(hardwareMap, "color_left", 0d);
        hardware = new QueuerHardwareComponents(tableServo, popper, intakeMotor, intakeColor);
        compartments = new TableCompartmentManager(
                ball -> {
                    intakeMotor.setCurrentState(Types.IntakeState.OUTTAKE);
                    setCurrentState(TurnTableInternalState.values()[ball]);
                    scheduler.add(new Delay(smallIntakeDelay, () -> {
                        hardware.intakeColor().disable();
                        intakeMotor.stop();
                        scheduler.add(new Delay(() -> Math.abs(tableServo.getLastError()) < 50 || (tableServo.getVelocity() < 10 && Math.abs(tableServo.getLastError()) < 500), () -> {
                            intake();
                            DecodeColorSensor.enableSensors();
                        }).setMaxTimeToRun(2200));
                    }));
                },
                ball -> setCurrentState(TurnTableInternalState.values()[ball]),
                () -> {
                    setCycleState(getCurrentState().getCycleState().toggle());
                    compartments.resetPopHistory();

                    switch (getCurrentCycleState()) {
                        case POP -> {
                            shooterStateConsumer.accept(Types.ShooterState.TRACKING);
                            intakeMotor.setCurrentState(Types.IntakeState.OUTTAKE);
                            scheduler.add(new Delay(bigIntakeDelay, intakeMotor::stop));
                        }
                        case INTAKE -> Robot.INSTANCE.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
                    }
                }
        );
        currentState = new TurnTableState(TurnTableCycleState.INTAKE, TurnTableInternalState.BALL1, hardware, scheduler, compartments);
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
        compartments.update();

        if (currentState.getCycleState() == TurnTableCycleState.INTAKE && hardware.intakeMotor().getCurrentState() == Types.IntakeState.INTAKE) compartments.intake(hardware.intakeColor().getColor());
    }

    @Override
    public boolean isMotifMode() {
        return compartments.isMotifMode();
    }

    public void pop() {
        setCycleState(TurnTableCycleState.POP);
        hardware.popper().setCurrentState(Types.PopperState.POP);
        scheduler.add(new Delay(() -> hardware.popper().reached(Types.PopperVoltageState.FULLY_UP), () -> scheduler.add(new Delay(150, () -> {
            hardware.popper().setCurrentState(Types.PopperState.NEUTRAL);
            scheduler.add(new Delay(() -> hardware().popper().reached(Types.PopperVoltageState.FULLY_DOWN),
                    () -> scheduler.add(new Delay(300, compartments::pop))
            ));
        }))));
    }

    public void switchStateToPop() {
        setCycleState(TurnTableCycleState.POP);
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

    public Vector2D[] getBallColors() {
        return Arrays.stream(compartments.getCompartments())
                .map(TableCompartmentManager.TableCompartment::getColor)
                .toArray(org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D[]::new);
    }

    public void prepareGreen() {
        compartments.prepareGreenForPop();
        setCycleState(TurnTableCycleState.POP);
    }

    public void preparePurple() {
        compartments.preparePurpleForPop();
        setCycleState(TurnTableCycleState.POP);
    }

    public void manualTransfer() {
        compartments.manualTransfer();
    }

    public void correctJam() {
        compartments.correctJam();
    }

    public void incrementPopIndex() {
        compartments.incrementPopIndex();
    }

    public void decrementPopIndex() {
        compartments.decrementPopIndex();
    }

    @Override
    public void populateAndShoot() {
        compartments.populateAndShoot();
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

    public boolean currentIndexHasArtifact() {
        return !compartments.currentCompartment().isNone();
    }

    public void fillWithPurple() {
        compartments.fillWithPurple();
    }
}