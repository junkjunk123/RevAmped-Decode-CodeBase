package revAmped.components.Pedro;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathChain;

import java.util.List;

/**
 * PathSupplier is an interface that provides methods to get the starting pose and build paths.
 * It is used to supply paths for autonomous actions in a robot's autonomous mode.
 */
public interface PathSupplier {
    /**
     * Returns the starting pose of the robot.
     * @return the starting Pose
     */
    Pose getStartPose();

    /**
     * Builds and returns a list of PathChain objects that represent the paths to be followed.
     * @return a List of PathChain objects
     */
    List<PathChain> buildPaths();

    default PlayingField getPlayingField() {
        return null;
    }
}
