package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Arrays;

@TeleOp(name = "PrototypingTeleOp", group = "TeleOp")
public class PrototypingTeleOp extends DecodeRevAmpedOpMode {
    private TeleOpMecanumDrive drivetrain;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;

    // Hardware directly referenced here
    private IntakeMotor intakeMotor;
    private PopperServo popperServo;
    private TableServo turntableServo;
    private TurretMotor turretMotor;
    private FlywheelMotor flywheelMotor1;
    private FlywheelMotor flywheelMotor2;
    private HoodServo hoodServo;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.FORWARD));
        intakeMotor = new IntakeMotor(hardwareMap);
        popperServo = new PopperServo(hardwareMap, true);
        turntableServo = new TableServo(hardwareMap, true);
        turretMotor = new TurretMotor(hardwareMap);
        flywheelMotor1 = new FlywheelMotor(hardwareMap, "flywheel1", DcMotorSimple.Direction.REVERSE, FlywheelMotor.FlywheelState.STOPPED);
        flywheelMotor2 = new FlywheelMotor(hardwareMap, "flywheel2", DcMotorSimple.Direction.FORWARD, FlywheelMotor.FlywheelState.STOPPED);
        flywheelMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        hoodServo = new HoodServo(hardwareMap,true);
    }

    /*
    b - intake/outtake
    y - stop intake

     */

    @Override
    public void RevAmpedLoop() {
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        if (gamepad_1.b.isPress()){
            if(intakeMotor.getCurrentState() == IntakeMotor.IntakeState.INTAKE){
                intakeMotor.setCurrentState(IntakeMotor.IntakeState.OUTTAKE);
            } else{
                intakeMotor.setCurrentState(IntakeMotor.IntakeState.INTAKE);
            }
        }
        if (gamepad_1.y.isPress()){
            intakeMotor.setCurrentState(IntakeMotor.IntakeState.STOPPED);
        }
        if (gamepad_2.right_bumper.isPress()){
            if (turntableServo.getCurrentState().ordinal()+1 <=2) {
                turntableServo.setCurrentState(TableServo.TableState.values()[turntableServo.getCurrentState().ordinal() + 1]);
            } else{
                //turntableServo.setCurrentState(TableServo.TableState.values()[0]);
            }
        }
        if (gamepad_2.left_bumper.isPress()){
            if (turntableServo.getCurrentState().ordinal()-1 >= 0) {
                turntableServo.setCurrentState(TableServo.TableState.values()[turntableServo.getCurrentState().ordinal() - 1]);
            } else{
                //turntableServo.setCurrentState(TableServo.TableState.values()[2]);
            }
        }
        if (gamepad_2.a.isTrue()) {
            popperServo.setCurrentState(PopperServo.PopperState.POP);
        } else{
            popperServo.setCurrentState(PopperServo.PopperState.DOWN);
        }
        if (gamepad_2.x.isPress()){
            if (flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.STOPPED){
                flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.FAR);
                flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.FAR);
            } else if (flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.FAR){
                flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.STOPPED);
                flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.STOPPED);
            }
        }
        if(gamepad_2.left_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.FORWARD);
        } else if (gamepad_2.right_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.BACKWARD);
        } else{
            turretMotor.setCurrentState(TurretMotor.TurretState.STOPPED);
        }
        if(gamepad_2.dpad_up.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.FAR);
        }
        if(gamepad_2.dpad_down.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.CLOSE);
        }

    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        flywheelMotor1.update();
        flywheelMotor2.update();
        turretMotor.update();
        turntableServo.update();
        popperServo.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}