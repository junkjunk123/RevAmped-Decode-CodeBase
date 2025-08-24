package revAmped.components.Misc;

import revAmped.components.PedroEnhancements.AutonomousActionExecutor;
import revAmped.components.PedroEnhancements.AutonomousEnumeratedAction;

public abstract class RevAmpedAutoTest extends RevAmpedOpMode {
    public AutonomousEnumeratedAction action;

    @Override
    public void RevAmpedInit() {
        action.update();
    }

    @Override
    public void RevAmpedLoop() {
        action.update();
    }

    public abstract void buildAutonomousEnumeratedAction();
}
