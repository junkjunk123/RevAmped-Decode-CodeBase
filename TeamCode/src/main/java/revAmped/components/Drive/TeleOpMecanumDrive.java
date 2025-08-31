package revAmped.components.Drive;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.Range;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;

import revAmped.components.GamepadDevices.Button;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.Util.Drivetrain;
import revAmped.components.MathUtil.Vector2D;

/**
 * TeleOpMecanumDrive is a class that extends the Drivetrain class to provide
 * mecanum drive functionality for teleoperated control of a robot.
 * It supports arcade and tank driving modes, field-centric driving, and slow mode.
 */
public class TeleOpMecanumDrive extends Drivetrain {
    protected DoubleSupplier getHeading;
    protected boolean fieldCentric = false;

    /**
     * Constructor for TeleOpMecanumDrive.
     * @param op the OpMode instance
     * @param lfDirection the direction for the left front motor
     * @param lrDirection the direction for the left rear motor
     * @param rrDirection the direction for the right rear motor
     * @param rfDirection the direction for the right front motor
     * @param follower the Follower instance used for field-centric driving
     */
    public TeleOpMecanumDrive(OpMode op, DcMotorSimple.Direction lfDirection, DcMotorSimple.Direction lrDirection, DcMotorSimple.Direction rrDirection, DcMotorSimple.Direction rfDirection, Follower follower) {
        this(op, Arrays.asList(lfDirection, lrDirection, rrDirection, rfDirection), follower);
    }

    /**
     * Constructor for TeleOpMecanumDrive.
     * @param op the OpMode instance
     * @param directions a list of motor directions for the left front, left rear, right rear, and right front motors
     * @param follower the Follower instance used for field-centric driving
     */
    public TeleOpMecanumDrive(OpMode op, List<DcMotorSimple.Direction> directions, Follower follower) {
        super(op, directions);
        this.getHeading = follower.getPose()::getHeading;
    }

    /**
     * Constructor for TeleOpMecanumDrive without field-centric driving.
     * @param op the OpMode instance
     * @param directions a list of motor directions for the left front, left rear, right rear, and right front motors
     */
    public TeleOpMecanumDrive(OpMode op, List<DcMotorSimple.Direction> directions) {
        super(op, directions);
        this.getHeading = null;
    }

    /**
     * Smooths the gamepad angle to meet human standards
     * @param x the x-value
     * @param y the y-value
     * @param zeroAngle the angle at which the gamepad is considered to be at zero
     * @return a Vector2D representing the smoothed x and y values
     */
    private Vector2D smoothGamepadAngle(double x, double y, double zeroAngle) {
        if (x == 0 || y==0) {
            return new Vector2D(x,y);
        } else if (Math.abs(y/x) < zeroAngle) {
            return new Vector2D(x, 0);
        } else if (Math.abs(x/y) < zeroAngle) {
            return new Vector2D(0,y);
        } else {
            return new Vector2D(x,y);
        }
    }

    /**
     * Arcade drive method for controlling the robot using a gamepad.
     * @param gamepad1 the gamepad used for control
     * @param slowMode whether to enable slow mode
     * @param slowMult the multiplier for slow mode
     * @param DZ deadzone value for joystick input
     * @param angleZero the angle at which the gamepad is considered to be at zero
     * @param maxPower the maximum power for the motors
     */
    public void arcadeDrive(RevAmpedGamepad gamepad1, boolean slowMode, float slowMult, float DZ, float angleZero, float maxPower) {
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        double rx = gamepad1.right_stick_x;
        double robotHeading;

        if (Math.abs(y) > DZ || Math.abs(x) > DZ || Math.abs(rx) > DZ) {
            Vector2D movementVector = smoothGamepadAngle(x, y, angleZero);

            if (Math.abs(y) > DZ) {
                y = -0.5 * Math.tan(movementVector.getY() * 1.12); // Remember, this is reversed!
            } else {
                y = 0;
            }

            if (Math.abs(x) > DZ) {
                x = 0.5 * Math.tan(movementVector.getX() * 1.12);
            } else {
                x = 0;
            }

            if (Math.abs(rx) > DZ) {
                rx = 0.5 * Math.tan(rx * 1.12);
            } else {
                rx = 0;
            }

            if (fieldCentric) {
                robotHeading = getHeading.getAsDouble();
                x = x * Math.cos(-robotHeading) - y * Math.sin(-robotHeading);
                y = x * Math.sin(-robotHeading) + y * Math.cos(robotHeading);
            }

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPow = (y + x + rx) / denominator;
            double backLeftPow = (y - x + rx) / denominator;
            double frontRightPow = (y - x - rx) / denominator;
            double backRightPow = (y + x - rx) / denominator;

            double highestPower = Math.max(Math.max(frontLeftPow, backLeftPow), Math.max(frontRightPow, backRightPow));

            if (highestPower > maxPower) {
                frontLeftPow *= maxPower / highestPower;
                frontRightPow *= maxPower / highestPower;
                backRightPow *= maxPower / highestPower;
                backLeftPow *= maxPower / highestPower;
            }

            if (slowMode) {
                frontLeftPow *= slowMult;
                frontRightPow *= slowMult;
                backRightPow *= slowMult;
                backLeftPow *= slowMult;
            }

            setPowers(frontLeftPow, backLeftPow, frontRightPow, backRightPow);
        } else if (!prevVelZero){
            setPowers(0,0,0,0);
        }
    }

