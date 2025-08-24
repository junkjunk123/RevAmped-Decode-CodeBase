package revAmped.components.PedroEnhancements.Tree;

import java.util.function.BooleanSupplier;

public interface Node {
    default boolean isComplete() {
        return true;
    }
    void run();
    void update();
    default boolean isDrive() {return false;}
    default BooleanSupplier checkComplete() {
        return this::isComplete;
    }
    default int nextState(int state) {
        return state+1;
    }
}
