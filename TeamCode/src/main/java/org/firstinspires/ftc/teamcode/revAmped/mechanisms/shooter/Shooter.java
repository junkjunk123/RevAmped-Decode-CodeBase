package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.math.ProjectileMathWithDrag;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector3D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterHardwareComponents;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterState;

@Config
public class Shooter implements ShootingMechanism<ShooterState> {
    public final Scheduler scheduler;
    public static boolean trackHood = false;
    public static boolean trackTurret = false;

    public final ShooterHardwareComponents hardware;

    private ShooterState currentState;

    public static double FLYWHEEL_MAX_VELOCITY;
    public static double LAUNCH_HEIGHT;
    public static Pose APRIL_TAG_POSE_RED = new Pose(139, 138);
    public static Pose APRIL_TAG_POSE_BLUE = new Pose(5, 138);
    public static Vector3D BLUE_SHOOT_ZONE;

    public static double BALL_MASS = 0.057152639; //kg
    private double confidence;

    public static boolean velocityCompensationOnTurret;

    private Localizer pinpoint;

    public Shooter(HardwareMap hardwareMap, boolean setPositions, Scheduler scheduler) {
        this.scheduler = scheduler;
        HoodServo hoodServo = new HoodServo(hardwareMap, setPositions);
        TurretMotor turretServo = new TurretMotor(hardwareMap, setPositions ? Types.TurretState.HOLD : Types.TurretState.IDLE);
        Flywheel flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
        hardware = new ShooterHardwareComponents(hoodServo, turretServo, flywheel);
        trackHood = false;
        trackTurret = false;
        velocityCompensationOnTurret = false;
    }

    public Localizer pinpoint() {
        if (pinpoint == null) {
            if (Globals.followerManager != null) pinpoint = Globals.followerManager.follower.getPoseTracker().getLocalizer();
            else pinpoint = TeleOpDrivetrainHandler.INSTANCE.getLocalizer();
        }

        return pinpoint;
    }

    @Override
    public ShooterHardwareComponents hardware() {
        return hardware;
    }

    @Override
    public void toggleHoodTracking() {
        trackHood = !trackHood;
    }

    @Override
    public void toggleTurretTracking() {
        trackTurret = !trackTurret;
        if (hardware().turretMotor().getCurrentState() == Types.TurretState.TRACKING && !trackTurret) hardware().turretMotor().setCurrentState(Types.TurretState.HOLD);
    }

    public void update() {
        hardware.update();

        if (currentState == ShooterState.TRACKING) {
            Pose targetPos = Globals.allianceColor == Globals.AllianceColor.Red ? Shooter.APRIL_TAG_POSE_RED : Shooter.APRIL_TAG_POSE_BLUE;
            Pose currentPos = pinpoint().getPose();
            Globals.telemetry.addData("currentPose", currentPos);
            Vector offset = new Vector();
            offset.setOrthogonalComponents(targetPos.getX() - currentPos.getX(), targetPos.getY() - currentPos.getY());
            Globals.telemetry.addData("offset", offset);
            double dist = offset.getMagnitude();
            Vector robotVelocity = pinpoint.getVelocityVector();
            if (robotVelocity.getMagnitude() < 4) hardware.flywheel().updateTargetVelocity(dist);

            double deltaAngle;

            if (trackTurret) {
                if (robotVelocity.getMagnitude() < 4) {
                    double targetAngle = offset.getTheta() + Math.PI;
                    Globals.telemetry.addData("hoodTargetAngleDeg", Math.toDegrees(targetAngle));
                    double currentAngle = pinpoint().getPose().getHeading();
                    deltaAngle = MathUtil.normalizeAnglePi(targetAngle - currentAngle);
                    deltaAngle = Range.clip(deltaAngle, -TurretMotor.TURRET_RAD_LIMIT, TurretMotor.TURRET_RAD_LIMIT);
                    Globals.telemetry.addData("deltaAngle", deltaAngle);
                    hardware.turretMotor().finetuneAngle(deltaAngle);
                } else if (velocityCompensationOnTurret) {
                    Vector iHat = offset.normalize();
                    Vector jHat = new Vector();
                    jHat.setOrthogonalComponents(-offset.getYComponent(), offset.getXComponent());
                    double flywheelLaunchSpeed = hardware.flywheel().getBallLaunchVelocity();
                    Vector invertediHat = new Vector();
                    invertediHat.setOrthogonalComponents(iHat.getYComponent(), iHat.getXComponent());
                    double normalizedTurretNormalComponent = robotVelocity.getMagnitude() *
                            robotVelocity.dot(invertediHat) / flywheelLaunchSpeed;
                    Vector flywheelDirectionVector = new Vector();
                    flywheelDirectionVector.setOrthogonalComponents(Math.sqrt(1 - Math.pow(normalizedTurretNormalComponent, 2)), normalizedTurretNormalComponent);
                    Matrix inverseRotMatrix = new Matrix(new double[][] {
                            {iHat.getXComponent(), iHat.getYComponent()},
                            {jHat.getXComponent(), jHat.getYComponent()}
                    });
                    double targetAngle = flywheelDirectionVector.transform(inverseRotMatrix).getTheta() + Math.PI;
                    double currentAngle = pinpoint().getPose().getHeading();
                    deltaAngle = MathUtil.normalizeAnglePi(targetAngle - currentAngle);
                    deltaAngle = Range.clip(deltaAngle, -TurretMotor.TURRET_RAD_LIMIT, TurretMotor.TURRET_RAD_LIMIT);
                    hardware.turretMotor().finetuneAngle(deltaAngle);
                } else {
                    deltaAngle = hardware.turretMotor().getPrevFineTuneAngle();
                }
            } else {
                deltaAngle = hardware.turretMotor().getPrevFineTuneAngle();
            }

            if (trackHood) {
                Vector3D target = getTargetLaunchPos();
                Vector2D targetDisplacements = new Vector2D (
                        pinpoint.getPose().distanceFrom(new Pose(target.getX(), target.getY())),
                        target.getZ() - LAUNCH_HEIGHT
                );
                double initialVelocity = calcInitialLaunchVelocity(deltaAngle);

                double theta = ProjectileMathWithDrag.solveTheta(
                        targetDisplacements,
                        initialVelocity
                );

                confidence = ProjectileMathWithDrag.getConfidence(
                        targetDisplacements,
                        initialVelocity,
                        theta
                );

                if (!Double.isNaN(theta) && confidence > 0.5) {
                    float hoodAngle = HwServo2.getPositionFromRad(
                            theta,
                            HoodServo.HOOD_MIN_RAD,
                            HoodServo.HOOD_MAX_RAD,
                            HoodServo.HOOD_MIN_POS,
                            HoodServo.HOOD_MAX_POS
                    );
                    hardware.hoodServo().setCurrentState(Types.HoodState.TRACKING, hoodAngle);
                }
            } else {
                if (robotVelocity.getMagnitude() < 4) hardware.hoodServo().updatePreset(dist);
            }
        }
    }

