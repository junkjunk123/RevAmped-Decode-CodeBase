package revAmped.components.CommandScheduler.Commands;

import java.util.function.BooleanSupplier;

public class OptionalDelay extends Delay{
    public OptionalDelay(boolean runInstantly, BooleanSupplier condition, Runnable action) {
        super(condition, action);

        if (runInstantly) run();
    }

    public OptionalDelay(boolean runInstantly, double wait, Runnable action) {
        super(wait, action);

        if (runInstantly) run();
    }
}
