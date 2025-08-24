package revAmped.components.CommandScheduler;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class CommandBuilder {
    private Scheduler scheduler;

    public CommandBuilder(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public interface RunnableComponent {

    }

    public interface ConditionalComponent extends RunnableComponent {

    }

    @FunctionalInterface
    public interface Action extends RunnableComponent, Runnable {
        void run();
    }

    @FunctionalInterface
    public interface Condition extends RunnableComponent, BooleanSupplier, ConditionalComponent {
        boolean getAsBoolean();
    }

    @FunctionalInterface
    public interface MillisAmount extends RunnableComponent, ConditionalComponent {
        double getMillis();
    }

    private ArrayList<RunnableComponent> components = new ArrayList<>();

    public CommandBuilder addNode(Action action) {
       components.add(action);
       return this;
    }

    public CommandBuilder waitMillis(double millis) {
        components.add((MillisAmount) () -> millis);
        return this;
    }

    public CommandBuilder waitUntil(Condition condition) {
        components.add(condition);
        return this;
    }

    public Runnable build() {
        Runnable result = () -> {};
        Runnable current = () -> {};
        ConditionalComponent currentCondition = null;
        for (int i = 0; i < components.size(); i++) {
            RunnableComponent component = components.get(i);

            if (i == components.size() - 1) {
                if (component instanceof Action) {
                    Runnable finalCurrent = current;
                    current = () -> {
                        finalCurrent.run();
                        ((Action) component).run();
                    };
                }

                result = updateResult(result, current, currentCondition);
                current = () -> {};
            } else if (component instanceof Action) {
                Runnable finalCurrent = current;
                current = () -> {
                    finalCurrent.run();
                    ((Action) component).run();
                };
            } else if (component instanceof ConditionalComponent) {
                result = updateResult(result, current, currentCondition);
                current = () -> {};
                currentCondition = (ConditionalComponent) component;
            }
        }

        components = new ArrayList<>();
        return result;
    }

    private Runnable updateResult(Runnable result, Runnable current, ConditionalComponent currentCondition) {
        if (currentCondition == null) {
            Runnable finalResult = result;
            result = () -> {
                finalResult.run();
                current.run();
            };
        } else {
            Runnable finalResult1 = result;
            result = () -> {
                finalResult1.run();

                if (currentCondition instanceof Condition) {
                    scheduler.add(new Delay(((Condition) currentCondition), current));
                } else if (currentCondition instanceof MillisAmount) {
                    scheduler.add(new Delay(((MillisAmount) currentCondition).getMillis(), current));
                }
            };
        }

        return result;
    }
}
