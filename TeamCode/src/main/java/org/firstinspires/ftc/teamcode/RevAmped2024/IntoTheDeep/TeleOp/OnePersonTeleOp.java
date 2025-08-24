package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;

import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.localization.localizers.PinpointLocalizer;
import com.pedropathing.pathgen.MathFunctions;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Paths.SampleAutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.GyroHandler;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.SampleDepositionMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.HangMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.PTOServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;
import org.opencv.core.Mat;

@TeleOp(name = "OnePersonTeleOp", group = "Game")
public class OnePersonTeleOp extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private IntakeMethods intake;
    private SampleDepositionMethods sampleDepoMethods;
    private ITDRevAmpedGamepad gamepad_1;
    private ITDRevAmpedGamepad gamepad_2;
    private GyroHandler gyro;
    private IntakeFourbar intakeFourbar;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1, 0.3f);
        gamepad_2 = new ITDRevAmpedGamepad(gamepad2, 0.3f);
        gamepad1.reset();
        gamepad2.reset();
        robot = new RobotRevampedITD(this, AutoTeleopCommunication.getAllianceColor(), AutoTeleopCommunication.getStartPose());
        gamepad_1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad_2.sendLEDS(robot.allianceColorToDetectionColor());
        Constants.setConstants(FConstants.class, LConstants.class);
    }

    @Override
    public void RevAmpedStart() {
        robot.init();
        gyro = new GyroHandler(robot.pinpoint, robot.depoWrist, robot.depoTurret);
        DepositionArm depositionArm = new DepositionArm(gyro, robot.depoClaw, robot.depoTilt, robot.depoArm, robot.depoWrist, robot.depoTurret, scheduler.delays);
        intakeFourbar = new IntakeFourbar(robot.intakeArm, robot.intakeTilt, robot.intakeTrapdoor, scheduler.delays);
        intake = new IntakeMethods(depositionArm, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar, telemetry);
        sampleDepoMethods = new SampleDepositionMethods(depositionArm, gyro, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar);
    }

    @Override
    public void RevAmpedLoop() {
        /*
        double xVel = poseUpdater.getVelocity().getXComponent();
        if (robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN) {
            if (robot.hslides.getCurrentPosition() > 10) robot.hslides.setPower(-0.6f);
            else if ((xVel > 3.5 && xVel < 8) || robot.hslides.getCurrentPosition() > 5) robot.hslides.setPower(-0.2f);
            else if (xVel >= 8) robot.hslides.setPower(-0.6f);
            else if (xVel <= 3.5) robot.hslides.setPower(0f);
        }

         */
        // Arcade Drive
        // The 3rd and 4th parameters can be tuned to driver's preference.
        // The 3rd parameter changes the minimum the joystick should move in order to count.
        // The 4th parameter changes the angle between the joysticks to create better strafing and combat unwanted strafing.
        // The 5th parameter is the maxPower (from 0 to 1, keep at 1 unless special cases)
        robot.drive.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f, false);

        //Change alliance colors (for intake)
        if (gamepad_1.dpad_down.isPress()) {
            AutoTeleopCommunication.handleAllianceColor(robot, gamepad_1, gamepad_2);
        }

        //Extend Hslide
        if (gamepad_1.left_bumper.isPress()) {
            if (robot.getCycleState().isBasketDepo()) {
                robot.depoWrist.setCurrentState(DepoWristServo.WristState.LEFT_BASKET);
            }

            intake.fullExtend();
        }

        if (gamepad_1.right_bumper.isPress()) {
            if (!robot.getCycleState().isBasketDepo()) {
                if (robot.hslides.getExtentionState() != hSlideMotor.ExtensionState.HALF || robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN) {
                    intake.extend(ITDConstants.HSLIDES_MAX / 2);
                } else {
                    robot.hslides.runToPosition(hSlideMotor.MAX_ETENSION);
                }
            } else {
                robot.depoWrist.setCurrentState(DepoWristServo.WristState.RIGHT_BASKET);
            }
        }

        //Intake Goes Down
        if (gamepad_1.b.isPress()) {
            if (!robot.getCycleState().isBasketDepo()) {
                if (intakeFourbar.getCurrentState() != IntakeFourbar.FourbarState.INTAKE) {
                    intake.intakeDown();
                } else {
                    intakeFourbar.teleopClearing();
                    robot.intake.setPower(-0.7f);
                }
            }
        }

        //High Basket Drop Depo
        if (gamepad_1.dpad_up.isPress() && !robot.isSpecimen()) {
            sampleDepoMethods.highBasketDepo();
        }

        //Low Basket Drop Depo
        else if (gamepad_1.dpad_right.isPress() && !robot.isSpecimen()) {
            sampleDepoMethods.lowBasketDepo();
        }

        //Specimen Deposit
        else if (gamepad_1.dpad_left.isPress()) {
            robot.swapAllianceSpecificIntakeOnly();
            gamepad_1.rumble();
            gamepad_2.rumble();
        } else if (gamepad_1.dpad_down.isPress()) {
            if (gyro.getCurrentState() == GyroHandler.GyroState.UNACTIVATED && robot.vslides.getCurrentState() != vSlideMotor.VSlideState.IN) {
                gyro.start(GyroHandler.GyroState.BASKET);
            } else {
                gyro.stop();
            }
        }

        //Toggle Claw
        if (gamepad_1.a.isPress()) {
            robot.clawToggle();
        }

        //Specimen Intake
        if (gamepad_1.x.isPress()) {
            sampleDepoMethods.toggleBasketFull();
        }

        //Transfer/Collapse
        if (gamepad_1.y.isPress()) {
            if (robot.getCycleState() == RobotRevampedITD.CycleState.SAMPLE_INTAKE || robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT) {
                intake.transfer();
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.LOW_BASKET_DEPO || robot.getCycleState() == RobotRevampedITD.CycleState.HIGH_BASKET_DEPO || robot.vslides.getCurrentState() == vSlideMotor.VSlideState.OUT) {
                sampleDepoMethods.collapseFromBasketDeposit();
            } else if (robot.intakeArm.getCurrentState() != IntakeFourbarArmServo.IntakeArmState.TRANSFER) {
                intake.transfer();
            }
        }

        if (Math.abs(gamepad_1.right_trigger) > 0.3f && robot.getCycleState().isBasketDepo()) {
            robot.vslides.runToPosition(-gamepad_1.fineTune(gamepad_1.right_trigger, ITDConstants.VSLIDE_MAX)*4+robot.vslides.getTargetPosition());
        }

        if (gamepad_1.right_trigger_button.isPress() && !robot.getCycleState().isBasketDepo()) {
            intake.edgeSampleIntake();
        }

        if (Math.abs(gamepad_1.left_trigger) > 0.3f) {
            robot.vslides.runToPosition(gamepad_1.fineTune(gamepad_1.left_trigger, ITDConstants.VSLIDE_MAX)*4+robot.vslides.getTargetPosition());
        }
    }

    @Override
    public void update() {
        //Update Functions - Called Once at Start of Every Loop3
        robot.update(true);
        telemetry.addData("alliance",robot.getAllianceColor().name());
        telemetry.addData("allianceSpecificIntakeOnly",robot.isAllianceSpecificIntakeOnly());
        telemetry.addData("limitSwitch",robot.vSwitch.isTouch());
        gamepad_1.update();
        gamepad_2.update();
        intake.update();
        sampleDepoMethods.update();

        if (robot.ptoServo.getCurrentState() == PTOServo.PTOState.INACTIVE) {
            gyro.update();
        }
        telemetry.update();
    }

    @Override
    public void RevAmpedStop() {
        try {
            AutoTeleopCommunication.reset();
            robot.drive.setPowers(0, 0, 0, 0);

            if (robot != null) {
                robot.close();
            }
        } catch (Exception e) {
            //throw new RuntimeException();
        }
    }
}
