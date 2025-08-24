package revAmped.components.Pedro;

import com.pedropathing.localization.Pose;

import java.util.Collections;
import java.util.List;

public interface TargetZone {
    /**
     * Checks if the given pose is within the target zone.
     *
     * @param pose the pose to check
     * @return true if the pose is within the scoring zone, false otherwise
     */
    boolean inScoringZone(Pose pose);

    /**
     * Gets the optimal pose for scoring or intaking.
     * @return the optimal pose for scoring or intaking.
     */
    Pose getOptimalPose();

    /**
     * Gets the inverse kinematics for the given pose.
     * This method can be overridden to provide specific inverse kinematics calculations.
     *
     * @param pose the pose for which to calculate inverse kinematics
     * @return a list of joint angles or positions that achieve the desired pose
     */
    default List<Double> getInverseKinematics(Pose pose) {
        return Collections.emptyList(); // Default implementation returns empty, can be overridden if needed
    }

    boolean isIntake();
    default boolean isDepo() {
        return !isIntake();
    }
}
