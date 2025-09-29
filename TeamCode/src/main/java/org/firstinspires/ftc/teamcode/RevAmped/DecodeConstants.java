package org.firstinspires.ftc.teamcode.RevAmped;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DualIntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TableServo;

import java.util.List;

import revAmped.components.Misc.Constants;

public final class DecodeConstants implements Constants {
    public static List<DcMotorSimple.Direction> DRIVE_DIRECTIONS = List.of (
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE,
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE
    );

    public void build() {
        //Popper Constants
        Popper.POP = 72/255f; Popper.NEUTRAL = 110/255f; Popper.UP_VOLTAGE = 0; Popper.DOWN_VOLTAGE = 0;

        //Flywheel Constants
        Flywheel.FLYWHEEL_MAX_VELOCITY = 0; Flywheel.GEAR_RATIO = 0.75; Flywheel.STALL_TORQUE = 1.47; Flywheel.STALL_CURRENT = 9.2;
        Flywheel.MOMENT_OF_INERTIA = 0; Flywheel.LAUNCH_ETA = 0.9; Flywheel.COUNTS_PER_REVOLUTION = 0; Flywheel.RADIUS = 0;
        Flywheel.AUTO_PRELOADS = 0; Flywheel.AUTO_SET_1 = 0; Flywheel.AUTO_SET_2 = 0; Flywheel.AUTO_SET_3 = 0;
        Flywheel.VELOCITY_ERROR_THRESHOLD = 0.5;

        //Hood Constants
        HoodServo.HOOD_REST = 0f; HoodServo.HOOD_MAX_POS = 0f; HoodServo.HOOD_MIN_POS = 0f; HoodServo.HOOD_MAX_RAD = 0f; HoodServo.HOOD_MIN_RAD = 0f;
        HoodServo.AUTO_PRELOADS = 0f; HoodServo.AUTO_SET_1 = 0f; HoodServo.AUTO_SET_2 = 0f; HoodServo.AUTO_SET_3 = 0f;
        HoodServo.TELE_FAR_PRESET = 0f; HoodServo.TELE_NEAR_PRESET = 0f;

        //Turret Constants
        TurretMotor.TURRET_LIMITS_ENABLED = true; TurretMotor.TURRET_TICKS_PER_ROTATION = 0;
        TurretMotor.TURRET_PIDF_COEFFICIENTS = new PIDFCoefficients(0, 0, 0, 0);
        TurretMotor.LL_DETECTION_BLUE = 0; TurretMotor.LL_DETECTION_RED = 0;
        TurretMotor.AUTO_PRELOADS = 0; TurretMotor.AUTO_SET_1 = 0; TurretMotor.AUTO_SET_2 = 0; TurretMotor.AUTO_SET_3 = 0;

        //Table Constants
        TableServo.TABLE_PIDF_COEFFICIENTS = new PIDFCoefficients(0, 0, 0, 0);
        TableServo.BALL1 = 0; TableServo.BALL2 = 0; TableServo.BALL3 = 0;
        TableServo.TICKS_PER_ROTATION = 0;

        //ColorSensor Constants
        IntakeColor.maxGreenHue = 0; IntakeColor.minGreenHue = 0; IntakeColor.maxPurpleHue = 0; IntakeColor.minPurpleHue = 0;
        DualIntakeColor.LEFT_OFFSET = 0;
    }
}
