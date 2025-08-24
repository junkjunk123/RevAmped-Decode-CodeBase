package revAmped.components.Pedro;

import java.util.Set;

public interface PlayingField {
    Set<CollisionZone> getCollisionZones();
    Set<TargetZone> getTargetZones();
}