    /**
     * Arcade drive method for controlling the robot using a gamepad with default parameters. These are based off Thilan's preferences.
     * @param gamepad1 the gamepad used for control
     * @param fieldCentric whether to enable field-centric driving
     */
    //Thilan Preferences
    public void arcadeDrive(RevAmpedGamepad gamepad1, boolean fieldCentric) {
        arcadeDrive(gamepad1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        this.fieldCentric = fieldCentric;
    }

    /**
     * Arcade drive method for controlling the robot using a gamepad with default parameters. These are based off Thilan's preferences.
     * @param gamepad1 the gamepad used for control
     * @param slowMode whether to enable slow mode
     */
    public void arcadeDrive(RevAmpedGamepad gamepad1, boolean fieldCentric, boolean slowMode) {
        arcadeDrive(gamepad1, slowMode, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        this.fieldCentric = fieldCentric;
    }

    /**
     * Tank drive method for controlling the robot using a gamepad.
     * @param gamepad1 the gamepad used for control
     * @param slowMode whether to enable slow mode
     * @param slowMult the multiplier for slow mode
     * @param DZ deadzone value for joystick input
     */
    public void tankDrive(Gamepad gamepad1, boolean slowMode, float slowMult, double DZ) {
        float x1 = gamepad1.left_stick_x;
        float y1 = -gamepad1.left_stick_y;
        float x2 = gamepad1.right_stick_x;
        float y2 = -gamepad1.right_stick_y;

        x1 = Button.scaleInput(x1);
        x2 = Button.scaleInput(x2);
        y1 = Button.scaleInput(y1);
        y2 = Button.scaleInput(y2);

        // drive
        if (slowMode) {
            x1 = Range.clip(x1 * slowMult, -1f, 1f);
            x2 = Range.clip(x2 * slowMult, -1f, 1f);
            y1 = Range.clip(y1 * slowMult, -1f, 1f);
            y2 = Range.clip(y2 * slowMult, -1f, 1f);
        }

        //strafe
        boolean b = Math.abs(y2) < Math.abs(x2);
        boolean b1 = Math.abs(y1) < Math.abs(x1);
        if ((b1 && b) || (b1 && x2 == 0) || (b && x1 == 0)) {
            if (Math.abs(x1) >= DZ || Math.abs(x2) >= DZ) {
                float avg = (x1 + x2) / 2f;
                setPowers(avg, -avg, -avg, avg);
            } else {
                if (!prevVelZero) {
                    setPowers(0f, 0f, 0f, 0f);
                }
            }
        }

        //straight tank and turning
        else {
            if (Math.abs(y1) >= DZ || Math.abs(y2) >= DZ) {
                setPowers(y1, y1, y2, y2);
            } else {
                if (!prevVelZero) {
                    setPowers(0f, 0f, 0f, 0f);
                }
            }
        }
    }

    /**
     * Turns off a set of drive motors permanently until settings are reset
     * @param motors an array of motor indices to turn off -- 0 for left front, 1 for left rear, 2 for right rear, 3 for right front
     */
    public void turnOff(int[] motors) {
        for (int i : motors) {
            DcMotor motor = this.motors.get(i);
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(0f);
            motor.setMotorType(motorConfigurationType);
        }
    }

    /**
     * Sets whether the drive is field-centric.
     * @param fieldCentric true if field-centric driving is enabled, false otherwise
     */
    public void setFieldCentric(boolean fieldCentric) {
        this.fieldCentric = fieldCentric;
    }

    /**
     * Checks if the drive is field-centric.
     * @return true if field-centric driving is enabled, false otherwise
     */
    public boolean isFieldCentric() {
        return fieldCentric;
    }
}
