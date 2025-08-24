package revAmped.components.PedroEnhancements;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class AEABuilder {
    public static AutonomousEnumeratedAction createAction(Runnable init, Consumer<Runnable> loop, BooleanSupplier finishedDriving, AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                init.run();
            }

            @Override
            protected void loop() {
                loop.accept(() -> completed = true);
            }

            @Override
            public boolean finishedDriving() {
                return finishedDriving.getAsBoolean();
            }

            @Override
            public NextStateGenerator nextStateGenerator() {
                return nextStateGenerator;
            }
        };
    }

    public static AutonomousEnumeratedAction createAction(Runnable init, Consumer<Runnable> loop, BooleanSupplier finishedDriving, AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator, String identifier) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                init.run();
            }

            @Override
            protected void loop() {
                loop.accept(() -> completed = false);
            }

            @Override
            public boolean finishedDriving() {
                return finishedDriving.getAsBoolean();
            }

            @Override
            public NextStateGenerator nextStateGenerator() {
                return nextStateGenerator;
            }

            @Override
            public String identifier() {
                return identifier;
            }
        };
    }

    public static AutonomousEnumeratedAction createAction(Runnable init, Consumer<Runnable> loop, BooleanSupplier finishedDriving, String identifier) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                init.run();
            }

            @Override
            protected void loop() {
                loop.accept(() -> completed = true);
            }

            @Override
            public boolean finishedDriving() {
                return finishedDriving.getAsBoolean();
            }

            @Override
            public String identifier() {
                return identifier;
            }
        };
    }

    public static AutonomousEnumeratedAction createAction(Runnable init, Consumer<Runnable> loop, BooleanSupplier finishedDriving) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {
                init.run();
            }

            @Override
            protected void loop() {
                loop.accept(() -> completed = true);
            }

            @Override
            public boolean finishedDriving() {
                return finishedDriving.getAsBoolean();
            }
        };
    }

    public Runnable init;
    public Consumer<Runnable> loop;
    public BooleanSupplier finishedDriving;
    public AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator = i -> i+1;
    public String identifier = "";

    public AEABuilder init(Runnable init) {
        this.init = init;
        return this;
    }

    public AEABuilder loop(Consumer<Runnable> loop) {
        this.loop = loop;
        return this;
    }

    public AEABuilder finishedDriving(BooleanSupplier finishedDriving) {
        this.finishedDriving = finishedDriving;
        return this;
    }

    public AEABuilder nextStateGenerator(AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator) {
        this.nextStateGenerator = nextStateGenerator;
        return this;
    }

    public AEABuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public AutonomousEnumeratedAction build() {
        return AEABuilder.createAction(init, loop, finishedDriving, nextStateGenerator, identifier);
    }
}
