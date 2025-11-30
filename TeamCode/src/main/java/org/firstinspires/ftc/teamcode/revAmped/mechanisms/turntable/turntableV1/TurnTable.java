package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.TeleOpStateHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ArtifactQueuer;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor.ArtifactColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableState;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DCTeleOpV2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.QueuerHardwareComponents;

import java.util.Arrays;
import java.util.function.Consumer;

public class TurnTable implements ArtifactQueuer {
    public static int SMALL_INTAKE_DELAY;
    public static int BIG_INTAKE_DELAY;
    private final Scheduler scheduler;
    public TableCompartmentManager compartments;
    public final QueuerHardwareComponents hardware;
    private final TurnTableState currentState;

    public TurnTable(Scheduler scheduler, boolean setPositions, HardwareMap hardwareMap, Consumer<Types.ShooterState> shooterStateConsumer, HwVoltageSensor voltageSensor) {
        this.scheduler = scheduler;
        IntakeMotor intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, voltageSensor);
        TableServoV2 tableServo = new TableServoV2(hardwareMap, setPositions, Types.TableState2.BALL2, Encoder.external(intakeMotor));
        Popper popper = new Popper(hardwareMap, setPositions);
        ColorManager intakeColor = new ColorManager(hardwareMap);
        IntakeUltrasonic ultrasonic = new IntakeUltrasonic(hardwareMap);
        hardware = new QueuerHardwareComponents(tableServo, popper, intakeMotor, intakeColor, ultrasonic);
        compartments = new TableCompartmentManager(
                hardware.intakeColor(),
                hardware.intakeDistance(),
                (ball, compartments) -> {},
                ball -> setCurrentState(TurnTableInternalState.values()[ball]),
                () -> {
                    TurnTableCycleState cycleState = getCurrentState().getCycleState().toggle();
                    switch (cycleState) {
                        case POP -> shooterStateConsumer.accept(Types.ShooterState.TRACKING);
                        case INTAKE -> {
                            Robot.INSTANCE.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
                            shooterStateConsumer.accept(Types.ShooterState.REST);
                            hardware().tableServo().resetToNeutral();
                        }
                    }
                    setCycleState(cycleState);
                    compartments.resetPopHistory();
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

    private void setCycleState(TurnTableCycleState newState) {
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

        if (currentState.getCycleState() == TurnTableCycleState.INTAKE
                && hardware.intakeMotor().getCurrentState() == Types.IntakeState.INTAKE)
            compartments.intake();
    }

    @Override
    public boolean isMotifMode() {
        return compartments.isMotifMode();
    }

    public void launch(boolean all, boolean forwardRotation) {
        if (!all) {
            shootAllSlow(() -> {}, getNumberOfBalls());
        } else {
            shootAll(() -> {}, getNumberOfBalls());
        }
    }

    private void shootAllSlow(Runnable andThen, int numBalls) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.4f);
        Types.TableState2[] shootSequence = hardware.tableServo().getShootStates();
        hardware.tableServo().setCurrentState(shootSequence[0]);
        setCycleState(TurnTableCycleState.POP);
        Robot.INSTANCE.updateLock();
        scheduler.add(new Delay(hardware.tableServo()::reached, () -> {scheduler.add(new Delay(250, () -> {
            hardware.tableServo().setCurrentState(shootSequence[1]);
            hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.5f);
            scheduler.add(new Delay(hardware.tableServo()::reached, () -> {scheduler.add(new Delay(250, () -> {
                hardware.tableServo().setCurrentState(shootSequence[2]);
                hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.5f);
                scheduler.add(new Delay(hardware.tableServo()::reached, () -> {
                    for (int j = 0; j < numBalls; j++) {
                        compartments.pop(false);
                    }
                    compartments.toggleCycleState();
                    andThen.run();
                }).setMaxTimeToRun(2500));
            })); hardware.intakeMotor().stop();}).setMaxTimeToRun(1000));
        })); hardware.intakeMotor().stop();}).setMaxTimeToRun(1000));
    }

    private double computeRotation(int numberOfBalls) {
        return numberOfBalls / 3.0 + 1.0 / 3.0;
    }

    private void shootAll(Runnable andThen, int numBalls) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.4f);
        hardware.tableServo().fullRotation();
        setCycleState(TurnTableCycleState.POP);
        Robot.INSTANCE.updateLock();
        scheduler.add(new Delay(hardware.tableServo()::reached, () -> {
            for (int j = 0; j < numBalls; j++) {
                compartments.pop(false);
            }
            compartments.toggleCycleState();
            andThen.run();
        }).setMaxTimeToRun(3500));
    }

    public void shootAllAuto(Runnable andThen, double flywheelDelay) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.4f);
        Types.TableState2[] shootSequence = hardware.tableServo().getShootStates();
        hardware.tableServo().setCurrentState(shootSequence[0]);
        scheduler.add(new Delay(hardware.tableServo()::reached, () -> {scheduler.add(new Delay(flywheelDelay, () -> {
            hardware.tableServo().setCurrentState(shootSequence[1]);
            hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.5f);
            scheduler.add(new Delay(hardware.tableServo()::reached, () -> {scheduler.add(new Delay(flywheelDelay, () -> {
                hardware.tableServo().setCurrentState(shootSequence[2]);
                hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.5f);
                scheduler.add(new Delay(hardware.tableServo()::reached, () -> {
                    hardware.tableServo().resetToNeutral();
                    andThen.run();
                }).setMaxTimeToRun(2500));
            })); hardware.intakeMotor().stop();}).setMaxTimeToRun(1000));
        })); hardware.intakeMotor().stop();}).setMaxTimeToRun(1000));
    }

    public void shootAllAuto(Runnable andThen) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.4f);
        hardware.tableServo().fullRotation();
        scheduler.add(new Delay(hardware.tableServo()::reached, () -> {
            hardware.tableServo().resetToNeutral();
            scheduler.add(new Delay(hardware.tableServo()::reached, andThen).setMaxTimeToRun(500));
        }).setMaxTimeToRun(3500));
    }

    @Override
    public void launch(boolean slow, TeleOpStateHandler<DCTeleOpV2.CycleState> tsh, Runnable action) {
        tsh.runTransition(TeleOpStateHandler.TransitionCommand.fromRunnable(() -> {
            int i = getNumberOfBalls();

            if (slow)
                tsh.runTransition(t -> shootAllSlow(() -> {
                    t.accept(true);
                    action.run();
                }, i), DCTeleOpV2.CycleState.INTAKE, 3500);
            else
                tsh.runTransition(t -> shootAll(() -> {
                    t.accept(true);
                    action.run();
                }, i), DCTeleOpV2.CycleState.INTAKE, 3500);
        }), DCTeleOpV2.CycleState.SHOOTING);
    }

    public void switchStateToPop() {
        setCycleState(TurnTableCycleState.POP);
        hardware().popper().setCurrentState(Types.PopperState.POP);
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
                .toArray(Vector2D[]::new);
    }

    @Override
    public ArtifactColor[] getPredictedBallColors() {
        return Arrays.stream(compartments.getCompartments())
                .map(TableCompartmentManager.TableCompartment::getGuessedColor)
                .toArray(ArtifactColor[]::new);
    }

    public static ArtifactColor[] getPredictedBallColors(TableCompartmentManager.TableCompartment[] compartments) {
        return Arrays.stream(compartments)
                .map(TableCompartmentManager.TableCompartment::getGuessedColor)
                .toArray(ArtifactColor[]::new);
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
        return !compartments.isEmpty();
    }

    public boolean full() {
        return compartments.isFull();
    }

    public boolean isEmpty() {
        return compartments.isEmpty();
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

    public void updateColors() {
        compartments.getIntakeThread().updateColors(currentState.getInternalState());
    }

    public TableCompartmentManager getCompartments() {
        return compartments;
    }
}