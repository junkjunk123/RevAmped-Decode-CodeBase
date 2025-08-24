package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class SampleDepositionMethods {
    private final DelayManager delays;
    private final RobotRevampedITD robot;
    private ITDRevAmpedGamepad gamepad1;
    private ITDRevAmpedGamepad gamepad2;
    private final DepositionArm depositionArm;
    private final GyroHandler gyro;
    private final IntakeFourbar intakeFourbar;
    private boolean isBasketFull = false;

    public SampleDepositionMethods(GyroHandler gyro, DepositionArm depositionArm, RobotRevampedITD robot, DelayManager delays, IntakeFourbar intakeFourbar) {
        this.robot = robot;
        this.delays = delays;
        this.gyro = gyro;
        this.depositionArm = depositionArm;
        this.intakeFourbar = intakeFourbar;
    }

    public enum WristPosition {
        LEFT,
        RIGHT,
        PERPENDICULAR,
        REST
    }

    public SampleDepositionMethods(DepositionArm depositionArm, GyroHandler gyro, RobotRevampedITD robot, DelayManager delays, ITDRevAmpedGamepad gamepad1, ITDRevAmpedGamepad gamepad2, IntakeFourbar intakeFourbar) {
        this.delays = delays;
        this.robot = robot;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.gyro = gyro;
        this.depositionArm = depositionArm;
        this.intakeFourbar = intakeFourbar;
    }

    public void highBasketDepo() {
        robot.intake.setPower(0.5f);
        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());
        robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);

        if (isBasketFull) {
            robot.vslides.runToPosition(ITDConstants.VSLIDE_HIGH_BASKET);
        } else {
            robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO-47);
        }

        delays.add(new Delay(() -> robot.vslides.getCurrentPosition() > ITDConstants.VSLIDE_MIN, () -> {
            if (isBasketFull) {
                depositionArm.basketDeposition();
            } else {
                depositionArm.teleopInitialBasketDepo();
            }

            delays.add(new Delay(600, robot.intake::stop));
        }).setMaxTimeToRun(400));
    }

    public void lowBasketDepo() {
        robot.intake.setPower(1f);
        gamepad1.sendLEDS(robot.allianceColorToDetectionColor());
        gamepad2.sendLEDS(robot.allianceColorToDetectionColor());
        robot.setCycleState(RobotRevampedITD.CycleState.LOW_BASKET_DEPO);
        robot.vslides.runToPosition(ITDConstants.VSLIDE_LOW_BASKET);
        delays.add(new Delay(() -> robot.vslides.getCurrentPosition() > ITDConstants.VSLIDE_MIN, () -> {
            depositionArm.teleopInitialBasketDepo();
            delays.add(new Delay(600, robot.intake::stop));
        }).setMaxTimeToRun(400));
    }


    public void collapseFromBasketDeposit() {
        gyro.stop();
        robot.setSpecimen(false);
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
        robot.intakeTrapdoor.close();
        robot.vslides.fullRetract();
        depositionArm.resetDepoArm();
    }

    public void joystickToWristPosition() {
        float x = gamepad2.left_stick_x;
        float y = gamepad2.left_stick_y;

        if (robot.getCycleState().isBasketDepo()) {
            if (y >= 0.85f) {
                if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.HIGH_RUNG);
                } else {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.BASKET);
                }

                if (gyro.getCurrentState() == GyroHandler.GyroState.BASKET)
                    gyro.stop();
            } else if (y <= -0.85f) {
                robot.depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
            } else if (x <= -0.85f) {
                if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.RIGHT_RUNG);
                } else {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.RIGHT_BASKET);
                }

                if (gyro.getCurrentState() == GyroHandler.GyroState.BASKET)
                    gyro.stop();
            }

            if (x >= 0.85f) {
                if (robot.getCycleState() == RobotRevampedITD.CycleState.SPECIMEN_DEPO_UP_CLIP) {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.LEFT_RUNG);
                } else {
                    robot.depoWrist.setCurrentState(DepoWristServo.WristState.LEFT_BASKET);
                }

                if (gyro.getCurrentState() == GyroHandler.GyroState.BASKET)
                    gyro.stop();
            }
        }
    }

    public void gyroToggle() {
        if (gyro.getCurrentState() == GyroHandler.GyroState.RUNG_DOWN) {
            robot.depoWrist.setCurrentState(DepoWristServo.WristState.HIGH_RUNG);
            robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            gyro.stop();
        } else if (gyro.getCurrentState() == GyroHandler.GyroState.BASKET) {
            robot.depoWrist.setCurrentState(DepoWristServo.WristState.BASKET);
            robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            gyro.stop();
        } else if (gyro.getCurrentState() == GyroHandler.GyroState.SPECIMEN_INTAKE) {
            robot.depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
            robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            gyro.stop();
        } else if (robot.depoWrist.getCurrentState() == DepoWristServo.WristState.BASKET) {
            gyro.start(GyroHandler.GyroState.BASKET);
        } else if (robot.depoWrist.getCurrentState() == DepoWristServo.WristState.HIGH_RUNG) {
            gyro.start(GyroHandler.GyroState.RUNG_DOWN);
        } else if (robot.depoArm.getCurrentState() == DepoArmServo.ArmState.SPECIMEN_INTAKE) {
            gyro.start(GyroHandler.GyroState.SPECIMEN_INTAKE);
        }
    }

    public void update() {}

    public void giveToHumanPlayerToNeutral() {
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
    }

    public void basketGyro() {
        if (gyro.getCurrentState() != GyroHandler.GyroState.BASKET) {
            gyro.start(GyroHandler.GyroState.BASKET);
        } else {
            gyro.stop();
        }
    }

    public void stopGyro() {
        gyro.stop();
    }

    public boolean isBasketFull() {
        return isBasketFull;
    }

    public void setBasketFull(boolean basketFull) {
        isBasketFull = basketFull;
    }

    public void toggleBasketFull() {
        isBasketFull = !isBasketFull;

        if (robot.depoArm.getCurrentState() == DepoArmServo.ArmState.HIGH_BASKET || robot.depoArm.getCurrentState() == DepoArmServo.ArmState.HIGH_BASKET_AUTO) {
            if (isBasketFull) {
                depositionArm.basketDeposition();
            } else {
                gyro.stop();
                robot.depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
                delays.add(new Delay(robot.depoTurret.timeToNeutral(), depositionArm::autoBasketDepo));
            }
        }
    }
}
