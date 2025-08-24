package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang;

import com.pedropathing.pathgen.Point;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.PinpointGyro;

import java.util.Arrays;
import java.util.List;

import revAmped.components.GamepadDevices.Button;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Hardware.Drivetrain;

public class TeleOpMecanumDrive extends Drivetrain {
    public final PinpointGyro pinpoint;



    public TeleOpMecanumDrive(OpMode op, PinpointGyro pinpoint, DcMotorSimple.Direction lfDirection, DcMotorSimple.Direction lrDirection, DcMotorSimple.Direction rrDirection, DcMotorSimple.Direction rfDirection) {
        this(op, pinpoint, Arrays.asList(lfDirection, lrDirection, rrDirection, rfDirection));
    }

    public TeleOpMecanumDrive(OpMode op, PinpointGyro pinpoint, List<DcMotorSimple.Direction> directions) {
        super(op, directions);
        this.pinpoint = pinpoint;
    }

    public TeleOpMecanumDrive(OpMode opMode, DcMotorSimple.Direction lfDirection, DcMotorSimple.Direction lrDirection, DcMotorSimple.Direction rrDirection, DcMotorSimple.Direction rfDirection) {
        this(opMode, Arrays.asList(lfDirection, lrDirection, rrDirection, rfDirection));
    }

    public TeleOpMecanumDrive(OpMode opMode, List<DcMotorSimple.Direction> directions) {
        super(opMode, directions);
        pinpoint = null;
    }

    private Point smoothGamepadAngle(double x, double y, double zeroAngle) {
        if (x == 0 || y==0) {
            return new Point(x,y);
        } else if (Math.abs(y/x) < zeroAngle) {
            return new Point(x, 0);
        } else if (Math.abs(x/y) < zeroAngle) {
            return new Point(0,y);
        } else {
            return new Point(x,y);
        }
    }

    public void arcadeDrive(RevAmpedGamepad gamepad1, boolean slowMode, float slowMult, float DZ, float angleZero, float maxPower, boolean fieldCentric) {
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;
        double rx = gamepad1.right_stick_x;
        double robotHeading;

        if (Math.abs(y) > DZ || Math.abs(x) > DZ || Math.abs(rx) > DZ) {
            Point movementVector = smoothGamepadAngle(x, y, angleZero);

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
                robotHeading = pinpoint.getHeading();
                double rotx = x * Math.cos(-robotHeading) - y * Math.sin(-robotHeading);
                double roty = x * Math.sin(-robotHeading) + y * Math.cos(robotHeading);
                x = rotx;
                y = roty;
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

    //Thilan Preferences
    public void arcadeDrive(RevAmpedGamepad gamepad1, boolean fieldCentric) {
        arcadeDrive(gamepad1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f, fieldCentric);
    }

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

    public void turnOffForPTO() {
        MotorConfigurationType motorConfigurationTypeRear = leftRear.getMotorType().clone();
        motorConfigurationTypeRear.setAchieveableMaxRPMFraction(0f);
        leftRear.setMotorType(motorConfigurationTypeRear);

        MotorConfigurationType motorConfigurationTypeFront = leftFront.getMotorType().clone();
        motorConfigurationTypeFront.setAchieveableMaxRPMFraction(0f);
        leftFront.setMotorType(motorConfigurationTypeFront);
    }
}
