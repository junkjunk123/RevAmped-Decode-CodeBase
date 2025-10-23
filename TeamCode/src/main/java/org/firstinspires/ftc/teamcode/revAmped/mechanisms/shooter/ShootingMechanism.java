package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector3D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public interface ShootingMechanism<T> {
    Pose APRIL_TAG_POSE_RED = null;
    Pose APRIL_TAG_POSE_BLUE = null;
    Vector3D BLUE_SHOOT_ZONE = null;

    void update();

    static Vector3D getTargetLaunchPos() {
        return Globals.allianceColor == Globals.AllianceColor.Red ? new Vector3D(144 - BLUE_SHOOT_ZONE.getX(), BLUE_SHOOT_ZONE.getY(), BLUE_SHOOT_ZONE.getY()) : BLUE_SHOOT_ZONE;
    }

    void shoot();

    void setCurrentState(T state);
    void setCurrentState(T state, boolean init);
    T getCurrentState();

    void shootAutoSet(int set);
    Localizer pinpoint();

    Types.ShooterHardwareComponents hardware();

    void toggleHoodTracking();
    void toggleTurretTracking();
    void toggleState();
    Localizer getPinpoint();
}
