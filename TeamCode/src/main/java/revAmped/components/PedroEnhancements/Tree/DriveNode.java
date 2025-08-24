package revAmped.components.PedroEnhancements.Tree;

import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;

import java.util.function.BooleanSupplier;

import revAmped.components.MathUtil.RevAmpedTimer;

public class DriveNode implements Node {
    private PathChain pathChain;
    private Follower follower;
    private BooleanSupplier isComplete;
    private boolean complete = false;
    private RevAmpedTimer timer = new RevAmpedTimer(false);

    public DriveNode(Follower follower, PathChain chain, BooleanSupplier isComplete) {
        this.follower = follower;
        this.pathChain = chain;
        this.isComplete = isComplete;
    }

    public DriveNode(Follower follower, PathChain chain, BooleanSupplier isComplete, double maxMillisToComplete) {
        this.follower = follower;
        this.pathChain = chain;
        this.isComplete = () -> timer.millis() >= maxMillisToComplete || isComplete.getAsBoolean();
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void run() {
        follower.followPath(pathChain);
        timer.start();
    }

    @Override
    public void update() {
        if (!complete && isComplete.getAsBoolean()) {
            complete = true;
        }
    }
}