    public void setCurrentState(ShooterState currentState) {
        setCurrentState(currentState, true);
    }

    private void initAction() {
        switch (currentState) {
            case REST -> {
                hardware.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
                hardware.hoodServo().setCurrentState(Types.HoodState.REST);
                hardware.turretMotor().setCurrentState(Types.TurretState.HOLD);
                confidence = 0;
            }
            case TRACKING -> {
                Pose targetPos = Globals.allianceColor == Globals.AllianceColor.Red ? Shooter.APRIL_TAG_POSE_RED : Shooter.APRIL_TAG_POSE_BLUE;
                double dist = targetPos.distanceFrom(targetPos);
                hardware.hoodServo().setPreset(dist);
                hardware.turretMotor().setCurrentState(Types.TurretState.TRACKING);
                hardware.flywheel().setCurrentState(Types.FlywheelState.RUNNING);
            }
        }
    }

    public void setCurrentState(ShooterState currentState, boolean init) {
        this.currentState = currentState;
        if (init) initAction();
    }

    @Override
    public ShooterState getCurrentState() {
        return currentState;
    }

    public boolean isTrackHood() {
        return trackHood;
    }

    public boolean isTrackTurret() {
        return trackTurret;
    }

    public void setTrackHood(boolean trackHood) {
        Shooter.trackHood = trackHood;
    }

    public void setTrackTurret(boolean trackTurret) {
        Shooter.trackTurret = trackTurret;
    }

    public void shoot() {
        hardware.flywheel().run(FLYWHEEL_MAX_VELOCITY);
    }

    public void shootAutoSet(int set) {
        switch (set) {
            case 0 -> {
                hardware.hoodServo().setCurrentState(Types.HoodState.REST, HoodServo.AUTO_PRELOADS);
                hardware.turretMotor().setCurrentState(Types.TurretState.IDLE, TurretMotor.AUTO_PRELOADS);
                hardware.flywheel().run(Flywheel.AUTO_PRELOADS);
            }
            case 1 -> {
                hardware.hoodServo().setCurrentState(Types.HoodState.REST, HoodServo.AUTO_SET_1);
                hardware.turretMotor().setCurrentState(Types.TurretState.IDLE, TurretMotor.AUTO_SET_1);
                hardware.flywheel().run(Flywheel.AUTO_SET_1);
            }
            case 2 -> {
                hardware.hoodServo().setCurrentState(Types.HoodState.REST, HoodServo.AUTO_SET_2);
                hardware.turretMotor().setCurrentState(Types.TurretState.IDLE, TurretMotor.AUTO_SET_2);
                hardware.flywheel().run(Flywheel.AUTO_SET_2);
            }
            case 3 -> {
                hardware.hoodServo().setCurrentState(Types.HoodState.REST, HoodServo.AUTO_SET_3);
                hardware.turretMotor().setCurrentState(Types.TurretState.IDLE, TurretMotor.AUTO_SET_3);
                hardware.flywheel().run(Flywheel.AUTO_SET_3);
            }
        }
    }

    private double calcInitialLaunchVelocity(double turretAngleOffset) {
        double velFlywheel = hardware.flywheel().getBallLaunchVelocity();
        double velRobot = pinpoint.getVelocityVector().getMagnitude() * Math.cos(turretAngleOffset);
        return velRobot + velFlywheel;
    }

    public static Vector3D getTargetLaunchPos() {
        return Globals.allianceColor == Globals.AllianceColor.Red ? new Vector3D(144 - BLUE_SHOOT_ZONE.getX(), BLUE_SHOOT_ZONE.getY(), BLUE_SHOOT_ZONE.getY()) : BLUE_SHOOT_ZONE;
    }

    public void toggleState() {
        if (currentState == ShooterState.REST) setCurrentState(ShooterState.TRACKING);
        else setCurrentState(ShooterState.REST);
    }
}
