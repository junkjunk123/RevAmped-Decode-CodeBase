package revAmped.components.Pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;

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
        this.detectCollision = new Stuck(() -> follower.getVelocity().getMagnitude() < FollowerConstants.pathEndVelocityConstraint
                && follower.getCurrentPath().getLastControlPoint().distanceFrom(new Point(follower.getPose())) < 0.5, 2000);
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

    public void pausePathFollowing(BezierCurve bezierCurve) {
        // Placeholder for pausing path following
        follower.breakFollowing();
        follower.followPath(new Path(bezierCurve));
        Scheduler.getInstance().add(new Delay(() -> !follower.isBusy(), this::resumePathFollowing));
        navigatingAroundObstacle = true;
    }

    public void pausePathFollowing(PathChain pathChain) {
        // Placeholder for pausing path following
        follower.breakFollowing();
        follower.followPath(pathChain);
        Scheduler.getInstance().add(new Delay(() -> !follower.isBusy(), this::resumePathFollowing));
        navigatingAroundObstacle = true;
    }

    public void resumePathFollowing() {
        //Placeholder for resuming path following
        follower.followPath(Globals.followerManager.getCurrentPathChain());
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

        if (!obstacle.intersection(follower.getPose(), follower.getClosestPose())) {
            pausePathFollowing(new BezierLine(follower.getPose(), follower.getClosestPose()));
        }

        pausePathFollowing(new BezierLine(follower.getPose(), obstacle.safestPoint(follower.getCurrentTValue(), follower.getPose(), follower.getCurrentPath())));
    }

    public void setField(PlayingField field) {
        this.field = field;
    }
}
