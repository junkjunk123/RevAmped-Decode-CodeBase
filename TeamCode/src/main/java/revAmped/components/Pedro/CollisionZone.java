package revAmped.components.Pedro;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.Path;

public interface CollisionZone {
    Pose center();
    double minXDist();
    double minYDist();
    boolean in(Pose pose);
    boolean intersection(Pose current, Pose target);
    Pose safestPoint(double t, Pose pose, Path path);
}
