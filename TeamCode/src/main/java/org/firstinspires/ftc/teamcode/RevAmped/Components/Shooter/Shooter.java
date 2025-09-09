package org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.ProjectileMath;
import revAmped.components.MathUtil.Vector2D;
import revAmped.components.Misc.Globals;

public class Shooter {
    public final Scheduler scheduler;
    private boolean trackHood = false;
    private boolean trackTurret = false;

    public record HardwareComponents(HoodServo hoodServo, TurretMotor turretMotor, Flywheel flywheel) {
        public void update() {
            hoodServo.update();
            turretMotor.update();
            flywheel.update();
        }
    }

    public final HardwareComponents hardware;

    public enum ShooterState {
        REST,
        TRACKING
    }

    private ShooterState currentState;

    public static double FLYWHEEL_MAX_VELOCITY;
    public static double LAUNCH_HEIGHT;
    public static double TARGET_LAUNCH_HEIGHT;
    public static Pose APRIL_TAG_POSE_RED;
    public static Pose APRIL_TAG_POSE_BLUE;

    private Localizer pinpoint;

    public Shooter(HardwareMap hardwareMap, boolean setPositions, Scheduler scheduler) {
        this.scheduler = scheduler;
        HoodServo hoodServo = new HoodServo(hardwareMap, setPositions);
        TurretMotor turretServo = new TurretMotor(hardwareMap, setPositions ? TurretMotor.TurretState.HOLD : TurretMotor.TurretState.IDLE, null);
        Flywheel flywheel = new Flywheel(hardwareMap, Flywheel.FlywheelState.STOPPED);
        hardware = new HardwareComponents(hoodServo, turretServo, flywheel);
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
                            offset.magnitude(), Shooter.TARGET_LAUNCH_HEIGHT);
                    float hoodAngle = HwServo2.getPositionFromRad(theta, HoodServo.HOOD_MIN_RAD, HoodServo.HOOD_MAX_RAD,
                            HoodServo.HOOD_MIN_POS, HoodServo.HOOD_MAX_POS);
                    hardware.hoodServo().setCurrentState(HoodServo.HoodState.TRACKING, hoodAngle);
                }

                if (trackTurret) {
                    double targetAngle = offset.getTheta() + Math.PI;
                    double currentAngle = pinpoint().getPose().getHeading();
                    double deltaAngle = MathUtil.normalizeAngleHalfPi(targetAngle - currentAngle);
                    hardware.turretMotor().finetuneAngle(deltaAngle);
                }
            }
        }
    }

    public void setCurrentState(ShooterState currentState) {
        setCurrentState(currentState, true);
    }

    private void initAction() {
        switch (currentState) {
            case REST -> {
                hardware.flywheel().setCurrentState(Flywheel.FlywheelState.STOPPED);
                hardware.hoodServo().setCurrentState(HoodServo.HoodState.REST);
                hardware.turretMotor().setCurrentState(TurretMotor.TurretState.IDLE);
                setTrackHood(false);
                setTrackTurret(false);
            }
            case TRACKING -> {
                hardware.hoodServo.setCurrentState(HoodServo.HoodState.TRACKING);
                hardware.turretMotor.setCurrentState(TurretMotor.TurretState.TRACKING);
                hardware.flywheel.setCurrentState(Flywheel.FlywheelState.TRACKING);
                setTrackHood(true);
                setTrackTurret(true);
            }
        }
    }

    public void setCurrentState(ShooterState currentState, boolean init) {
        this.currentState = currentState;
        if (init) initAction();
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

    public void shoot() {
        hardware.flywheel().run(FLYWHEEL_MAX_VELOCITY);
    }

    public void shootAutoSet(int set) {
        switch (set) {
            case 0 -> {
                hardware.hoodServo().setCurrentState(HoodServo.HoodState.REST, HoodServo.AUTO_PRELOADS);
                hardware.turretMotor().setCurrentState(TurretMotor.TurretState.IDLE, TurretMotor.AUTO_PRELOADS);
                hardware.flywheel().run(Flywheel.AUTO_PRELOADS);
            }
            case 1 -> {
                hardware.hoodServo().setCurrentState(HoodServo.HoodState.REST, HoodServo.AUTO_SET_1);
                hardware.turretMotor().setCurrentState(TurretMotor.TurretState.IDLE, TurretMotor.AUTO_SET_1);
                hardware.flywheel().run(Flywheel.AUTO_SET_1);
            }
            case 2 -> {
                hardware.hoodServo().setCurrentState(HoodServo.HoodState.REST, HoodServo.AUTO_SET_2);
                hardware.turretMotor().setCurrentState(TurretMotor.TurretState.IDLE, TurretMotor.AUTO_SET_2);
                hardware.flywheel().run(Flywheel.AUTO_SET_2);
            }
            case 3 -> {
                hardware.hoodServo().setCurrentState(HoodServo.HoodState.REST, HoodServo.AUTO_SET_3);
                hardware.turretMotor().setCurrentState(TurretMotor.TurretState.IDLE, TurretMotor.AUTO_SET_3);
                hardware.flywheel().run(Flywheel.AUTO_SET_3);
            }
        }
    }
}
