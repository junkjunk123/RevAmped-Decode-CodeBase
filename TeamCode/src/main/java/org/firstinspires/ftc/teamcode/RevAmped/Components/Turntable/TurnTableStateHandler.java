package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import java.util.function.BiConsumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class TurnTableStateHandler {
    public enum TurnTableInternalState {
        BALL1((hardwareComponents, scheduler) -> hardwareComponents.turntable().setCurrentState(TableServo.TableState.BALL1)),
        BALL2((hardwareComponents, scheduler) -> hardwareComponents.turntable().setCurrentState(TableServo.TableState.BALL2)),
        BALL3((hardwareComponents, scheduler) -> hardwareComponents.turntable().setCurrentState(TableServo.TableState.BALL3)),
        POP((hardwareComponents, scheduler) -> {
            hardwareComponents.popper().setCurrentState(Popper.PopperState.POP);
            scheduler.add(new Delay(500,() -> hardwareComponents.popper().setCurrentState(Popper.PopperState.NEUTRAL)));
        }),
        INTAKE((hardwareComponents, scheduler) -> {
                hardwareComponents.intakeMotor().setCurrentState(IntakeMotor.IntakeState.INTAKE);
        });
        private BiConsumer<TurnTable.HardwareComponents, Scheduler> initialize;

        TurnTableInternalState(BiConsumer<TurnTable.HardwareComponents, Scheduler> initialize){this.initialize = initialize;}

        public void initialize(TurnTable.HardwareComponents hardwareComponents, Scheduler scheduler){
            initialize.accept(hardwareComponents, scheduler);
        }
    }

    public interface TurnTableStateInterface {
        void initialize(TurnTable.HardwareComponents hardwareComponents, Scheduler scheduler);
    }

    public enum TurnTableCycleState {
        INTAKE(TurnTableInternalState.BALL1, TurnTableInternalState.BALL2, TurnTableInternalState.BALL3),
        POP(TurnTableInternalState.BALL1, TurnTableInternalState.BALL2, TurnTableInternalState.BALL3, TurnTableInternalState.POP);

        public TurnTableCycleState toggle(){
            return this == INTAKE ? POP : INTAKE;
        }

        private final TurnTableInternalState[] states;

        TurnTableCycleState(TurnTableInternalState... states) {
            this.states = states;
        }
    }

    public static class TurnTableState {
        private TurnTableCycleState cycleState;
        private TurnTableInternalState internalState;
        private final TurnTable.HardwareComponents hardware;
        private final Scheduler scheduler;

        public TurnTableState(TurnTableCycleState cycleState, TurnTableInternalState internalState, TurnTable.HardwareComponents hardware, Scheduler scheduler) {
            this.cycleState = cycleState;
            this.internalState = internalState;
            this.hardware = hardware;
            this.scheduler = scheduler;
            internalState.initialize(hardware, scheduler);
        }

        public void setCycleState(TurnTableCycleState newState) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState();
                internalState.initialize(hardware, scheduler);
            }
        }

        public void setCycleState(TurnTableCycleState newState, boolean init) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState();
                if (init) internalState.initialize(hardware, scheduler);
            }
        }

        public void setInternalState(TurnTableInternalState newState) {
            if (this.internalState != newState) {
                this.internalState = newState;
                switchCycleState();
                newState.initialize(hardware, scheduler);
            }
        }

        public void setInternalState(TurnTableInternalState newState, boolean init) {
            if (this.internalState != newState) {
                this.internalState = newState;
                switchCycleState();
                if (init) newState.initialize(hardware, scheduler);
            }
        }

        public TurnTableCycleState getCycleState() {
            return cycleState;
        }

        public TurnTableInternalState getInternalState() {
            return internalState;
        }

        private void switchInternalState() {
            for (TurnTableInternalState state : cycleState.states) {
                if (state == internalState) {
                    return;
                }
            }

            internalState = cycleState.states[0];
        }

        private void switchCycleState() {
            for (TurnTableInternalState state : cycleState.states) {
                if (state == internalState) {
                    return;
                }
            }

            cycleState = cycleState.toggle();
        }
    }
}
