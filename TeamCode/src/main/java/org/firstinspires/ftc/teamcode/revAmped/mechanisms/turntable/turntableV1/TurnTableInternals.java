package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public final class TurnTableInternals {
    public enum TurnTableInternalState {
        BALL1,
        BALL2,
        BALL3
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
                //int nextPopIndex = compartmentManager.isMotifMode() ? compartmentManager.getNextMotifPopIndex() : compartmentManager.findNextPopIndex();
                //setInternalState(TurnTableInternalState.values()[nextPopIndex], init);
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
                case BALL1 -> hardware.tableServo().setCurrentState(Types.TableState2.BALL1);
                case BALL2 -> hardware.tableServo().setCurrentState(Types.TableState2.BALL2);
                case BALL3 -> hardware.tableServo().setCurrentState(Types.TableState2.BALL3);
            }
        }

        private void cycleStateInitAction() {
            if (cycleState == TurnTableCycleState.INTAKE) {
                //turns off intake when resetting turntable & popper
                hardware.intakeMotor().setCurrentState(Types.IntakeState.STOP);
                //delay until turntable resets position
                scheduler.add(new Delay(hardware.tableServo()::reached, () -> scheduler.add(new Delay(500, () -> {
                    //retract popper
                    hardware.popper().setCurrentState(Types.PopperState.NEUTRAL);
                    compartmentManager.startIntake(internalState);
                    //turns intake back on after popper & turnable is reset
                    hardware.intakeMotor().setCurrentState(Types.IntakeState.INTAKE);
                }))).setMaxTimeToRun(3500));
            }
        }

        private void switchInternalCompartmentState() {
            compartmentManager.setCurrentIndex(internalState.ordinal());

            if (cycleState == TurnTableCycleState.INTAKE) {
                compartmentManager.startIntake(internalState);
            }
        }
    }
}
