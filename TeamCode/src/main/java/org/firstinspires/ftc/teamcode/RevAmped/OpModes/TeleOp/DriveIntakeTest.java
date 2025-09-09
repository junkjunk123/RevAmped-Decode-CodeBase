package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;

@TeleOp(name = "DriveIntakePopperTest", group = "TeleOp")
public class DriveIntakeTest extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
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

        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad_1(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .build();

        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        popperServo = hardwareMap.get(Servo.class, "popper");

        popperServo.setPosition(POPPER_DOWN);
    }

    @Override
    public void RevAmpedLoop() {
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
            popperServo.setPosition(POPPER_UP);
        }

        if (gamepad_1.y.isPress()) {
            popperServo.setPosition(POPPER_DOWN);
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        drivetrain.updateLocalization();
        drivetrain.arcadeDrive();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}