package org.firstinspires.ftc.teamcode.revAmped.components.architecture;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * AEABuilder is a utility class for creating instances of AutonomousEnumeratedAction.
 */
public class AEABuilder {
    /**
     * Creates an AutonomousEnumeratedAction with the specified parameters.
     * @param init the initialization Runnable to be executed once when the action starts
     * @param loop the loop Consumer that accepts a Runnable to mark the action as completed
     * @param finishedDriving a BooleanSupplier that indicates whether the action is finished driving
     * @param nextStateGenerator a NextStateGenerator that generates the next state of the action
     * @return
     */
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
            public boolean isFinishedDriving() {
                return finishedDriving.getAsBoolean();
            }

            @Override
            public NextStateGenerator nextStateGenerator() {
                return nextStateGenerator;
            }
        };
    }

    /**
     * Creates an AutonomousEnumeratedAction with the specified parameters.
     * @param init the initialization Runnable to be executed once when the action starts
     * @param loop the loop Consumer that accepts a Runnable to mark the action as completed
     * @param finishedDriving a BooleanSupplier that indicates whether the action is finished driving
     * @param nextStateGenerator a NextStateGenerator that generates the next state of the action
     * @param identifier a unique identifier for the action
     * @return the created AutonomousEnumeratedAction
     */
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
            public boolean isFinishedDriving() {
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

/**
     * Creates an AutonomousEnumeratedAction with the specified parameters.
     * @param init the initialization Runnable to be executed once when the action starts
     * @param loop the loop Consumer that accepts a Runnable to mark the action as completed
     * @param finishedDriving a BooleanSupplier that indicates whether the action is finished driving
     * @param identifier a unique identifier for the action
     * @return the created AutonomousEnumeratedAction
     */
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
            public boolean isFinishedDriving() {
                return finishedDriving.getAsBoolean();
            }

            @Override
            public String identifier() {
                return identifier;
            }
        };
    }

/**
     * Creates an AutonomousEnumeratedAction with the specified parameters.
     * @param init the initialization Runnable to be executed once when the action starts
     * @param loop the loop Consumer that accepts a Runnable to mark the action as completed
     * @param finishedDriving a BooleanSupplier that indicates whether the action is finished driving
     * @return the created AutonomousEnumeratedAction
     */
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
            public boolean isFinishedDriving() {
                return finishedDriving.getAsBoolean();
            }
        };
    }

    public Runnable init;
    public Consumer<Runnable> loop;
    public BooleanSupplier finishedDriving;
    public AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator = i -> i+1;
    public String identifier = "";

    /**
     * Initializes the AEABuilder with a Runnable that will be executed once when the action starts.
     * @param init the initialization Runnable to be executed once when the action starts
     * @return the AEABuilder instance for method chaining
     */
    public AEABuilder init(Runnable init) {
        this.init = init;
        return this;
    }

    /**
     * Sets the loop Consumer that accepts a Runnable to mark the action as completed.
     * @param loop the loop Consumer that accepts a Runnable to mark the action as completed
     * @return the AEABuilder instance for method chaining
     */
    public AEABuilder loop(Consumer<Runnable> loop) {
        this.loop = loop;
        return this;
    }

    /**
     * Sets the BooleanSupplier that indicates whether the action is finished driving.
     * @param finishedDriving a BooleanSupplier that indicates whether the action is finished driving
     * @return the AEABuilder instance for method chaining
     */
    public AEABuilder finishedDriving(BooleanSupplier finishedDriving) {
        this.finishedDriving = finishedDriving;
        return this;
    }

    /**
     * Sets the NextStateGenerator that generates the next state of the action.
     * @param nextStateGenerator a NextStateGenerator that generates the next state of the action
     * @return the AEABuilder instance for method chaining
     */
    public AEABuilder nextStateGenerator(AutonomousEnumeratedAction.NextStateGenerator nextStateGenerator) {
        this.nextStateGenerator = nextStateGenerator;
        return this;
    }

    /**
     * Sets a unique identifier for the action.
     * @param identifier a unique identifier for the action
     * @return the AEABuilder instance for method chaining
     */
    public AEABuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * Builds and returns an AutonomousEnumeratedAction with the specified parameters.
     * @return the created AutonomousEnumeratedAction
     */
    public AutonomousEnumeratedAction build() {
        return AEABuilder.createAction(init, loop, finishedDriving, nextStateGenerator, identifier);
    }

    public static AutonomousEnumeratedAction drive(Function<FollowerManager, Boolean> finishedDriving) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {

            }

            @Override
            protected void loop() {
                if (finishedDriving())
                    completed = true;
            }

            @Override
            public boolean isFinishedDriving() {
                return finishedDriving.apply(Globals.followerManager);
            }
        };
    }

    public static AutonomousEnumeratedAction drive(Function<FollowerManager, Boolean> finishedDriving, String identifier) {
        return new AutonomousEnumeratedAction() {
            @Override
            protected void init() {

            }

            @Override
            protected void loop() {

            }

            @Override
            public boolean isFinishedDriving() {
                return finishedDriving.apply(Globals.followerManager);
            }

            @Override
            public String identifier() {
                return identifier;
            }
        };
    }
}
