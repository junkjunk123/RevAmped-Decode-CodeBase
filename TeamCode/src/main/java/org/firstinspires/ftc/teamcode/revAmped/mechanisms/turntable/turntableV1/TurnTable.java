package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.TeleOpStateHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.FiniteFieldVector;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Shooter;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ArtifactQueuer;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor.ArtifactColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableCycleState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableInternalState;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals.TurnTableState;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.DCTeleOpV2;
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
        ColorManager intakeColor = new ColorManager(hardwareMap);
        IntakeUltrasonic ultrasonic = new IntakeUltrasonic(hardwareMap, "intake_ultrasonic");
        hardware = new QueuerHardwareComponents(tableServo, popper, intakeMotor, intakeColor, ultrasonic);
        compartments = new TableCompartmentManager(
                hardware.intakeColor(),
                hardware.ultrasonic(),
                (ball, compartments) -> {},
                ball -> setCurrentState(TurnTableInternalState.values()[ball]),
                () -> {
                    switch (getCurrentCycleState()) {
                        case POP -> {
                            shooterStateConsumer.accept(Types.ShooterState.TRACKING);
                            intakeMotor.setCurrentState(Types.IntakeState.OUTTAKE);
                            scheduler.add(new Delay(bigIntakeDelay, intakeMotor::stop));
                        }
                        case INTAKE -> {
                            Robot.INSTANCE.flywheel().run(Flywheel.FLYWHEEL_MEDIUM_VELOCITY);
                            shooterStateConsumer.accept(Types.ShooterState.REST);
                            hardware().turntable().resetToRelativeRotation();
                        }
                    }

                    setCycleState(getCurrentState().getCycleState().toggle());
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
            shootOne(() -> {}, forwardRotation);
        } else {
            shootAll(() -> {}, forwardRotation, getNumberOfBalls());
        }
    }

    private void shootOne(Runnable andThen, boolean forwardRotation) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.75f);
        Robot.INSTANCE.updateLock();
        if (forwardRotation) hardware.turntable().setNextState();
        else hardware.turntable().setPreviousState();
        setCycleState(TurnTableCycleState.POP);
        scheduler.add(new Delay(hardware.turntable()::reached, () -> scheduler.add(new Delay(300, () -> {
            compartments.pop(true);
            andThen.run();
        }))));
        scheduler.add(Robot.INSTANCE.lockTillLaunchedAll(hardware.intakeMotor()::stop));
    }

    private double computeRotation(int numberOfBalls) {
        return numberOfBalls / 3.0 + 1.0 / 3.0;
    }

    private void shootAll(Runnable andThen, boolean forwardRotation, int numBalls) {
        hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE, 0.75f);
        if (forwardRotation) hardware.turntable().absoluteRotate(computeRotation(numBalls));
        else hardware.turntable().absoluteRotate(-computeRotation(numBalls));
        setCycleState(TurnTableCycleState.POP);
        Robot.INSTANCE.updateLock();
        scheduler.add(new Delay(hardware.turntable()::reached, () -> {
            scheduler.add(new Delay(800, () -> {
                for (int j = 0; j < numBalls; j++) {
                    compartments.pop(false);
                    andThen.run();
                }
                hardware.intakeMotor().stop();
            }));
        }));
    }

    @Override
    public void launch(boolean all, boolean forwardRotation, TeleOpStateHandler<DCTeleOpV2.CycleState> tsh) {
        DCTeleOpV2.CycleState cycleState = tsh.nextState();
        tsh.runTransition(TeleOpStateHandler.TransitionCommand.fromRunnable(() -> {
            if (!all) {
                int i = getNumberOfBalls();
                if (i == 0) return;

                if (i == 1) {
                    tsh.runTransition(t -> shootOne(() -> t.accept(true), forwardRotation), DCTeleOpV2.CycleState.INTAKING, 800);
                } else {
                    if (cycleState == DCTeleOpV2.CycleState.SHOOTING) {
                        tsh.task(() -> shootOne(() -> {}, forwardRotation), FiniteFieldVector.binaryVectorOf(0, 0, 1));
                    } else if (cycleState == DCTeleOpV2.CycleState.DRIVING_TO_SHOOTING) {
                        tsh.runTransition(t -> shootOne(() -> t.accept(true), forwardRotation), DCTeleOpV2.CycleState.SHOOTING, 800);
                    }
                }
            } else {
                int i = getNumberOfBalls();
                if (i == 0) return;

                tsh.runTransition(t -> shootAll(() -> t.accept(true), forwardRotation, i), DCTeleOpV2.CycleState.INTAKING, 3500);
            }
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