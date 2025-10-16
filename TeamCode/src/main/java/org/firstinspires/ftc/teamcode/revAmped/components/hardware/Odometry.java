package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import com.pedropathing.geometry.Pose;

public interface Odometry {
    Pose getPose();
    Pose getVelocity();
}
