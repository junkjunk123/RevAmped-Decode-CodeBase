package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import revAmped.components.drive.TeleOpMecanumDrive;
import revAmped.components.gamepad.RevAmpedGamepad;

@Disabled
@TeleOp(name = "DriveIntakePopperTest", group = "TeleOp")
public class DriveIntakeTest extends DecodeRevAmpedOpMode {
    private TeleOpMecanumDrive drivetrain;
    private RevAmpedGamepad gamepad_1;

    // Hardware directly referenced here
    private DcMotor intakeMotor;
    private Servo popperServo;

    // Servo positions (tweak for your robot)
    private static final double POPPER_DOWN = 0.0;
    private static final double POPPER_UP = 1.0;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);

        drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.FORWARD));

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        //popperServo = hardwareMap.get(Servo.class, "popper");

        //popperServo.setPosition(POPPER_DOWN);
    }

    @Override
    public void RevAmpedLoop() {
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        if (gamepad_1.a.isPress()) {
            if (intakeMotor.getPower() > 0) {
                intakeMotor.setPower(-1); // outtake
            } else {
                intakeMotor.setPower(1);  // intake
            }
        }

        if (gamepad_1.b.isPress()) {
            intakeMotor.setPower(0);
        }

        if (gamepad_1.x.isPress()) {
            //popperServo.setPosition(POPPER_UP);
        }

        if (gamepad_1.y.isPress()) {
            //popperServo.setPosition(POPPER_DOWN);
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}