package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.ftcontrol.panels.Panels;
import com.bylazar.ftcontrol.panels.configurables.annotations.Configurable;
import com.bylazar.ftcontrol.panels.integration.TelemetryManager;
import com.pedropathing.localization.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.MathUtil.MathUtil;
import revAmped.components.MathUtil.Matrix;
import revAmped.components.MathUtil.Vector3D;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.Logger;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.util.LimeLightStream;

@Configurable
@TeleOp(name = "SubIntakeTestOpenCV", group = "Test")
public class SubIntakeTestOpenCV extends RevAmpedOpMode {
    public static Globals.AllianceColor allianceColor = Globals.AllianceColor.Red;
    private Robot robot;
    private Limelight3A limelight;
    public static boolean detectedSample;
    private LLResult result;
    private Pose samplePosition = new Pose();
    private final double armLengthInches = 7;
    private TelemetryManager telemetryManager;
    private boolean rotateWrist = false;
    private double turretAngle;
    private double hslidePosInches;
    private double wristAngle;
    private RevAmpedGamepad gamepad_1;

    @Override
    public void RevAmpedInit() {
        detectedSample = false;
        robot = new Robot(this, Globals.AllianceColor.Red);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetryManager = Panels.getTelemetry();
        limelight.pipelineSwitch(3);
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
            if (rotateWrist) robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE_PERP);
            else robot.intakeWrist().setCurrentState(IntakeWristServo.WristState.INTAKE);
            robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT, HslideMotor.inchesToTicks(hslidePosInches));
            robot.intakeTurret().intake(turretAngle);
            robot.intakeArm().setCurrentState(IntakeArmServo.ArmState.NEUTRAL_LOWER);
            robot.intakeTilt().setCurrentState(IntakeTiltServo.TiltState.NEUTRAL);
            robot.intakeClaw().open();
        }

        if (gamepad_1.b.isPress()) {
            robot.intake.clawGrab();
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        robot.update();
        telemetryManager.update(telemetry);
        Logger.update(telemetry);
        telemetry.addData("detectedSample", detectedSample);
        if (detectedSample) {
            telemetry.addData("robotPose", samplePosition.getX() + " " + samplePosition.getY());
            telemetryManager.debug("wrist", rotateWrist);
        }

        if (!detectedSample) {
            result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                double[] output = result.getPythonOutput();
                samplePosition = new Pose(output[1] + 3.5, -output[0]);

                if (!Double.isNaN(samplePosition.getY()) && !Double.isNaN(samplePosition.getX())) {
                    Log.e("pos", "pose: " + samplePosition.getX() + " " + samplePosition.getY());
                    Log.e("wrist", String.valueOf(rotateWrist));
                    detectedSample = true;
                    turretAngle = Math.asin(samplePosition.getY() / armLengthInches);
                    wristAngle = rotateWrist ? 0 : 90;
                    hslidePosInches = samplePosition.getX() - Math.cos(turretAngle) * armLengthInches;
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
}
