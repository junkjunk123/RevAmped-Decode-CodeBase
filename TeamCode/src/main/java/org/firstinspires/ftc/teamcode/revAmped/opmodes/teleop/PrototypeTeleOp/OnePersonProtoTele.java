package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Arrays;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;

@TeleOp(name = "One Person Prototyping TeleOp", group = "TeleOp")
public class OnePersonProtoTele extends DecodeRevAmpedOpMode {
    private TeleOpMecanumDrive drivetrain;
    private RevAmpedGamepad gamepad_1;

    // Hardware directly referenced here
    private IntakeMotor intakeMotor;
    private PopperServo popperServo;
    private TableServo turntableServo;
    private TurretMotor turretMotor;
    private FlywheelMotor flywheelMotor1;
    private FlywheelMotor flywheelMotor2;
    private HoodServo hoodServo;

    private enum ShootState{
        CLOSE,
        FAR;
    }
    private ShootState shootState = ShootState.CLOSE;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
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
        telemetry.addData("Flywheel Power Far",flywheelMotor1.getFarPower());
        telemetry.addData("Flywheel Power Close",flywheelMotor1.getClosePower());
        telemetry.addData("Hood Position Far",hoodServo.getFarPosition());
        telemetry.addData("Hood Position Close",hoodServo.getClosePosition());
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
        //flywheel config
        if (gamepad_1.right_bumper.isPress()){
            if (flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.FAR) {
                flywheelMotor1.incrementFarPower();
                flywheelMotor2.incrementFarPower();
            }
            else if(flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.CLOSE){
                flywheelMotor1.incrementClosePower();
                flywheelMotor2.incrementClosePower();
            }
        }
        if (gamepad_1.left_bumper.isPress()){
            if (flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.FAR) {
                flywheelMotor1.decrementFarPower();
                flywheelMotor2.decrementFarPower();
            }
            else if(flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.CLOSE){
                flywheelMotor1.decrementClosePower();
                flywheelMotor2.decrementClosePower();
            }
        }
        if (gamepad_1.a.isTrue()) {
            popperServo.setCurrentState(PopperServo.PopperState.POP);
        } else{
            popperServo.setCurrentState(PopperServo.PopperState.DOWN);
        }
        if (gamepad_1.x.isPress()){
            if (flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.STOPPED){
                if (shootState == ShootState.FAR) {
                    flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.FAR);
                    flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.FAR);
                } else if (shootState == ShootState.CLOSE){
                    flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.CLOSE);
                    flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.CLOSE);
                }
            } else if (flywheelMotor1.getCurrentState() != FlywheelMotor.FlywheelState.STOPPED){
                flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.STOPPED);
                flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.STOPPED);
            }
        }
        if(gamepad_1.left_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.FORWARD);
        } else if (gamepad_1.right_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.BACKWARD);
        } else{
            turretMotor.setCurrentState(TurretMotor.TurretState.STOPPED);
        }
        if(gamepad_1.dpad_up.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.FAR);
            shootState = ShootState.FAR;
            if(flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.CLOSE){
                flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.FAR);
                flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.FAR);
            }
        }
        if(gamepad_1.dpad_down.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.CLOSE);
            shootState = ShootState.CLOSE;
            if(flywheelMotor1.getCurrentState() == FlywheelMotor.FlywheelState.FAR){
                flywheelMotor1.setCurrentState(FlywheelMotor.FlywheelState.CLOSE);
                flywheelMotor2.setCurrentState(FlywheelMotor.FlywheelState.CLOSE);
            }
        }
        //hood config
        if(gamepad_1.dpad_left.isPress()){
            if (hoodServo.getCurrentState() == HoodServo.HoodState.FAR) {
                hoodServo.decrementFarPosition();
            }
            else if(hoodServo.getCurrentState() == HoodServo.HoodState.CLOSE){
                hoodServo.decrementClosePosition();
            }
        }
        if(gamepad_1.dpad_right.isPress()) {
            if (hoodServo.getCurrentState() == HoodServo.HoodState.FAR) {
                hoodServo.incrementFarPosition();
            } else if (hoodServo.getCurrentState() == HoodServo.HoodState.CLOSE) {
                hoodServo.incrementClosePosition();
            }
        }

    }

    @Override
    public void update() {
        gamepad_1.update();
        flywheelMotor1.update();
        flywheelMotor2.update();
        turretMotor.update();
        turntableServo.update();
        popperServo.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}