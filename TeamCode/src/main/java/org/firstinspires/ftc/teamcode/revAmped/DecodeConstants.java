package org.firstinspires.ftc.teamcode.revAmped;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Constants;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.IntakeDistance;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.IntakeThread;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServoV2;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTable;

import java.util.List;

public final class DecodeConstants implements Constants {
    /**
     * List of motor directions for the drive motors in the order: front left, back left, back right, front right
     */
    public static List<DcMotorSimple.Direction> DRIVE_DIRECTIONS = List.of (
            DcMotorSimple.Direction.REVERSE,
            DcMotorSimple.Direction.REVERSE,
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.FORWARD
    );
    public void build() {
        //Popper Constants
        Popper.POP = 27/255f; Popper.NEUTRAL = 102/255f;

        //Flywheel Constants
        Flywheel.P = 0.0005; Flywheel.K_static = 0.05; Flywheel.K_V = 0.00045; Flywheel.L = 0.35;
        Flywheel.FLYWHEEL_FAR_VELOCITY = 1095; Flywheel.FLYWHEEL_NEAR_VELOCITY = 725; Flywheel.FLYWHEEL_MEDIUM_VELOCITY = 875; Flywheel.FLYWHEEL_AUTO_VELOCITY = 780; Flywheel.GEAR_RATIO = 57/93f;
        Flywheel.STALL_TORQUE = 1.47; Flywheel.STALL_CURRENT = 9.2; Flywheel.k_T = 0.0980665 * Flywheel.STALL_TORQUE / Flywheel.STALL_CURRENT;
        Flywheel.MOMENT_OF_INERTIA = 0; Flywheel.LAUNCH_ETA = 0.85; Flywheel.COUNTS_PER_REVOLUTION = 43; Flywheel.RADIUS = 4.094;
        Flywheel.AUTO_PRELOADS = 0; Flywheel.AUTO_SET_1 = 0; Flywheel.AUTO_SET_2 = 0; Flywheel.AUTO_SET_3 = 0;
        Flywheel.VELOCITY_ERROR_THRESHOLD = 0.5;

        //Hood Constants
        HoodServo.HOOD_REST = 8/255f; HoodServo.HOOD_MAX_POS = 0f; HoodServo.HOOD_MIN_POS = 0f; HoodServo.HOOD_MAX_RAD = 0f; HoodServo.HOOD_MIN_RAD = 0f;
        HoodServo.AUTO_PRELOADS = 0f; HoodServo.AUTO_SET_1 = 0f; HoodServo.AUTO_SET_2 = 0f; HoodServo.AUTO_SET_3 = 0f;
        //HOOD POSITIONS FOR TELEOP
        HoodServo.TELE_FAR_PRESET = 78/255f; HoodServo.TELE_NEAR_PRESET = 8/255f; HoodServo.TELE_MED_PRESET = 78/255f;

        //Turret Constants
        TurretMotor.TURRET_LIMITS_ENABLED = true; TurretMotor.TURRET_TICKS_PER_ROTATION = 2200;
        TurretMotor.TURRET_PIDF_COEFFICIENTS = new PIDFCoefficients(0.01, 0, 0, 0.01);
        TurretMotor.LL_DETECTION_BLUE = 0; TurretMotor.LL_DETECTION_RED = 0;
        TurretMotor.AUTO_PRELOADS = 438; TurretMotor.AUTO_SET_1 = 513; TurretMotor.AUTO_SET_2 = 513; TurretMotor.AUTO_SET_3 = 563;
        TurretMotor.FAR_AUTO = 691;
        TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS = -560; TurretMotor.TURRET_POSITIVE_LIMIT_TICKS = 560; TurretMotor.TURRET_RAD_LIMIT = Math.PI / 2;

        //Table Constants
        TableServoV2.BALL2 = 205/255f; TableServoV2.BALL3 = 168/255f; TableServoV2.BALL1 = 242/255f;
        TableServoV2.BALL2_END = 51/255f; TableServoV2.BALL3_END = 14/255f; TableServoV2.BALL1_END = 88/255f;
        TurnTable.SMALL_INTAKE_DELAY = 60; TurnTable.BIG_INTAKE_DELAY = 800;
        IntakeThread.INTAKE_DELAY = 400;

        //ColorSensor Constants
        ColorManager.maxGreenDistanceOne = 60.0f; ColorManager.maxPurpleDistanceOne = 50.0f;
        ColorManager.maxGreenDistanceTwo = 60.0f; ColorManager.maxPurpleDistanceTwo = 60.0f;
        ColorManager.maxPurpleHueOne = 240.0f; ColorManager.minPurpleHueOne = 200.0f;
        ColorManager.maxPurpleHueTwo = 235.0f; ColorManager.minPurpleHueTwo = 195.0f;
        ColorManager.maxGreenHueOne = 165.0f; ColorManager.minGreenHueOne = 155.0f;
        ColorManager.maxGreenHueTwo = 165.0f; ColorManager.minGreenHueTwo = 155.0f;

        IntakeDistance.THRESHOLD_DISTANCE.set(0.5f);
    }
}
