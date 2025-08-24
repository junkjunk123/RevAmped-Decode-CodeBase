package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.MathUtil.RGBValue;

public class IntakeMethods {
    private final RobotRevampedITD robot;
    private final DelayManager delays;
    private ITDRevAmpedGamepad gamepad_1;
    private ITDRevAmpedGamepad gamepad_2;
    private final DepositionArm depositionArm;
    private final IntakeFourbar intakeFourbar;
    private boolean atSideOfSub = true;
    private boolean colorSensor = true;
    private Telemetry telemetry;

    public IntakeMethods(DepositionArm depositionArm, RobotRevampedITD robot, DelayManager delays, ITDRevAmpedGamepad gamepad_1, ITDRevAmpedGamepad gamepad_2, IntakeFourbar intakeFourbar, Telemetry telemetry) {
        this.robot = robot;
        this.delays = delays;
        this.gamepad_1 = gamepad_1;
        this.gamepad_2 = gamepad_2;
        this.depositionArm = depositionArm;
        this.intakeFourbar = intakeFourbar;
        this.telemetry = telemetry;
    }

    public IntakeMethods(RobotRevampedITD robot, DelayManager delays) {
        this.robot = robot;
        this.delays = delays;
        depositionArm = new DepositionArm(robot, delays);
        intakeFourbar = new IntakeFourbar(robot.intakeArm, robot.intakeTilt, robot.intakeTrapdoor, delays);
    }

    public void extend(int position) {
        extend(position, IntakeFourbar.FourbarState.MIDDLE);
    }

    public void extend(int position, IntakeFourbar.FourbarState fourbarState) {
        robot.intakeTrapdoor.close();
        intakeFourbar.setCurrentState(fourbarState);
        robot.hslides.runToPosition(position);
    }

    public void edgeSampleIntake() {
        robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
        intakeFourbar.setCurrentState(IntakeFourbar.FourbarState.EDGE_SAMPLES);
        clawOpenForIntake();
        robot.intakeTrapdoor.close();
        colorSensor = true;
        robot.intake.setPower(1f);
    }

    public void transfer() {
        colorSensor = true;
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);

        if (atSideOfSub) {
            robot.intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            delays.add(new Delay(300, () -> delays.add(new Delay(50, () -> {
                delays.add(new Delay(300, () -> robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER)));
                robot.hslides.retract();
                delays.add(new Delay(() -> robot.hslides.getCurrentState() == hSlideMotor.HslideState.IN, () -> delays.add(new Delay(150, () -> {
                    robot.intake.setPower(0.4f);
                    clawGrabSample(false);
                    delays.add(new Delay(50, robot.intakeTrapdoor::open));
                }))).setMaxTimeToRun(2000));
            }))));
        } else {
            intakeFourbar.middlePosition();
            delays.add(new Delay(350, () -> {
                robot.intake.setPower(-0.6f);
                delays.add(new Delay(400, robot.intake::stop));
            }));
        }
    }

    public void transferNoHslides() {
        colorSensor = true;
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);

        if (atSideOfSub) {
            intakeFourbar.transfer();
            delays.add(new Delay(200, robot.intake::stop));
            delays.add(new Delay(400, this::clawGrabSample));
        } else {
            intakeFourbar.middlePosition();
            delays.add(new Delay(200, robot.intake::stop));
        }
    }

    public void clawGrabSample() {
        clawGrabSample(true);
    }

    public void clawGrabSample(boolean openTrapdoor) {
        robot.vslides.fullRetract();
        atSideOfSub = true;
        robot.depoClaw.looseClose();
        Delay clawDelay = new Delay(250, () -> {
            gamepad_1.rumble();
            gamepad_2.rumble();

            if (openTrapdoor) {
                robot.intakeTrapdoor.open();
            }

            robot.intake.stop();
            robot.vslides.stopTransfer();
        });
        delays.add(clawDelay);
    }

    public void fullExtend() {
        extend(ITDConstants.HSLIDES_MAX);
    }

    public void intakeEdgeSamples() {
        colorSensor = true;
        clawOpenForIntake();
        intakeFourbar.setCurrentState(IntakeFourbar.FourbarState.EDGE_SAMPLES);
        robot.intake.setPower(1f);
    }

    public void checkSampleColor() {
        DetectionColor sampleColor = robot.colorSensor.getColor();

        if (robot.getAllianceColor() != RobotRevampedITD.AllianceColor.None && (sampleColor == robot.allianceColorToDetectionColor() || (sampleColor == DetectionColor.Yellow && !robot.isAllianceSpecificIntakeOnly())) && sampleColor != DetectionColor.None) {
            telemetry.addData("trying","false");
            robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
            colorSensor = false;
            if (gamepad_1 != null && gamepad_2 != null) {
                gamepad_1.sendGamepadSignals(new RGBValue(1, 1, 1));
                gamepad_2.sendGamepadSignals(new RGBValue(1, 1, 1));
            }

            robot.intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.MIDDLE);
            if (robot.hslides.getCurrentState() != hSlideMotor.HslideState.IN) {
                delays.add(new Delay(300, () -> robot.intake.setPower(-1f)));
                transfer();
            } else {
                transferNoHslides();
            }
        } else if ((robot.getAllianceColor() != RobotRevampedITD.AllianceColor.None && sampleColor != DetectionColor.None)) {
            telemetry.addData("trying","true");
            colorSensor = false;
            intakeFourbar.middlePosition();
            delays.add(new Delay(200, () -> robot.intakeTrapdoor.spitSample(robot.intake, () -> colorSensor = true)));
        }
    }

    public void update() {
        if (colorSensor && robot.intake.getPower() > 0f && robot.getCycleState() == RobotRevampedITD.CycleState.SAMPLE_INTAKE && (intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.INTAKE || intakeFourbar.getCurrentState() == IntakeFourbar.FourbarState.EDGE_SAMPLES)) {
            checkSampleColor();
        }
    }

    public void intakeDown() {
        colorSensor = true;
        robot.setCycleState(RobotRevampedITD.CycleState.SAMPLE_INTAKE);
        clawOpenForIntake();
        intakeFourbar.setCurrentState(IntakeFourbar.FourbarState.INTAKE);
        robot.intake.setPower(1f);
    }

    public void toggleAtSideOfSub() {
         atSideOfSub = !atSideOfSub;
         gamepad_1.rumble();
         gamepad_2.rumble();
    }

    public void clawOpenForIntake() {
        if (robot.vslides.getCurrentState() == vSlideMotor.VSlideState.IN) {
            robot.depoClaw.open();
        }
    }

    public void setColorSensor(boolean colorSensor) {
        this.colorSensor = colorSensor;
    }
}