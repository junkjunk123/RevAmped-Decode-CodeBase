package revAmped.components.PedroEnhancements.Tree;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.MathUtil.RevAmpedTimer;

public class CommandTreeBuilder {
    private ArrayList<Node> nodes;
    private Scheduler scheduler;
    private int prevDriveNodeIndex = 0;
    private Follower follower;

    public CommandTreeBuilder(Scheduler scheduler, Follower follower) {
        nodes = new ArrayList<>();
        this.scheduler = scheduler;
        this.follower = follower;
    }

    public CommandTreeBuilder addRunNode(RunNode node) {
        if (node.failsafe != null && node.failsafe.getJumpState() == 0) {
            node.failsafe.setJumpState(nodes.size() + 1);
        }
        nodes.add(node);
        return this;
    }

    public CommandTreeBuilder waitNode(double millis) {
        Node node = new Node() {
            RevAmpedTimer timer = new RevAmpedTimer(false);

            @Override
            public void run() {
                timer.start();
            }

            @Override
            public void update() {}

            @Override
            public boolean isComplete() {
                return timer.millis() >= millis;
            }
        };

        nodes.add(node);
        return this;
    }

    public CommandTreeBuilder waitForDriveNode() {
        Node node = new Node() {
            boolean complete = false;
            @Override
            public void run() {}

            @Override
            public void update() {
                if (!complete && nodes.get(prevDriveNodeIndex).isComplete()) {
                    complete = true;
                }
            }

            @Override
            public boolean isComplete() {
                return complete;
            }
        };

        nodes.add(node);
        return this;
    }

    public CommandTreeBuilder waitForDriveNode(BooleanSupplier complete) {
        Node node = new Node() {
            boolean isComplete = false;
            @Override
            public void run() {}

            @Override
            public void update() {
                if (!isComplete && complete.getAsBoolean()) {
                    isComplete = true;
                }
            }

            @Override
            public boolean isComplete() {
                return isComplete;
            }
        };

        nodes.add(node);
        return this;
    }

    public CommandTreeBuilder addDriveNode(PathChain pathChain, BooleanSupplier isComplete) {
        Node node = new DriveNode(follower, pathChain, isComplete);
        nodes.add(node);
        prevDriveNodeIndex = nodes.size()-1;
        return this;
    }

    public CommandTreeBuilder addDriveNode(PathChain pathChain, BooleanSupplier isComplete, double maxMillisToComplete) {
        Node node = new DriveNode(follower, pathChain, isComplete, maxMillisToComplete);
        nodes.add(node);
        prevDriveNodeIndex = nodes.size()-1;
        return this;
    }

    public CommandTree build() {
        CommandTree tree = new CommandTree();
        tree.setNodes(nodes);
        tree.setScheduler(scheduler);
        tree.init();
        return tree;
    }
}
