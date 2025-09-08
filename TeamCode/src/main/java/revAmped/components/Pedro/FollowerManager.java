package revAmped.components.Pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

import revAmped.components.Misc.Globals;

/**
 * FollowerManager is a class that manages the Follower component,
 */
public class FollowerManager {
    /**
     * The Follower instance that is used to follow paths.
     */
    public final Follower follower;
    /**
     * A list of PathChain objects that represent the paths to be followed.
     * Each PathChain contains multiple Path objects.
     */
    protected List<PathChain> pathChains = new ArrayList<>();

    /**
     * The index of the current pathChain
     */
    private int currentChainIndex = 0;

    /**
     * The index of the current path in the current path chain
     */
    private int currentPathIndex = 0;

    /**
     * A PathSupplier that provides the paths to be followed.
     */
    private PathSupplier pathSupplier;

    /**
     * A Stuck instance that checks if the Follower is stuck at the end of a path.
     * It evaluates whether the Follower's position is not changing significantly.
     */
    public final Stuck position;

    /**
     * A Stuck instance that checks if the Follower's heading is not changing significantly.
     * It evaluates whether the angular velocity of the Follower is below a certain threshold.
     */
    public final Stuck heading;

    /**
     * An ObstacleAvoidanceHandler that manages obstacle avoidance for the Follower.
     * It checks for obstacles and handles navigation around them.
     */
    public final ObstacleAvoidanceHandler obstacleAvoider;

    /**
     * Constructor for FollowerManager.
     * @param hardwareMap the HardwareMap used to initialize the Follower
     * @param pathSupplier the PathSupplier that provides the paths to be followed
     */
    public FollowerManager(HardwareMap hardwareMap, PathSupplier pathSupplier) {
        follower = Constants.createFollower(hardwareMap);
        Globals.followerManager = this;
        this.pathSupplier = pathSupplier;
        setStartingPose();
        buildPaths();
        position = new Stuck(follower);
        heading = new Stuck(() -> {
            if (follower.getCurrentPath() == null) return false;
            return follower.poseTracker.getAngularVelocity() < 0.007;
        }, 400);

        if (pathSupplier.getPlayingField() != null) {
            obstacleAvoider = new ObstacleAvoidanceHandler(follower, pathSupplier.getPlayingField());
        } else {
            obstacleAvoider = null;
        }
    }

    /**
     * Returns the PathSupplier that provides the paths to be followed.
     * @param i the index of the path chain to retrieve
     * @return the PathChain at the specified index
     */
    public PathChain getPathChain(int i) {
        return pathChains.get(i);
    }

    /**
     * Starts following the first path in the first path chain.
     */
    public void start() {
        followPath(0);
    }

    /**
     * Updates the Follower's state.
     */
    public void update() {
        update(false);
    }

    /**
     * Updates the Follower's state.
     */
    public void update(boolean useObstacleAvoidance) {
        if (!Globals.disableDrive) follower.update();
        currentPathIndex = follower.getChainIndex();
        heading.update();
        position.update();

        if (useObstacleAvoidance && obstacleAvoider != null) {
            obstacleAvoider.update();
        }
    }

    /**
     * Follows the path corresponding to the given index
     * @param i the index of the path chain to follow
     * @param holdEnd if true, the follower will hold the end of the path when it reaches it
     */
    public void followPath(int i, boolean holdEnd) {
        currentChainIndex = i;
        if (i < size()) follower.followPath(pathChains.get(currentChainIndex), holdEnd);
    }

    /**
     * Follows the path corresponding to the given index
     * @param i the index of the path chain to follow
     */
    public void followPath(int i) {
        followPath(i, true);
    }

    /**
     * Returns the current path chain index.
     * @return the index of the current path chain
     */
    public int currentPathChainIndex() {
        return currentChainIndex;
    }

    /**
     * Follows the next path chain in the list.
     */
    public void followNextChain() {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), true);
        }
    }

    /**
     * Follows the next path chain in the list, with an option to hold the end of the path.
     * @param holdEnd if true, the follower will hold the end of the path when it reaches it
     */
    public void followNextChain(boolean holdEnd) {
        currentChainIndex++;

        if (currentChainIndex < pathChains.size()) {
            follower.followPath(pathChains.get(currentChainIndex), holdEnd);
        }
    }

    /**
     * Returns the current PathChain being followed.
     * @return the current PathChain
     */
    public PathChain getCurrentPathChain() {
        return pathChains.get(currentChainIndex);
    }

    /**
     * Returns the index of the current path chain being followed.
     * @return the index of the current path chain
     */
    public int getCurrentChainIndex() {
        return currentChainIndex;
    }

    /**
     * Returns the current Path being followed.
     * @return the current path
     */
    public Path getCurrentPath() {
        return getCurrentPathChain().getPath(currentPathIndex);
    }

    /**
     * Returns the index of the current path in the current path chain.
     * @return the current path index in the current path chain
     */
    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    /**
     * The robot's distance from the target end position
     * @return the distance to the target position
     */
    public double distanceFromTarget() {
        return follower.getPose().distanceFrom(follower.getCurrentPathChain().endPoint());
    }

    /**
     * The robot's heading error from the target heading
     * @return the error from the target heading
     */
    public double headingFromTarget() {
        return follower.getCurrentPath().getHeadingGoal(1) - follower.getPose().getHeading();
    }

    /**
     * Builds the paths using the PathSupplier.
     */
    private void buildPaths() {
        pathChains = pathSupplier.buildPaths();
    }

    /**
     * Sets the starting pose for the Follower based on the PathSupplier's start pose.
     */
    private void setStartingPose() {
        follower.setStartingPose(pathSupplier.getStartPose());
    }

    /**
     * Returns the last point of the last path in the given PathChain.
     * @param pathChain the PathChain from which to get the last point
     * @return the last control point of the last path in the PathChain
     */
    public Pose getLastPoint(PathChain pathChain) {
        return pathChain.getPath(pathChain.size() - 1).getLastControlPoint();
    }

    /**
     * Returns the number of PathChains managed by this FollowerManager.
     * @return the size of the pathChains list
     */
    public int size() {
        return pathChains.size();
    }

    /**
     * Checks if the Follower has finished following the current path.
     * @param parametricEnd if true, allows atParametricEnd() to be considered as finished
     * @return true if the Follower has finished following the path, false otherwise
     */
    public boolean finished(boolean parametricEnd) {
        if (follower.getCurrentPath() == null) return false;
        if (follower.getCurrentPathChain().endPoint().distanceFrom(getCurrentPathChain().endPoint()) > 0.5) return false;
        if (!follower.isBusy()) return true;
        if (follower.atParametricEnd() && parametricEnd) return true;
        if (follower.getVelocity().getMagnitude() > follower.getCurrentPath().getConstraints().getVelocityConstraint()) return false;
        if (follower.getAngularVelocity() > follower.getCurrentPath().getConstraints().getVelocityConstraint()) return false;
        if (!(position.evaluate() && distanceFromTarget() < 1.5) && !(follower.atParametricEnd() || follower.getTranslationalError().getMagnitude() > follower.getCurrentPath().getConstraints().getTranslationalConstraint())) return false;
        return (parametricEnd || heading.evaluate() && headingFromTarget() < 0.055 || follower.getHeadingError() < follower.getCurrentPath().getConstraints().getHeadingConstraint());
    }

    public void setPlayingField(PlayingField field) {
        if (obstacleAvoider != null) {
            obstacleAvoider.setField(field);
        }
    }
}
