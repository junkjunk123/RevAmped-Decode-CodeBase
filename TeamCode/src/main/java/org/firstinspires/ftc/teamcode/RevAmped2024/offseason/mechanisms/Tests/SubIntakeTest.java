package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import android.util.Log;

import com.bylazar.ftcontrol.panels.Panels;
import com.bylazar.ftcontrol.panels.configurables.annotations.Configurable;
import com.bylazar.ftcontrol.panels.integration.TelemetryManager;
import com.pedropathing.localization.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.ITDColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.VslideMotor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.Vector;
import revAmped.components.MathUtil.Vector2D;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.Logger;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.util.Range;

@Configurable
@TeleOp(name = "SubIntakeTest", group = "Test")
public class SubIntakeTest extends RevAmpedOpMode {
    public static ITDColor.DetectionColor allianceColor = ITDColor.DetectionColor.Red;
    private Robot robot;
    private Limelight3A limelight;
    public static boolean detectedSample;
    private LLResult result;
    private Pose samplePosition = new Pose();
    private final double armLengthInches = 65.0/16.0;
    private TelemetryManager telemetryManager;
    private boolean rotateWrist = false;
    private double turretAngle;
    private double hslidePosInches;
    private float wristAngle;
    private RevAmpedGamepad gamepad_1;
    private boolean drop = false;
    private float wristTarget;
    private List<Pose> poses = new ArrayList<>();

    @Override
    public void RevAmpedInit() {
        detectedSample = false;
        robot = new Robot(this, Globals.AllianceColor.Red);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetryManager = Panels.getTelemetry();
        limelight.pipelineSwitch(7);
        limelight.setPollRateHz(10);
        limelight.start();
        robot.depoArm().setCurrentState(DepoArmServo.ArmState.SPEC_INTAKE);
        robot.intake.setCurrentState(Intake.IntakeState.DROP_TO_HUMAN_PLAYER);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
    }

