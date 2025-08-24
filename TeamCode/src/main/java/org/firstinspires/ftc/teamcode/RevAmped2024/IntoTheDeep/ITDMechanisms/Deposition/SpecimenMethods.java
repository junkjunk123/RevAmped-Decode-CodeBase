package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.TrapdoorServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class SpecimenMethods {
    private final RobotRevampedITD robot;
    private final DelayManager delays;
    private final DepositionArm depositionArm;
    private final ITDRevAmpedGamepad gamepad1;
    private final ITDRevAmpedGamepad gamepad2;
    private final GyroHandler gyro;
    private final IntakeFourbar intakeFourbar;

    public SpecimenMethods(DepositionArm depositionArm, GyroHandler gyro, RobotRevampedITD robot, DelayManager delays, ITDRevAmpedGamepad gamepad1, ITDRevAmpedGamepad gamepad2, IntakeFourbar intakeFourbar) {
        this.robot = robot;
        this.delays = delays;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.gyro = gyro;
        this.depositionArm = depositionArm;
        this.intakeFourbar = intakeFourbar;
    }

    public void specimenRungDepo() {
        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());

        if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_INTAKE) {
            robot.depoClaw.looseClose();
            robot.vslides.runToPosition(ITDConstants.VSLIDE_DOWN_CLIP, true, 10);
            robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP);
            depositionArm.specimenDepositDownClip();
        }
    }

    public void downClip() {
        depositionArm.clipSpec();
        //delays.add(new Delay(100, robot.vslides::fullRetract));
    }

    public void downClipToStraightClip() {
        robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_STRAIGHT_CLIP);
        robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG, true, 10);
        depositionArm.specimenDepositStraightClip();
    }

    public void straightClipToDownClip() {
        robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP);
        robot.vslides.runToPosition(ITDConstants.VSLIDE_DOWN_CLIP);
        depositionArm.specimenDepositDownClip();
    }

    public void specimenIntake() {
        intakeFourbar.middlePosition();
        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());

        if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP || robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_STRAIGHT_CLIP) {
            robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            robot.depoWrist.setCurrentState(DepoWristServo.WristState.SPEC_INTAKE);
            depositionArm.specimenIntake(true, robot.depoArm::atSpecIntakeForTurret, robot.vslides::fullRetract);
        } else if (robot.getCycleState() == RobotRevampedITD.CycleState.NEUTRAL || robot.getCycleState() == RobotRevampedITD.CycleState.TRANSFER) {
            delays.add(new Delay(200, () -> depositionArm.specimenIntake(false)));
        } else if (robot.getCycleState() == RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER) {
            retractVSlides();
            robot.depoClaw.open();
        }

        robot.setCycleState(RobotRevampedITD.CycleState.SPECIMEN_INTAKE);
        robot.setSpecimen(true);
    }

    public void retractVSlides() {
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
        robot.vslides.retract();
    }

    public void giveToHumanPlayer() {
        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());
        robot.setCycleState(RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER);
        delays.addOptionalDelay(robot.intakeTrapdoor.getCurrentState() == TrapdoorServo.TrapdoorState.OPEN && !robot.depoClaw.isOpen(), new Delay(150, depositionArm::giveToHumanPlayer));
        robot.vslides.runToPosition(ITDConstants.VSLIDE_MIN);
        robot.intakeTrapdoor.open();
        robot.depoClaw.close();
    }

    public void update() {

    }

    public void collapseFromRungDeposit() {
        gyro.stop();
        robot.setSpecimen(false);
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);

        robot.intakeTrapdoor.close();
        intakeFourbar.middlePosition();

        delays.addOptionalDelay(robot.depoClaw.getCurrentState() != DepoClawServo.ClawState.CLOSED, new Delay(300, () -> {
            delays.addOptionalDelay(robot.depoWrist.getCurrentState() == DepoWristServo.WristState.TRANSFER, new Delay(200, () -> {
                robot.depoClaw.close();
                depositionArm.resetDepoArm();
                retractVSlides();
            }));
            robot.depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
        }));
        robot.depoClaw.open();
    }

    public void giveToHumanPlayerToNeutral() {
        gyro.stop();
        robot.setSpecimen(false);
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
        robot.intakeTrapdoor.close();
        depositionArm.resetDepoArm();
    }

    public void prepForSpecDepo() {
        robot.vslides.runToPosition(ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG, true, 10);
        robot.depoTilt.prepForSpecDepo();
        robot.depoWrist.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
    }
}
