package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.math.MathUtil;
import org.firstinspires.ftc.teamcode.revAmped.components.math.PoseDifferentiator;
import org.firstinspires.ftc.teamcode.revAmped.components.math.ProjectileMathWithDrag;
import org.firstinspires.ftc.teamcode.revAmped.components.math.RobotKinematicsCalculator;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector3D;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterHardwareComponents;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.ShooterState;

@Config
public class Shooter implements ShootingMechanism<ShooterState> {
    public final Scheduler scheduler;
    public static boolean trackHood;
    public static boolean trackTurret;

    public final ShooterHardwareComponents hardware;

    private ShooterState currentState;

    public static double FLYWHEEL_MAX_VELOCITY;
    public static double LAUNCH_HEIGHT;
    public static Pose APRIL_TAG_POSE_RED;
    public static Pose APRIL_TAG_POSE_BLUE_POSITIVE;
    public static Pose APRIL_TAG_POSE_RED_NEGATIVE;
    public static Pose APRIL_TAG_POSE_BLUE;
    public static Vector3D BLUE_SHOOT_ZONE;

    public static double BALL_MASS = 0.057152639; //kg
    public static double BALL_LAUNCH_MS = 250;
    private double confidence;

    public static boolean velocityCompensation;
    public static boolean simpleVelComp = true;

    private final PoseDifferentiator robotAcceleration;

    private Localizer pinpoint;

    public static double bluePositiveX = 14; //added 8 inch
    public static double bluePositiveY = 145; //subtracted 4 inch
    public static double blueNegativeX = 15; //added 18 inch
    public static double blueNegativeY = 133;
    public static double redPositiveX = 119;
    public static double redPositiveY = 133;
    public static double redNegativeX = 151;
    public static double redNegativeY = 180;

    public Shooter(HardwareMap hardwareMap, boolean setPositions, Scheduler scheduler, HwVoltageSensor voltageSensor) {
        this.scheduler = scheduler;
        HoodServo hoodServo = new HoodServo(hardwareMap, setPositions);
        TurretMotor turretServo = new TurretMotor(hardwareMap, setPositions ? Types.TurretState.HOLD : Types.TurretState.IDLE);
        Flywheel flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
        hardware = new ShooterHardwareComponents(hoodServo, turretServo, flywheel);
        trackHood = false;
        trackTurret = true;
        velocityCompensation = false;
        hoodServo.setUpdateLock(true);
        flywheel.setUpdateLock(true);
        robotAcceleration = new PoseDifferentiator(Pose::new, () -> pinpoint().getVelocity());
        flywheel.setVoltageCompensation(true);

        APRIL_TAG_POSE_BLUE = new Pose(blueNegativeX, blueNegativeY);
        APRIL_TAG_POSE_BLUE_POSITIVE = new Pose(bluePositiveX, bluePositiveY);
        APRIL_TAG_POSE_RED = new Pose(redPositiveX, redPositiveY);
        APRIL_TAG_POSE_RED_NEGATIVE = new Pose(redNegativeX, redNegativeY);
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
        if (hardware().turretMotor().getCurrentState() == Types.TurretState.TRACKING && !trackTurret)
            hardware().turretMotor().setCurrentState(Types.TurretState.HOLD);
        else if (trackTurret && hardware.turretMotor().getCurrentState() != Types.TurretState.TRACKING)
            hardware().turretMotor().setCurrentState(Types.TurretState.TRACKING);
    }

