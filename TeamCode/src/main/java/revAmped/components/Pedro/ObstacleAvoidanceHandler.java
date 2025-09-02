package revAmped.components.Pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Misc.Globals;

public class ObstacleAvoidanceHandler {
    private final Stuck detectCollision;
    private final Follower follower;
    private PlayingField field;
    private boolean navigatingAroundObstacle = false;

    public ObstacleAvoidanceHandler(Follower follower, PlayingField field) {
        // Initialize the Stuck instance with a Follower, velocity threshold, and time threshold
        // This will be used to detect if the robot is stuck due to an obstacle
        this.detectCollision = new Stuck(() -> follower.getVelocity().getMagnitude() < follower.getCurrentPath().getConstraints().getVelocityConstraint()
                && follower.getCurrentPath().getLastControlPoint().distanceFrom(follower.getPose()) < 0.5, 2000);
        this.follower = follower;
        this.field = field;
    }

    public boolean update() {
        if (!navigatingAroundObstacle) {
            detectCollision.update();
            checkObstacles();
        }

        return !navigatingAroundObstacle;
    }

    public void pausePathFollowing(Pose pose) {
        follower.pausePathFollowing();
        follower.holdPoint(new BezierPoint(pose), pose.getHeading(), false);
        Scheduler.getInstance().add(new Delay(() -> follower.getTranslationalError().getMagnitude() < 0.5, this::resumePathFollowing));
        navigatingAroundObstacle = true;
    }

    public void resumePathFollowing() {
        follower.resumePathFollowing();
    }

    public void checkObstacles() {
        if (!detectCollision.evaluate()) return;

        CollisionZone obstacle = null;
        for (CollisionZone o : field.getCollisionZones()) {
            if (o.in(follower.getPose())) {
                obstacle = o;
                break;
            }
        }

        detectCollision.reset();

        if (obstacle == null) return;

        if (!obstacle.intersection(follower.getPose(), follower.getClosestPose().getPose())) {
            pausePathFollowing(obstacle.safestPoint(follower.getCurrentTValue(), follower.getPose(), follower.getCurrentPath()));
        }

        pausePathFollowing(obstacle.safestPoint(follower.getCurrentTValue(), follower.getPose(), follower.getCurrentPath()));
    }

    public void setField(PlayingField field) {
        this.field = field;
    }
}
