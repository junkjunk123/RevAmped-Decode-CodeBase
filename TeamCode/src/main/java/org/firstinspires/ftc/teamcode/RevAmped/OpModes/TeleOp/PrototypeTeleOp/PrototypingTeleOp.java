package org.firstinspires.ftc.teamcode.RevAmped.OpModes.TeleOp.PrototypeTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import revAmped.components.Drive.TeleOpMecanumDrive;
import revAmped.components.GamepadDevices.RevAmpedGamepad;

@TeleOp(name = "PrototypingTeleOp", group = "TeleOp")
public class PrototypingTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpMecanumDrive drivetrain;
    private RevAmpedGamepad gamepad_1;

    // Hardware directly referenced here
    private DcMotor intakeMotor;
    private PopperServo popperServo;
    private TableServo turntableServo;
    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.FORWARD));
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        popperServo = new PopperServo(hardwareMap, true);
        turntableServo = new TableServo(hardwareMap, true);
    }

    /*
    b - intake/outtake
    y - stop intake

     */

    @Override
    public void RevAmpedLoop() {
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        if (gamepad_1.b.isPress()){
            if(intakeMotor.getPower() > 0){
                intakeMotor.setPower(-1);
            } else{
                intakeMotor.setPower(1);
            }
        }
        if (gamepad_1.y.isPress()){
            intakeMotor.setPower(0);
        }
        if (gamepad_1.right_bumper.isPress()){
            if (turntableServo.getCurrentState().ordinal()+1 <=2) {
                turntableServo.setCurrentState(TableServo.TableState.values()[turntableServo.getCurrentState().ordinal() + 1]);
            } else{
                //turntableServo.setCurrentState(TableServo.TableState.values()[0]);
            }
        }
        if (gamepad_1.left_bumper.isPress()){
            if (turntableServo.getCurrentState().ordinal()-1 >= 0) {
                turntableServo.setCurrentState(TableServo.TableState.values()[turntableServo.getCurrentState().ordinal() - 1]);
            } else{
                //turntableServo.setCurrentState(TableServo.TableState.values()[2]);
            }
        }
        if (gamepad_1.a.isTrue()) {
            popperServo.setCurrentState(PopperServo.PopperState.POP);
        } else{
            popperServo.setCurrentState(PopperServo.PopperState.DOWN);
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