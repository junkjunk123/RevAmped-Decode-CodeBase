package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.ProjectileMath;
import revAmped.components.MathUtil.Vector;
import revAmped.components.MathUtil.Vector2D;
import revAmped.components.Misc.Globals;

public class Shooter {
    public final Scheduler scheduler;
    private boolean trackHood = false;
    private boolean trackTurret = false;

    public record HardwareComponents(HoodServo hoodServo, TurretMotor turretServo) {
        public void update() {
            hoodServo.update();
            turretServo.update();
        }
    }

    public final HardwareComponents hardware;

    public enum ShooterState {
        REST,
        TRACKING,
    }
    public ShooterState currentState;

    public static double FLYWHEEL_MAX_VELOCITY;
    public static double LAUNCH_HEIGHT;
    public static double TARGET_LAUNCH_HEIGHT;
    public static Pose APRIL_TAG_POSE_RED;
    public static Pose APRIL_TAG_POSE_BLUE;

    private Localizer pinpoint;

    public Shooter(HardwareMap hardwareMap, boolean setPositions, Scheduler scheduler) {
        this.scheduler = scheduler;
        HoodServo hoodServo = new HoodServo(hardwareMap, setPositions);
        TurretMotor turretServo = new TurretMotor(hardwareMap, TurretMotor.TurretState.IDLE, null);
        hardware = new HardwareComponents(hoodServo, turretServo);
    }

    public Localizer pinpoint() {
        if (pinpoint == null) {
            if (Globals.followerManager != null) pinpoint = Globals.followerManager.follower.getPoseTracker().getLocalizer();
            else pinpoint = TeleOpDrivetrainHandler.INSTANCE.getLocalizer();
        }

        return pinpoint;
    }

    public void update() {
        hardware.update();

        switch (currentState) {
            case TRACKING -> {
                Pose targetPos = Globals.allianceColor == Globals.AllianceColor.Red ? Shooter.APRIL_TAG_POSE_RED : Shooter.APRIL_TAG_POSE_BLUE;
                Pose currentPos = pinpoint().getPose();
                Vector2D offset = new Vector2D(targetPos.getX() - currentPos.getX(), targetPos.getY() - currentPos.getY());

                if (trackHood) {
                    double theta = ProjectileMath.solveTheta(Shooter.FLYWHEEL_MAX_VELOCITY, Shooter.LAUNCH_HEIGHT,
                            offset.magnitude(), Shooter.LAUNCH_HEIGHT);
                    float hoodAngle = HwServo2.getPositionFromRad(theta, HoodServo.HOOD_MIN_RAD, HoodServo.HOOD_MAX_RAD,
                            HoodServo.HOOD_MIN_POS, HoodServo.HOOD_MAX_POS);
                    hardware.hoodServo().setCurrentState(HoodServo.HoodState.TRACKING, hoodAngle);
                }

                if (trackTurret) {
                    double targetAngle = offset.getTheta() + Math.PI;
                    double currentAngle = pinpoint().getPose().getHeading();
                    double deltaAngle = MathUtil.normalizeAngleHalfPi(targetAngle - currentAngle);
                    hardware.turretServo().finetuneAngle(deltaAngle);
                }
            }
        }
    }

    public void setCurrentState(ShooterState currentState) {
        this.currentState = currentState;
    }

    public boolean isTrackHood() {
        return trackHood;
    }

    public boolean isTrackTurret() {
        return trackTurret;
    }

    public void setTrackHood(boolean trackHood) {
        this.trackHood = trackHood;
    }

    public void setTrackTurret(boolean trackTurret) {
        this.trackTurret = trackTurret;
    }
}
