package revAmped.components.PedroEnhancements;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;

import java.util.List;

public interface PathSupplier {
    Pose getStartPose();
    List<PathChain> buildPaths(Follower follower);
}
