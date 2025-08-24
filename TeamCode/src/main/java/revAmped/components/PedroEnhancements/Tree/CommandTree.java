package revAmped.components.PedroEnhancements.Tree;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;

public class CommandTree {
    public ArrayList<Node> nodes;
    private int currentIndex = 0;
    private boolean driving = false;
    private Scheduler scheduler;
    private Node currentNode;

    public void update() {
        currentNode.update();

        if (currentNode.isComplete()) {
            nextNode();
        }
    }

    private void nextNode() {
        currentIndex = currentNode.nextState(currentIndex);

        if (currentIndex == nodes.size()) {
            return;
        }

        currentNode = nodes.get(currentIndex);
        currentNode.run();

        if (currentNode.isDrive()) {
            driving = true;
            scheduler.add(new Delay(currentNode.checkComplete(), () -> driving = false));
            nextNode();
        }
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void init() {
        currentNode = nodes.get(currentIndex);
    }

    public boolean isDriving() {
        return driving;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
