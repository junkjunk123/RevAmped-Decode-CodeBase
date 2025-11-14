package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.sun.tools.javac.tree.JCTree;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.architecture.TeleOpStateHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.math.FiniteFieldVector;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Hood;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@TeleOp(name = "AccuracyMaxxer", group = "TeleOp")
@Config
public class shooterPositionCalibrator extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drivetrain;
    private Robot robot;
    private RevAmpedGamepad gamepad_1;
    private RevAmpedGamepad gamepad_2;
    public static float hoodPositionFar = 158; //input the amount of ticks (1 = 1/255)
    public static float hoodPositionMedium = 90;
    public static float hoodPositionClose = 10;
    public static double motorVelocityFar = 1200;
    public static double motorVelocityMedium = 1100;
    public static double motorVelocityClose = 900;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.allianceColor);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        gamepad_2 = new RevAmpedGamepad(gamepad2);
        drivetrain = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
    }

    @Override
    public void RevAmpedStart() {
        robot.teleopInitialize();
    }

    @Override
    public void RevAmpedLoop() {
        if(gamepad_1.b.isPress()){
            robot.intakeMotor().setCurrentState((robot.intakeMotor().getCurrentState() == Types.IntakeState.INTAKE) ? Types.IntakeState.OUTTAKE : Types.IntakeState.INTAKE );
        }
        if(gamepad_1.dpad_up.isPress()){
            robot.hoodServo().setCurrentState(Types.HoodState.FAR_PRESET,hoodPositionFar/255);
            robot.flywheel().run(motorVelocityFar);
            //robot.flywheel().setCurrentState(Types.FlywheelState.RUNNING,motorVelocityFar);
        }
        if(gamepad_1.dpad_left.isPress()){
            robot.hoodServo().setCurrentState(Types.HoodState.MED_PRESET,hoodPositionMedium/255);
            robot.flywheel().run(motorVelocityMedium);
            //robot.flywheel().setCurrentState(Types.FlywheelState.RUNNING,motorVelocityMedium);
        }
        if(gamepad_1.dpad_down.isPress()){
            robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET,hoodPositionClose/255);
            robot.flywheel().run(motorVelocityClose);
            //robot.flywheel().setCurrentState(Types.FlywheelState.RUNNING,hoodPositionClose);
        }
        if(gamepad_1.x.isPress()){
            robot.turnTable.switchStateToPop();
            robot.intakeMotor().stop();
        }
        if (gamepad_1.dpad_right.isPress()) {
            robot.turnTable.shootAllAuto(()->{});
        }
        if(gamepad_1.y.isPress()){
            robot.flywheel().setCurrentState(Types.FlywheelState.STOPPED);
        }
        //Telemetry statements
        telemetry.addData("Hood Position", robot.hoodServo().getPosition());
        telemetry.addData("Motor Velocity", robot.flywheel().getVelocity());
        telemetry.addData("ShootState",robot.hoodServo().getCurrentState());
        telemetry.addData("=========Current=========","");
        telemetry.addData("intakeCurrent",robot.intakeMotor().motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("flywheelCurrent", robot.flywheel().motor.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("leftFrontCurrent", drivetrain.leftFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("leftBackCurrent", drivetrain.leftRear.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("rightFrontCurrent", drivetrain.rightFront.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("rightBackCurrent", drivetrain.rightRear.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("turretCurrent", robot.turretMotor().motor.getCurrent(CurrentUnit.AMPS));
    }

    @Override
    public void update() {
        gamepad_1.update();
        gamepad_2.update();
        telemetry.update();
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}
