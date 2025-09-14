package org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter;

import com.pedropathing.geometry.Pose;

import revAmped.components.MathUtil.Vector3D;
import revAmped.components.Misc.Globals;

public interface ShootingMechanism<T> {
    Pose APRIL_TAG_POSE_RED = null;
    Pose APRIL_TAG_POSE_BLUE = null;
    Vector3D BLUE_SHOOT_ZONE = null;

    void update();
    void setCurrentState(T state);
    T getCurrentState();
    void setCurrentState(T state, boolean initAction);

    static Vector3D getTargetLaunchPos() {
        return Globals.allianceColor == Globals.AllianceColor.Red ? new Vector3D(144 - BLUE_SHOOT_ZONE.getX(), BLUE_SHOOT_ZONE.getY(), BLUE_SHOOT_ZONE.getY()) : BLUE_SHOOT_ZONE;
    }

    void shoot();
}
