package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

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
        private final TableCompartmentManager compartmentManager;

        public TurnTableState(TurnTableCycleState cycleState, TurnTableInternalState internalState, Types.QueuerHardwareComponents hardware, Scheduler scheduler, TableCompartmentManager compartmentManager) {
            this.cycleState = cycleState;
            this.internalState = internalState;
            this.hardware = hardware;
            this.scheduler = scheduler;
            this.compartmentManager = compartmentManager;
            initAction();
        }

        public void setCycleState(TurnTableCycleState newState) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState(true);
                cycleStateInitAction();
            }
        }

        public void setCycleState(TurnTableCycleState newState, boolean init) {
            if (this.cycleState != newState) {
                this.cycleState = newState;
                switchInternalState(init);
                if (init) cycleStateInitAction();
            }
        }

        public void setInternalState(TurnTableInternalState newState) {
            if (this.internalState != newState) {
                this.internalState = newState;
                initAction();
                switchInternalCompartmentState();
            }
        }

        public void setInternalState(TurnTableInternalState newState, boolean init) {
            if (this.internalState != newState) {
                this.internalState = newState;
                if (init) initAction();
                switchInternalCompartmentState();
            }
        }

        public void switchInternalState(boolean init) {
            if (cycleState == TurnTableCycleState.POP) {
                int nextPopIndex = compartmentManager.isMotifMode() && Globals.randomizationState != null ? compartmentManager.getNextMotifPopIndex() : compartmentManager.findNextPopIndex();
                setInternalState(TurnTableInternalState.values()[nextPopIndex], init);
            } else if (cycleState == TurnTableCycleState.INTAKE) {
                int nextIntakeIndex = compartmentManager.findNextIntakeIndex();
                setInternalState(TurnTableInternalState.values()[nextIntakeIndex], init);
            }
        }

        public TurnTableCycleState getCycleState() {
            return cycleState;
        }

        public TurnTableInternalState getInternalState() {
            return internalState;
        }

        private void initAction() {
            switch (internalState) {
                case BALL1 -> hardware.turntable().setCurrentState(Types.TableState.BALL1);
                case BALL2 -> hardware.turntable().setCurrentState(Types.TableState.BALL2);
                case BALL3 -> hardware.turntable().setCurrentState(Types.TableState.BALL3);
            }
        }

        private void cycleStateInitAction() {
            if (cycleState == TurnTableCycleState.POP) {
                hardware.intakeMotor().stop();
            }
        }

        private void switchInternalCompartmentState() {
            compartmentManager.setCurrentIndex(internalState.ordinal());
        }
    }
}
