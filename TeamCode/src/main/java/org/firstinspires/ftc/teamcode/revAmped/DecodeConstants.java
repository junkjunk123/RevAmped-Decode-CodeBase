package org.firstinspires.ftc.teamcode.revAmped;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Constants;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.IntakeColor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;

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
        Popper.POP = 75/255f; Popper.NEUTRAL = 112/255f; Popper.UP_VOLTAGE = 2.24f; Popper.DOWN_VOLTAGE = 1.833f;

        //Flywheel Constants
        Flywheel.kStatic = 0.05;
        Flywheel.largeCoefficients = new PIDFCoefficients(0.004, 0, 0.0000002, 0.0003);
        Flywheel.smallCoefficients = new PIDFCoefficients(0.008, 0, 0.00002, 0.0008);
        Flywheel.mediumCoefficients = new PIDFCoefficients(0.005, 0, 0.00002, 0.0003);
        Flywheel.FLYWHEEL_FAR_VELOCITY = 1575; Flywheel.FLYWHEEL_NEAR_VELOCITY = 1300; Flywheel.FLYWHEEL_MEDIUM_VELOCITY = 0; Flywheel.GEAR_RATIO = 1; Flywheel.STALL_TORQUE = 1.47; Flywheel.STALL_CURRENT = 9.2;
        Flywheel.MOMENT_OF_INERTIA = 0; Flywheel.LAUNCH_ETA = 0.9; Flywheel.COUNTS_PER_REVOLUTION = 43; Flywheel.RADIUS = 4.094;
        Flywheel.AUTO_PRELOADS = 0; Flywheel.AUTO_SET_1 = 0; Flywheel.AUTO_SET_2 = 0; Flywheel.AUTO_SET_3 = 0;
        Flywheel.VELOCITY_ERROR_THRESHOLD = 0.5;

        //Hood Constants
        HoodServo.HOOD_REST = 2/255f; HoodServo.HOOD_MAX_POS = 0f; HoodServo.HOOD_MIN_POS = 0f; HoodServo.HOOD_MAX_RAD = 0f; HoodServo.HOOD_MIN_RAD = 0f;
        HoodServo.AUTO_PRELOADS = 0f; HoodServo.AUTO_SET_1 = 0f; HoodServo.AUTO_SET_2 = 0f; HoodServo.AUTO_SET_3 = 0f;
        //HOOD POSITIONS FOR TELEOP
        HoodServo.TELE_FAR_PRESET = 158/255f; HoodServo.TELE_NEAR_PRESET = 110/255f; HoodServo.TELE_MED_PRESET = 125/255;

        //Turret Constants
        TurretMotor.TURRET_LIMITS_ENABLED = true; TurretMotor.TURRET_TICKS_PER_ROTATION = 2200;
        TurretMotor.TURRET_PIDF_COEFFICIENTS = new PIDFCoefficients(0.01, 0, 0, 0.01);
        TurretMotor.LL_DETECTION_BLUE = 0; TurretMotor.LL_DETECTION_RED = 0;
        TurretMotor.AUTO_PRELOADS = 0; TurretMotor.AUTO_SET_1 = 0; TurretMotor.AUTO_SET_2 = 0; TurretMotor.AUTO_SET_3 = 0;
        TurretMotor.TURRET_NEGATIVE_LIMIT_TICKS = -560; TurretMotor.TURRET_POSITIVE_LIMIT_TICKS = 560; TurretMotor.TURRET_RAD_LIMIT = Math.PI / 2;

        //Table Constants
        TableServo.TABLE_PIDF_COEFFICIENTS = new PIDFCoefficients(0.00028, 0, 0.00001, 0.02);
        TableServo.BALL1 = 0; TableServo.BALL2 = 2730; TableServo.BALL3 = 5461;
        TableServo.TICKS_PER_ROTATION = 8192;

        //ColorSensor Constants
        IntakeColor.maxGreenHue = 200; IntakeColor.minGreenHue = 155; IntakeColor.maxPurpleHue = 250; IntakeColor.minPurpleHue = 208;
        IntakeColor.maxPurpleLeftDistance = 80; IntakeColor.maxGreenLeftDistance = 80; IntakeColor.maxPurpleRightDistance = 40; IntakeColor.maxGreenRightDistance = 30;
        DualIntakeColor.LEFT_OFFSET = 10;
    }
}
