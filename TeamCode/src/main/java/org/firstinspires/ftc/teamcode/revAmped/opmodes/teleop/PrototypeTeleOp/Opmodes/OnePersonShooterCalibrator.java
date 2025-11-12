package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.PopperServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.Arrays;
/*
Eric Zhao
10/20/2025
TeleOp for calibrating hood and motor powers/positions
 */

@TeleOp(name = "OnePersonShooterCalibrator", group = "TeleOp")
@Config
public class OnePersonShooterCalibrator extends DecodeRevAmpedOpMode {
    public static float HoodPositionFar; //input the amount of ticks (1 = 1/255)
    public static float HoodPositionMedium;
    public static float HoodPositionClose;
    public static int MotorVelocityFar;
    public static int MotorVelocityMedium;
    public static int MotorVelocityClose;
    private TeleOpMecanumDrive drivetrain;
    private RevAmpedGamepad gamepad_1;

    // Hardware directly referenced here
    private IntakeMotor intakeMotor;
    private PopperServo popperServo;
    private TableServo turntableServo;
    //private CRTableServo turntableServo;
    private Flywheel flywheel;
    private TurretMotor turretMotor;
    private HoodServo hoodServo;

    private enum ShootState{
        CLOSE,
        MEDIUM,
        FAR;
    }
    private ShootState shootState = ShootState.CLOSE;

    @Override
    public void RevAmpedInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.FORWARD));
        intakeMotor = new IntakeMotor(hardwareMap);
        popperServo = new PopperServo(hardwareMap, true);
        turntableServo = new TableServo(hardwareMap, true);
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
        turretMotor = new TurretMotor(hardwareMap);
        hoodServo = new HoodServo(hardwareMap,true);
    }

    /*
    b - intake/outtake
    y - stop intake

     */

    @Override
    public void RevAmpedLoop() {
        //Telemetry statements
        telemetry.addData("Hood Position", hoodServo.getPosition());
        telemetry.addData("Motor Velocity", flywheel.getVelocity());
        telemetry.addData("ShootState",shootState);
        telemetry.addData("=========Current=========","");
        telemetry.addData("intakeCurrent",intakeMotor.motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("flywheelCurrent", flywheel.motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("leftFrontCurrent", drivetrain.leftFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("leftBackCurrent", drivetrain.leftRear.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("rightFrontCurrent", drivetrain.rightFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("rightBackCurrent", drivetrain.rightRear.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("turretCurrent", turretMotor.motor.getCurrent(CurrentUnit.AMPS));
        //Drivetrain
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        //Intake/Outtake
        if (gamepad_1.b.isPress()){
            if(intakeMotor.getCurrentState() == IntakeMotor.IntakeState.INTAKE){
                intakeMotor.setCurrentState(IntakeMotor.IntakeState.OUTTAKE);
            } else{
                intakeMotor.setCurrentState(IntakeMotor.IntakeState.INTAKE);
            }
        }
        //Stop intake
        if (gamepad_1.y.isPress()) {
            intakeMotor.setCurrentState(IntakeMotor.IntakeState.STOPPED);
        }
        //Popper
        if (gamepad_1.a.isTrue()) {
            popperServo.setCurrentState(PopperServo.PopperState.POP);
        } else{
            popperServo.setCurrentState(PopperServo.PopperState.DOWN);
        }
        //Run/Stop Flywheel
        if (gamepad_1.x.isPress()){
            if(flywheel.getCurrentState() == Types.FlywheelState.STOPPED) {
                flywheel.run((shootState == ShootState.CLOSE) ? MotorVelocityClose : (shootState == ShootState.MEDIUM) ? MotorVelocityMedium : MotorVelocityFar);
            } else{
                flywheel.setCurrentState(Types.FlywheelState.STOPPED);
            }
        }
        //Turret Movement
        if(gamepad_1.right_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.FORWARD);
        } else if (gamepad_1.left_trigger!=0){
            turretMotor.setCurrentState(TurretMotor.TurretState.BACKWARD);
        } else{
            turretMotor.setCurrentState(TurretMotor.TurretState.STOPPED);
        }
        //Hood Positions
        if(gamepad_1.dpad_up.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.FAR);
            shootState = ShootState.FAR;
        }
        if(gamepad_1.dpad_down.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.CLOSE);
            shootState = ShootState.CLOSE;
        }
        if(gamepad_1.dpad_left.isPress()){
            hoodServo.setCurrentState(HoodServo.HoodState.MEDIUM);
        }

    }

    @Override
    public void update() {
        gamepad_1.update();
        flywheel.update();
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