    public void update() {
        /*
        APRIL_TAG_POSE_BLUE = new Pose(blueNegativeX, blueNegativeY);
        APRIL_TAG_POSE_BLUE_POSITIVE = new Pose(bluePositiveX, bluePositiveY);
        APRIL_TAG_POSE_RED = new Pose(redPositiveX, redPositiveY);
        APRIL_TAG_POSE_RED_NEGATIVE = new Pose(redNegativeX, redNegativeY);
        
         */

        hardware.update();
        robotAcceleration.update();

        if (currentState == ShooterState.TRACKING && (trackHood || trackTurret)) {
            Pose targetPos = Globals.allianceColor == Globals.AllianceColor.Red ? Shooter.APRIL_TAG_POSE_RED : Shooter.APRIL_TAG_POSE_BLUE;
            Pose currentPos = pinpoint().getPose();

            Matrix inverseRotation = MathUtil.rotMatrix(currentPos.getHeading()).transposed();
            Vector robotLinearVel = pinpoint.getVelocityVector().transform(inverseRotation);
            Pose robotVelPose = new Pose(robotLinearVel.getXComponent(), robotLinearVel.getYComponent(), robotLinearVel.getTheta());
            Vector robotVelocity = robotVelPose.getAsVector();
            Pose projectedRobotPose = RobotKinematicsCalculator.getProjectedPoseWithConstantVelocity(
                    currentPos,
                    BALL_LAUNCH_MS / 1000.0,
                    robotVelPose
            );

            double deltaAngle;

            if (trackTurret) {
                if (!velocityCompensation || robotVelocity.getMagnitude() < 8) {
                    Globals.AllianceColor allianceColor = Globals.allianceColor == Globals.AllianceColor.Red ? Globals.AllianceColor.Red : Globals.AllianceColor.Blue;
                    deltaAngle = angleTurretTo(currentPos, targetPos);

                    if ((deltaAngle > 0 && allianceColor == Globals.AllianceColor.Blue) || (deltaAngle < 0 && allianceColor == Globals.AllianceColor.Red)) {
                        Pose targetCorrectedPose = Globals.allianceColor == Globals.AllianceColor.Red ? APRIL_TAG_POSE_RED_NEGATIVE : APRIL_TAG_POSE_BLUE_POSITIVE;
                        deltaAngle = angleTurretTo(currentPos, targetCorrectedPose);
                    }

                    hardware.turretMotor().finetuneAngle(deltaAngle);
                } else if (velocityCompensation && (hardware().flywheel().getCurrentState() == Types.FlywheelState.RUNNING || Robot.INSTANCE.getOpModeType().isTest())) {
                    Vector offset = targetPos.minus(projectedRobotPose).getAsVector();
                    Vector iHat = offset.normalize();
                    Vector jHat = new Vector();
                    jHat.setOrthogonalComponents(-iHat.getYComponent(), iHat.getXComponent());
                    double flywheelLaunchSpeed = hardware.flywheel().getTheoreticalLaunchVelocity();
                    if (hardware.flywheel().getCurrentState() == Types.FlywheelState.STOPPED)
                        flywheelLaunchSpeed = Flywheel.FLYWHEEL_MEDIUM_VELOCITY;
                    Vector invertediHat = new Vector();
                    invertediHat.setOrthogonalComponents(iHat.getYComponent(), iHat.getXComponent());
                    double normalizedTurretNormalComponent = Range.clip(robotVelocity.dot(invertediHat) / flywheelLaunchSpeed, -1, 1);
                    Vector flywheelDirectionVector = new Vector();
                    flywheelDirectionVector.setOrthogonalComponents(
                            Math.sqrt(1 - Math.pow(normalizedTurretNormalComponent, 2)),
                            normalizedTurretNormalComponent
                    );
                    Matrix inverseRotMatrix = new Matrix(new double[][] {
                            {iHat.getXComponent(), iHat.getYComponent()},
                            {jHat.getXComponent(), jHat.getYComponent()}
                    });
                    double targetAngle = flywheelDirectionVector.transform(inverseRotMatrix).getTheta() + Math.PI;
                    deltaAngle = MathUtil.normalizeAnglePi(targetAngle - projectedRobotPose.getHeading());
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
                Pose robotPose = velocityCompensation? projectedRobotPose : currentPos;
                Vector2D targetDisplacements = new Vector2D (
                        robotPose.distanceFrom(new Pose(target.getX(), target.getY())),
                        target.getZ() - LAUNCH_HEIGHT
                );
                double initialVelocity = calcInitialLaunchVelocity(robotVelocity, hardware.flywheel().getTargetVelocity(), deltaAngle);

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
            }
        }
    }

    public void setCurrentState(ShooterState currentState) {
        setCurrentState(currentState, true);
    }

    private void initAction() {
        switch (currentState) {
            case REST -> {
                //hardware.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
                //hardware.hoodServo().setCurrentState(Types.HoodState.REST);

                if (hardware.turretMotor().getCurrentState() != Types.TurretState.HOLD)
                    hardware.turretMotor().setCurrentState(Types.TurretState.HOLD);
                confidence = 0;
            }
            case TRACKING -> {
                //Pose targetPos = Globals.allianceColor == Globals.AllianceColor.Red ? Shooter.APRIL_TAG_POSE_RED : Shooter.APRIL_TAG_POSE_BLUE;
                //double dist = targetPos.distanceFrom(targetPos);
                //hardware.hoodServo().setPreset(dist);

                if (trackTurret && hardware.turretMotor().getPresetState() == TurretMotor.PresetState.RESET)
                    hardware.turretMotor().setCurrentState(Types.TurretState.TRACKING);

                if (hardware.flywheel().getCurrentState() != Types.FlywheelState.RUNNING)
                    hardware.flywheel().setCurrentState(Types.FlywheelState.RUNNING);
            }
        }
    }

    private double angleTurretTo(Pose currentPos, Pose targetPos) {
        Vector offset = new Vector();
        offset.setOrthogonalComponents(targetPos.getX() - currentPos.getX(), targetPos.getY() - currentPos.getY());

        if (simpleVelComp) offset.minus(pinpoint.getVelocityVector().times(BALL_LAUNCH_MS));

        double targetAngle = offset.getTheta() + Math.PI;
        double currentAngle = pinpoint().getPose().getHeading();
        double deltaAngle = MathUtil.normalizeAnglePi(targetAngle - currentAngle);
        return Range.clip(deltaAngle, -TurretMotor.TURRET_RAD_LIMIT, TurretMotor.TURRET_RAD_LIMIT);
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

    private double calcInitialLaunchVelocity(Vector robotVelocity, double flywheelVelocity, double turretAngleOffset) {
        double robotSpeed = robotVelocity.getMagnitude() * Math.cos(turretAngleOffset);
        return robotSpeed + flywheelVelocity * Flywheel.LAUNCH_ETA;
    }

    public static Vector3D getTargetLaunchPos() {
        return Globals.allianceColor == Globals.AllianceColor.Red ? new Vector3D(144 - BLUE_SHOOT_ZONE.getX(), BLUE_SHOOT_ZONE.getY(), BLUE_SHOOT_ZONE.getY()) : BLUE_SHOOT_ZONE;
    }

    public void toggleState() {
        if (currentState == ShooterState.REST) setCurrentState(ShooterState.TRACKING);
        else setCurrentState(ShooterState.REST);
    }

    public void turntableAutomationControls(ShooterState state) {
        switch (state) {
            case REST -> intake();
            case TRACKING -> setCurrentState(ShooterState.TRACKING);
        }
    }

    public void intake() {
        currentState = ShooterState.REST;
        hardware().hoodServo().setCurrentState(Types.HoodState.REST);
        hardware().turretMotor().resetTurret();
        hardware.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
    }
}