    @Override
    public void RevAmpedLoop() {
        if (detectedSample) {
            Log.e("sample", "Turret Angle: " + turretAngle + ", Wrist Angle: " + wristAngle + ", Hslide Position Ticks: " + hslidePosInches);
            telemetryManager.debug("inverseKinematicsResult", "Turret Angle: " + turretAngle + ", Wrist Angle: " + wristAngle + ", Hslide Position Ticks: " + hslidePosInches);
        }

        if (gamepad_1.a.isPress()) {
            if (!drop) {
                robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE, wristAngle);
                robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT, HslideMotor.inchesToTicks(hslidePosInches));
                scheduler.add(new Delay(200, () -> {
                    robot.intakeArm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_LOWER);
                    robot.intakeTilt().setCurrentState(IntakeTiltServo.TiltState.NEUTRAL);
                    robot.intakeTurret().intake(turretAngle);
                }));
                robot.intakeClaw().open();
            } else {
                robot.intakeClaw().open();
            }
        }

        if (gamepad_1.b.isPress()) {
            robot.intake.clawGrabAuto();
        }

        if (gamepad_1.x.isPress()) {
            detectedSample = false;
            drop = false;
        }

        if (gamepad_1.y.isPress()) {
            drop = true;
            robot.depo.setCurrentState(Depo.DepoState.SPEC_INTAKE);
            robot.hslides().retract();
            scheduler.add(new Delay(() -> robot.hslides().getCurrentPosition() < HslideMotor.HALF_EXTENSION, () -> robot.intake.setCurrentState(Intake.IntakeState.DROP_TO_HUMAN_PLAYER)));
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        robot.update();
        telemetryManager.update(telemetry);
        Logger.update(telemetry);
        telemetry.addData("detectedSample", detectedSample);
        telemetry.addData("wristTarget",wristTarget);
        if (detectedSample) {
            telemetry.addData("robotPose", samplePosition.getX() + " " + samplePosition.getY());
            telemetry.addData("wrist", rotateWrist);
        }

        for (Pose p : poses) {
            telemetry.addData("corner",p);
        }

        if (!detectedSample) {
            result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                List<LLResultTypes.DetectorResult> output = result.getDetectorResults();
                output.removeIf(i -> !Objects.equals(i.getClassName(), allianceColor.name().toLowerCase()));
                HashMap<LLResultTypes.DetectorResult, Pose> potentialResults = new HashMap<>();

                for (LLResultTypes.DetectorResult u : output) {
                    double[] inches = MathUtil.homographyTransformation(Constants.HOMOGRAPHY_MATRIX.getMatrix(), u.getTargetXPixels(), u.getTargetYPixels());
                    potentialResults.put(u, new Pose(11 - inches[1], 7 - inches[0]));
                }

                LLResultTypes.DetectorResult sample = potentialResults.keySet().stream()
                        .filter(u -> potentialResults.get(u).getX() > 4.25 && potentialResults.get(u).getY() > 0)
                        .reduce((w, z) -> {
                            Pose u = potentialResults.get(w);
                            Pose v = potentialResults.get(z);
                            return u.getY() * u.getY() + u.getX() * u.getX() < v.getX() * v.getX() + v.getY() * v.getY() ? w : z;
                        })
                        .orElse(null);
                samplePosition = potentialResults.get(sample);

                if (samplePosition != null && !Double.isNaN(samplePosition.getY()) && !Double.isNaN(samplePosition.getX())) {
                    Log.e("pos", "pose: " + samplePosition.getX() + " " + samplePosition.getY());
                    Log.e("wrist", String.valueOf(rotateWrist));
                    turretAngle = -Math.asin(samplePosition.getY() / armLengthInches);
                    rotateWrist = computeWristRotation(sample);
                    wristAngle = rotateWrist ? 0 : (float) -Math.PI/2;
                    wristAngle = HwServo2.getPositionFromRad(MathUtil.normalizeAngleHalfPi(wristAngle-turretAngle), Math.toRadians(-90), Math.toRadians(90), Constants.INTAKE_WRIST_INTAKE, Constants.WRIST_INVERTED);
                    hslidePosInches = samplePosition.getX() - Math.cos(turretAngle) * armLengthInches;
                    hslidePosInches += hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION) + hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION) * hslidePosInches / HslideMotor.ticksToInches(Constants.HSLIDE_MAX_EXTENSION);
                    if (hslidePosInches > 6.75 && !rotateWrist) hslidePosInches += MathUtil.sigmoid(Range.scale(hslidePosInches, 6.75, 8, -5.5, 6)) * 3.5;
                    else if (rotateWrist && hslidePosInches > 6.75) hslidePosInches += MathUtil.sigmoid(Range.scale(hslidePosInches, 6.75, 8, -5.5, 6)) * 1.75;
                    if (!Double.isNaN(turretAngle) && !Double.isNaN(wristAngle) && !Double.isNaN(hslidePosInches)) detectedSample = true;
                }
            }
        }
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

    @Override
    public void RevAmpedStop() {
        limelight.pause();
    }

    private boolean computeWristRotation(LLResultTypes.DetectorResult result) {
        List<List<Double>> corners = result.getTargetCorners();
        Double[] xVals = corners.stream().map(u -> u.get(0)).toArray(Double[]::new);
        Double[] yVals = corners.stream().map(u -> u.get(1)).toArray(Double[]::new);
        if (corners.isEmpty()) throw new RuntimeException("no corners");
        double maxX = Arrays.stream(xVals).max(Double::compareTo).orElse(Double.NaN);
        double maxY = Arrays.stream(yVals).max(Double::compareTo).orElse(Double.NaN);
        double minX = Arrays.stream(xVals).min(Double::compareTo).orElse(Double.NaN);
        double minY = Arrays.stream(yVals).min(Double::compareTo).orElse(Double.NaN);
        if (Double.isNaN(maxX)) throw new RuntimeException("corners aint cornering");
        if (Math.abs(maxY - minY) < Math.abs(maxX - minX)) return true;
        return false;
    }

    private float calcWristAngle(LLResultTypes.DetectorResult result) {
        List<List<Double>> cornerPoints = result.getTargetCorners();
        List<Pose> corners = new ArrayList<>();

        for (List<Double> point : cornerPoints) {
            double[] inches = MathUtil.homographyTransformation(Constants.HOMOGRAPHY_MATRIX.getMatrix(), point.get(0), point.get(1));
            corners.add(new Pose(11 - inches[1], 7 - inches[0]));
        }

        corners.sort(Comparator.comparingDouble(Pose::getY));

        poses = corners;

        Vector2D rotated;
        if (!rotateWrist) {
            rotated = new Vector2D(Math.abs(corners.get(2).getX() - corners.get(3).getX()), Math.abs(corners.get(2).getY() - corners.get(3).getY()));
        } else {
            rotated = new Vector2D(Math.abs(corners.get(1).getX() - corners.get(3).getX()), Math.abs(corners.get(1).getY() - corners.get(3).getY()));
        }
        wristTarget = (float) rotated.getTheta();
        telemetry.addData("wristTarget",wristTarget);
        return wristTarget;
    }
}
