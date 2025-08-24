package revAmped.components.PedroEnhancements;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class FollowerManager {
    protected Follower follower;
    protected List<PathChain> pathChains = new ArrayList<>();
    private int currentChainIndex = 0;
    private int currentPathIndex = 0;

    public FollowerManager(Follower follower) {
        this.follower = follower;
        setStartingPose();
        buildPaths();
    }

    public PathChain getPathChain(int i) {
        return pathChains.get(i);
    }

    public int getPathChainSize() {
        return pathChains.size();
    }

    public void start() {
        followPath(0);
    }

    public void update() {
        follower.update();
        currentPathIndex = follower.getChainIndex();
    }

    public void followPath(int i, boolean holdEnd) {
        currentChainIndex = i;
        if (i < size()) follower.followPath(pathChains.get(currentChainIndex), holdEnd);
    }

    public void followPath(PathChain pathChain, boolean holdEnd) {
        follower.followPath(pathChain, holdEnd);
    }

    public void followPath(Path path, boolean holdEnd) {
        follower.followPath(path, holdEnd);
    }

    public void followPath(int i) {
        followPath(i, true);
    }

    public int currentPathChainIndex() {
        return currentChainIndex;
    }

    public void followNextChain() {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), true);
        }
    }

    public void followNextChain(boolean holdEnd) {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), holdEnd);
        }
    }

    public PathChain getCurrentPathChain() {
        return pathChains.get(currentChainIndex);
    }

    public int getCurrentChainIndex() {
        return currentChainIndex;
    }

    public Path getCurrentPath() {
        return getCurrentPathChain().getPath(currentPathIndex);
    }

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public double distanceFromTarget() {
        return new Point(follower.getPose()).distanceFrom(follower.getCurrentPathChain().getLastPoint());
    }

    public Follower getFollower() {
        return follower;
    }

    public abstract void buildPaths();
    public abstract void setStartingPose();

    public Point getLastPoint(PathChain pathChain) {
        return pathChain.getPath(pathChain.size() - 1).getLastControlPoint();
    }

    public int size() {
        return pathChains.size();
    }
}
