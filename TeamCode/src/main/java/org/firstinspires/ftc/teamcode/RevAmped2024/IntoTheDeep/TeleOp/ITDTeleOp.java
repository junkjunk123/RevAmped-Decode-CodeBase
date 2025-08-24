package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.GyroHandler;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.SampleDepositionMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.SpecimenMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.HangMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.PTOServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.SlideHangServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;

import revAmped.components.CommandScheduler.Delay;

@Config
@TeleOp(name = "ITD TeleOp", group = "Game")
public class ITDTeleOp extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private IntakeMethods intake;
    private SampleDepositionMethods sampleDepoMethods;
    private SpecimenMethods specimenMethods;
    private ITDRevAmpedGamepad gamepad_1;
    private ITDRevAmpedGamepad gamepad_2;
    private GyroHandler gyro;
    private HangMethods hang;
    private IntakeFourbar intakeFourbar;
    private Telemetry telemetryA;

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP;
    }

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1, 0.3f);
        gamepad_2 = new ITDRevAmpedGamepad(gamepad2, 0.1f);
        gamepad1.reset();
        gamepad2.reset();
        robot = new RobotRevampedITD(this, AutoTeleopCommunication.getAllianceColor(), AutoTeleopCommunication.getStartPose());
        gamepad_1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad_2.sendLEDS(robot.allianceColorToDetectionColor());
        telemetryA = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void RevAmpedStart() {
        robot.init();
        gyro = new GyroHandler(robot.pinpoint, robot.depoWrist, robot.depoTurret);
        DepositionArm depositionArm = new DepositionArm(gyro, robot.depoClaw, robot.depoTilt, robot.depoArm, robot.depoWrist, robot.depoTurret, scheduler.delays);
        intakeFourbar = new IntakeFourbar(robot.intakeArm, robot.intakeTilt, robot.intakeTrapdoor, scheduler.delays);
        intake = new IntakeMethods(depositionArm, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar, telemetryA);
        sampleDepoMethods = new SampleDepositionMethods(depositionArm, gyro, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar);
        specimenMethods = new SpecimenMethods(depositionArm, gyro, robot, scheduler.delays, gamepad_1, gamepad_2, intakeFourbar);
        hang = new HangMethods(robot, scheduler.delays);

        if (robot.vslides.getLimitSwitch().touchingUncached()) {
            robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
        }
    }

    @Override
    public void RevAmpedLoop() {
        // Arcade Drive
        // The 3rd and 4th parameters can be tuned to driver's preference.
        // The 3rd parameter changes the minimum the joystick should move in order to count.
        // The 4th parameter changes the angle between the joysticks to create better strafing and combat unwanted strafing.
        // The 5th parameter is the maxPower (from 0 to 1, keep at 1 unless special cases)
        robot.drive.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f, gamepad_1.y.isToggled());

        //Change alliance colors (for intake)
        if (gamepad_1.dpad_up.isToggled()) {
            intake.setColorSensor(false);
            robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, () -> intake.setColorSensor(true), -0.6);
            robot.setAllianceColor(RobotRevampedITD.AllianceColor.None);
            gamepad_1.setLED(1, 1, 1, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (gamepad_1.dpad_down.isPress()) {
            intake.setColorSensor(false);
            robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, () -> intake.setColorSensor(true), -0.6);
            AutoTeleopCommunication.handleAllianceColor(robot, gamepad_1, gamepad_2);
        }

        //Toggle Between Allowing Yellows
        if (gamepad_1.dpad_left.isPress()) {
            robot.swapAllianceSpecificIntakeOnly();
            gamepad_1.rumble();
            gamepad_2.rumble();
        }

        if (gamepad_1.x.isPress()) {
            if (robot.getCycleState().isBasketDepo()) robot.pinpoint.resetHeading();
            else robot.vslides.resetPosition();
        }

        if (gamepad_1.left_trigger_button.isPress()) {
            sampleDepoMethods.toggleBasketFull();
            gamepad_1.rumble();
            gamepad_2.rumble();
        }

        //Extend Hslide
        if (gamepad_1.left_bumper.isPress()) {
            if (robot.getCycleState().isBasketDepo()) sampleDepoMethods.collapseFromBasketDeposit();
            intake.fullExtend();
        }

        if (gamepad_1.right_bumper.isPress()) {
            if (robot.getCycleState().isBasketDepo()) sampleDepoMethods.collapseFromBasketDeposit();
            if (robot.hslides.getExtentionState() != hSlideMotor.ExtensionState.HALF || robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN) {
                intake.extend(ITDConstants.HSLIDES_MAX/2);
            } else {
                robot.hslides.runToPosition(hSlideMotor.MAX_ETENSION);
            }
        }

        //Intake Goes Down
        if (gamepad_2.b.isPress() && (robot.getCycleState() != RobotRevampedITD.CycleState.LOW_BASKET_DEPO && robot.getCycleState() != RobotRevampedITD.CycleState.HIGH_BASKET_DEPO)) {
            if (intakeFourbar.getCurrentState() != IntakeFourbar.FourbarState.INTAKE) {
                intake.intakeDown();
            }
        }

        //High Basket Drop Depo
        if (gamepad_2.dpad_up.isPress() && !robot.isSpecimen()) {
            sampleDepoMethods.highBasketDepo();
        }

        //Low Basket Drop Depo
        else if (gamepad_2.dpad_right.isPress() && !robot.isSpecimen()) {
            sampleDepoMethods.lowBasketDepo();
        }

        //Specimen Deposit
        else if (gamepad_2.dpad_left.isPress()) {
            if (robot.slideHang.getCurrentState() == SlideHangServo.SlideHangState.ACTIVE) {
                robot.vslides.runToPosition(ITDConstants.VSLIDE_HANG);
            } else {
                if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE || robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER) {
                    specimenMethods.specimenRungDepo();
                }
            }
        } else if (gamepad_2.dpad_down.isPress()) {
            specimenMethods.giveToHumanPlayer();
        }

        //Toggle Wrist Override
        if (gamepad_1.dpad_right.isPress()) {
            sampleDepoMethods.gyroToggle();
        }

        //Vslide Finetune
        if (Math.abs(gamepad_2.right_stick_y) > 0.3f) {
            robot.vslides.runToPosition((int) (gamepad_2.fineTune(-gamepad_2.right_stick_y, ITDConstants.VSLIDE_MAX)*4)+robot.vslides.getTargetPosition());
        }

        //Toggle Claw
        if (gamepad_2.a.isPress()) {
            if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                specimenMethods.downClip();
            } else {
                robot.clawToggle();

                if (robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER && robot.depoClaw.getCurrentState() == DepoClawServo.ClawState.OPEN) {
                    robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_INTAKE);
                    robot.vslides.retract();
                } else if ((robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE || robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER) && robot.depoClaw.getCurrentState() == DepoClawServo.ClawState.CLOSED) {
                    scheduler.add(new Delay(300, () -> {
                        robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG, true, 10);
                        scheduler.add(new Delay(200, () -> specimenMethods.specimenRungDepo()));
                    }));
                }
            }
        }

        //Specimen Intake
        if (gamepad_2.x.isPress()) {
            if (robot.getCycleState() != RobotRevampedITD.CycleState.SPECIMEN_INTAKE) {
                specimenMethods.specimenIntake();
            } else {
                robot.vslides.retract();
                robot.depoClaw.open();
            }
        }

        //Transfer/Collapse
        if (gamepad_2.y.isPress()) {
            sampleDepoMethods.stopGyro();
            if (robot.getCycleState() == RobotRevampedITD.CycleState.SAMPLE_INTAKE || robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT) {
                if (robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT) {
                    intake.transfer();
                    if ((intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.INTAKE || intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.EDGE_SAMPLES)) scheduler.add(new Delay(300, () -> robot.intake.setPower(-1f)));
                } else {
                    intake.transferNoHslides();
                }
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.LOW_BASKET_DEPO || robot.getCycleState() == RobotRevampedITD.CycleState.HIGH_BASKET_DEPO || ((robot.getCycleState() == RobotRevampedITD.CycleState.TRANSFER || robot.getCycleState() == RobotRevampedITD.CycleState.NEUTRAL) && robot.vslides.getTargetPosition() != 0)) {
                sampleDepoMethods.collapseFromBasketDeposit();
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP || robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_STRAIGHT_CLIP) {
                specimenMethods.collapseFromRungDeposit();
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER || robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE) {
                specimenMethods.giveToHumanPlayerToNeutral();
            } else if (robot.intakeArm.getCurrentState() != IntakeFourbarArmServo.IntakeArmState.TRANSFER) {
                intake.transfer();
            }
        }

        //Hslide Finetune Downwards
        if (gamepad_2.right_trigger_button.isPress()) {
            intake.edgeSampleIntake();
        }

        //Outtake
        if (gamepad_2.left_bumper.isPress()) {
            intake.setColorSensor(false);
            robot.intakeTrapdoor.spitSample(robot.colorSensor, robot.intake, () -> intake.setColorSensor(true), -0.6);
        }

        //Trapdoor
        if (gamepad_2.right_bumper.isPress()) {
            intake.toggleAtSideOfSub();
        }

        if (gamepad_2.left_trigger_button.isTrue()) {
            robot.intake.setPower(1f);
        }

        if (gamepad_2.left_trigger_button.isReleased()) {
            robot.intake.setPower(0f);
        }

        if (gamepad_1.b.isPress()) {
            if (robot.slideHang.getCurrentState() == SlideHangServo.SlideHangState.ACTIVE) {
                robot.ptoServo.activate();
                gyro.stop();
                robot.drive.turnOffForPTO();
                robot.vslides.startHang();
            } else {
                hang.hangStage1();
            }
        }

        if (gamepad_1.right_trigger_button.isPress()) {
            if (robot.hslides.getCurrentState() == hSlideMotor.HslideState.OUT)
            intake.transfer();
            else intake.transferNoHslides();
        }

        if (gamepad_1.a.isPress()) {
            if (robot.getCycleState() == RobotRevampedITD.CycleState.HANG) {
                hang.resetStage1();
                robot.ptoServo.setCurrentState(PTOServo.PTOState.INACTIVE);
            } else {
                if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                    specimenMethods.downClip();
                } else {
                    robot.clawToggle();

                    if (robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER && robot.depoClaw.getCurrentState() == DepoClawServo.ClawState.OPEN) {
                        robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_INTAKE);
                        robot.vslides.retract();
                    } else if ((robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE || robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER) && robot.depoClaw.getCurrentState() == DepoClawServo.ClawState.CLOSED) {
                        scheduler.add(new Delay(300, () -> {
                            robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG, true, 10);
                            scheduler.add(new Delay(200, () -> specimenMethods.specimenRungDepo()));
                        }));
                    }
                }
            }
        }

        if (Math.abs(gamepad_2.left_stick_y) > 0.3f) {
            if (robot.getCycleState() == RobotRevampedITD.CycleState.SAMPLE_INTAKE) {
                intake.extend(robot.hslides.getCurrentPosition() + Math.round(-gamepad_2.left_stick_y * 50), intakeFourbar.getCurrentState());
            } else if (robot.getCycleState() == RobotRevampedITD.CycleState.HIGH_BASKET_DEPO || robot.getCycleState() == RobotRevampedITD.CycleState.LOW_BASKET_DEPO){
                sampleDepoMethods.joystickToWristPosition();
            }
        }
    }

    @Override
    public void update() {
        //Update Functions - Called Once at Start of Every Loop3
        robot.update(robot.vslides.getCurrentState() == vSlideMotor.VSlideState.RETRACTION || gyro.getCurrentState() != GyroHandler.GyroState.UNACTIVATED);
        gamepad_1.update();
        gamepad_2.update();
        intake.update();
        sampleDepoMethods.update();
        specimenMethods.update();

        if (robot.slideHang.getCurrentState() == SlideHangServo.SlideHangState.INACTIVE) {
            gyro.update();
        }

        telemetryA.addData("Vslide Left Current (amps)", robot.vslides.leftMotor.motor.getCurrent(CurrentUnit.AMPS));
        telemetryA.addData("Vslide Right Current (amps)", robot.vslides.rightMotor.motor.getCurrent(CurrentUnit.AMPS));
        telemetryA.addData("vslidePos",robot.vslides.getCurrentPosition());
        telemetryA.update();
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
