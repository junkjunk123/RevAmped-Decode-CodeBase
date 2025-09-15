package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import revAmped.components.CommandScheduler.Scheduler;

public final class TurnTableInternals {
    public enum TurnTableInternalState {
        BALL1,
        BALL2,
        BALL3;
    }

    public interface TurnTableStateInterface {
        void initialize(Types.QueuerHardwareComponents hardwareComponents, Scheduler scheduler);
    }

    public enum TurnTableCycleState {
        INTAKE(TurnTableInternalState.BALL1, TurnTableInternalState.BALL2, TurnTableInternalState.BALL3),
        POP(TurnTableInternalState.BALL1, TurnTableInternalState.BALL2, TurnTableInternalState.BALL3);

        public TurnTableCycleState toggle() {
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
        private final Types.QueuerHardwareComponents hardware;
        private final Scheduler scheduler;

        public TurnTableState(TurnTableCycleState cycleState, TurnTableInternalState internalState, Types.QueuerHardwareComponents hardware, Scheduler scheduler) {
            this.cycleState = cycleState;
            this.internalState = internalState;
            this.hardware = hardware;
            this.scheduler = scheduler;
            initAction();
        }

        public void setCycleState(TurnTableCycleState newState) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState();
                initAction();
            }
        }

        public void setCycleState(TurnTableCycleState newState, boolean init) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState();
                if (init) initAction();
            }
        }

        public void setInternalState(TurnTableInternalState newState) {
            if (this.internalState != newState) {
                this.internalState = newState;
                switchCycleState();
                initAction();
            }
        }

        public void setInternalState(TurnTableInternalState newState, boolean init) {
            if (this.internalState != newState) {
                this.internalState = newState;
                switchCycleState();
                if (init) initAction();
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

        private void initAction() {
            switch (getInternalState()) {
                case BALL1 -> hardware.turntable().setCurrentState(Types.TableState.BALL1);
                case BALL2 -> hardware.turntable().setCurrentState(Types.TableState.BALL2);
                case BALL3 -> hardware.turntable().setCurrentState(Types.TableState.BALL3);
            }
        }
    }
}
