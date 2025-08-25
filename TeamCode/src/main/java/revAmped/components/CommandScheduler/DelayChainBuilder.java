package revAmped.components.CommandScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DelayChainBuilder {
    private final String identifier;
    private final List<RunnableTask> delays = new ArrayList<>();

    public DelayChainBuilder(String identifier) {
        this.identifier = identifier;
    }

    public DelayChainBuilder() {
        this.identifier = null;
    }

    public DelayChainBuilder addCustom(RunnableTask action) {
        delays.add(action);
        return this;
    }

    public DelayChainBuilder run(Command command) {
        return addCustom(command);
    }

    public DelayChainBuilder schedule(Delay delay) {
        return addCustom(delay);
    }

    public DelayChainBuilder wait(double millis) {
        return addCustom(new Delay(millis, () -> {}));
    }

    public DelayChainBuilder waitUntil(BooleanSupplier condition) {
        return addCustom(new Delay(condition, () -> {}));
    }

    public DelayChain build() {
        if (identifier != null) {
            return new DelayChain(identifier, delays.toArray(new RunnableTask[0]));
        } else {
            return new DelayChain(delays.toArray(new RunnableTask[0]));
        }
    }
}
