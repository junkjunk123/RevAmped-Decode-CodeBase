package org.firstinspires.ftc.teamcode.revAmped.components.scheduler;

import java.util.HashMap;

import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.DelayChain;

/**
 * DelayChainManager is a class that manages multiple DelayChains.
 * It allows you to add, update, clear, run, and check the completion status of DelayChains.
 */
public class DelayChainManager {
    /**
     * A map that holds DelayChains identified by a unique string name.
     */
    private final HashMap<String, DelayChain> chains = new HashMap<>();

    /**
     * Adds a DelayChain to the manager with a unique identifier.
     * @param chain the DelayChain to be added
     * @param name the unique identifier for the DelayChain
     */
    public void add(DelayChain chain, String name) {
        chains.put(name, chain);
    }

    /**
     * Updates all DelayChains in the manager.
     */
    public void update() {
        chains.forEach((s, d) -> {if (d.isActive()) d.update();});
    }

    /**
     * Clears all DelayChains from the manager.
     */
    public void clear() {
        chains.clear();
    }

    /**
     * Runs a DelayChain identified by its unique identifier.
     * @param identifier the unique identifier for the DelayChain to be run
     */
    public void run(String identifier) {
        DelayChain chain = chains.get(identifier);

        if (chain != null) {
            chain.run();
        } else {
            throw new IllegalArgumentException("No delay chain found with identifier: " + identifier);
        }
    }

    /**
     * Checks if a DelayChain identified by its unique identifier is currently active.
     * @param identifier the unique identifier for the DelayChain to check
     * @return true if the DelayChain is active, false otherwise
     */
    public boolean isFinished(String identifier) {
        DelayChain chain = chains.get(identifier);

        if (chain != null) {
            return chain.isCompleted();
        } else {
            throw new IllegalArgumentException("No delay chain found with identifier: " + identifier);
        }
    }
}