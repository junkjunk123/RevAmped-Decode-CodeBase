package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.TeleOpAction;

public class NewTeleOpStateHandler {
    public enum CycleState {
        INTAKE,
        NEUTRAL,
        SAMPLE_DEPO;

        public boolean canJump(CycleState next) {
            return Math.abs(this.ordinal() - next.ordinal()) == 1;
        }
    }

    private CycleState cycleState = CycleState.NEUTRAL;

    public static class ButtonMapper {
        private BooleanSupplier condition;
        private TeleOpAction action;

        public ButtonMapper(BooleanSupplier condition, TeleOpAction action) {
            this.condition = condition;
            this.action = action;
        }

        public BooleanSupplier getCondition() {
            return condition;
        }

        public TeleOpAction getAction() {
            return action;
        }

        public boolean update() {
            boolean run = condition.getAsBoolean();
            if (run) {
                action.run();
            } return run;
        }

        public boolean isCompleted() {
            return action.isCompleted();
        }
    }

    public interface StateDefiner {
        HashMap<ButtonMapper, CycleState> transitionMap(CycleState current);
    }

    public interface StateMap {
        StateDefiner get(CycleState cycleState);
    }

    public void setCycleState(CycleState cycleState) {
        if (this.cycleState.canJump(cycleState)) this.cycleState = cycleState;
    }

    private StateMap stateMap;
    private StateMap futureStateMap;
    private ConcurrentHashMap<ButtonMapper, CycleState> currentCycles = new ConcurrentHashMap<>();
    private Consumer<CycleState> updater;

    public void setUpdater(Consumer<CycleState> updater) {
        this.updater = updater;
    }

    public void setStateMap(StateMap stateMap) {
        this.stateMap = stateMap;
    }

    public void setFutureStateMap(StateMap futureStateMap) {
        this.futureStateMap = futureStateMap;
    }

    public void update() {
        updater.accept(cycleState);
    }

    public Consumer<CycleState> getUpdater() {
        return updater;
    }

    public CycleState getCycleState() {
        return cycleState;
    }

    public StateMap getFutureStateMap() {
        return futureStateMap;
    }

    public StateMap getStateMap() {
        return stateMap;
    }

    public ConcurrentHashMap<ButtonMapper, CycleState> getCurrentCycles() {
        return currentCycles;
    }

    public void setCurrentCycles(ConcurrentHashMap<ButtonMapper, CycleState> currentCycles) {
        this.currentCycles = currentCycles;
    }

    public void build(Scheduler scheduler) {
        setUpdater(cycleState -> {
            getStateMap().get(cycleState).transitionMap(cycleState).forEach((u, v) -> {
                if (u.update()) getCurrentCycles().put(u,v);
            });

            getStateMap().get(cycleState).transitionMap(cycleState).forEach((u, v) -> {
                if (u.update()) scheduler.add(new Delay(() -> this.cycleState == v, () -> getCurrentCycles().put(u,v)));
            });

            getCurrentCycles().forEach((u, v) -> {
                if (u.isCompleted()) {
                    setCycleState(v);
                    getCurrentCycles().remove(u);
                }
            });
        });
    }
}
