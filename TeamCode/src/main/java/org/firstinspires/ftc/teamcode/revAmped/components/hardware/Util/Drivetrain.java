package org.firstinspires.ftc.teamcode.revAmped.components.hardware.Util;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract class representing a drivetrain system with four motors.
 * It provides methods to set motor powers, modes, and zero power behaviors.
 * The drivetrain can be used in various configurations by extending this class.
 */
public abstract class Drivetrain {
    public final DcMotorEx leftFront, leftRear, rightRear, rightFront;
    protected final List<DcMotorEx> motors;
    protected volatile boolean prevVelZero = true;

    /**
     * Constructs a Drivetrain with specified motor directions.
     * The motors are initialized from the hardware map of the given OpMode.
     *
     * @param op the OpMode from which to get the hardware map
     * @param lfDirection direction for left front motor
     * @param lrDirection direction for left rear motor
     * @param rrDirection direction for right rear motor
     * @param rfDirection direction for right front motor
     */
    public Drivetrain(OpMode op, DcMotorSimple.Direction lfDirection, DcMotorSimple.Direction lrDirection, DcMotorSimple.Direction rrDirection, DcMotorSimple.Direction rfDirection) {
        HardwareMap hardwareMap = op.hardwareMap;
        int numberMissing = 0;

        DcMotorEx rightFront = null;
        try {
            rightFront = hardwareMap.get(DcMotorEx.class, "motor_rf");
        } catch (Exception e) {
            RobotLog.e("missing: motor_rf" + e.getMessage());
            op.telemetry.addLine("missing: motor_rf");
            numberMissing++;
        }
        this.rightFront = rightFront;

        DcMotorEx rightRear = null;
        try {
            rightRear = hardwareMap.get(DcMotorEx.class, "motor_rb");
        } catch (Exception e) {
            RobotLog.e("missing: motor_rb" + e.getMessage());
            op.telemetry.addLine("missing: motor_rb");
            numberMissing++;
        }
        this.rightRear = rightRear;

        DcMotorEx leftFront = null;
        try {
            leftFront = hardwareMap.get(DcMotorEx.class, "motor_lf");
        } catch (Exception e) {
            RobotLog.e("missing: motor_lf" + e.getMessage());
            op.telemetry.addLine("missing: motor_lf");
            numberMissing++;
        }
        this.leftFront = leftFront;

        DcMotorEx leftRear = null;
        try {
            leftRear = hardwareMap.get(DcMotorEx.class, "motor_lb");
        } catch (Exception e) {
            RobotLog.e("missing: motor_lf" + e.getMessage());
            op.telemetry.addLine("missing: motor_lb");
            numberMissing++;
        }
        this.leftRear = leftRear;

        leftFront.setDirection(lfDirection);
        leftRear.setDirection(lrDirection);
        rightRear.setDirection(rrDirection);
        rightFront.setDirection(rfDirection);

        motors = Arrays.asList(leftFront, leftRear, rightRear, rightFront);

        for (DcMotorEx motor : motors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
        }

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setPowers(0,0,0,0);

        if (numberMissing > 0) {
            op.telemetry.addData("drive number missing", numberMissing);
            op.telemetry.update();
        }
    }

    /**
     * Constructs a Drivetrain with specified motor directions.
     * This constructor allows for the use of a list of directions for the motors.
     *
     * @param op the OpMode from which to get the hardware map
     * @param directions a list containing the directions for each motor in the order: left front, left rear, right rear, right front
     */
    public Drivetrain(OpMode op, List<DcMotorSimple.Direction> directions) {
        this(op, directions.get(0), directions.get(1), directions.get(2), directions.get(3));
    }

    /**
     * Sets the run mode for all motors in the drivetrain.
     * @param runMode the run mode to set for the motors, such as RUN_USING_ENCODER, RUN_WITHOUT_ENCODER, etc.
     */
    public void setMode(DcMotor.RunMode runMode) {
        for (DcMotorEx motor : motors) {
            motor.setMode(runMode);
        }
    }

    /**
     * Sets the zero power behavior for all motors in the drivetrain.
     * This determines how the motors behave when their power is set to zero.
     *
     * @param zeroPowerBehavior the zero power behavior to set, such as BRAKE or FLOAT
     */
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    /**
     * Sets the power levels for each of the four motors in the system.
     *
     * @param lf the power level for the left front motor, must be between -1.0 and 1.0
     * @param lr the power level for the left rear motor, must be between -1.0 and 1.0
     * @param rf the power level for the right front motor, must be between -1.0 and 1.0
     * @param rr the power level for the right rear motor, must be between -1.0 and 1.0
     */

    public void setPowers(double lf, double lr, double rf, double rr) {
        if (lf == lr && lr == rf && rf == rr && lf == 0) {
            if (prevVelZero) {
                return;
            }

            prevVelZero = true;
        } else {
            prevVelZero = false;
        }

        if (!Globals.disableDrive) {
            leftFront.setPower(lf);
            leftRear.setPower(lr);
            rightFront.setPower(rf);
            rightRear.setPower(rr);
        }
    }
}
