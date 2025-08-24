package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.pathgen.MathFunctions;
import com.pedropathing.pathgen.Vector;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.MathUtil.MathUtil;
import revAmped.util.Range;

public class GyroHandler {
    public final PinpointGyro pinpoint;
    private final DepoWristServo wristServo;
    private final DepoTurretServo turretServo;
    private double targetHeading = 0;

    public enum GyroState {
        RUNG_DOWN,
        RUNG_UP,
        BASKET,
        BASKET_TURRET,
        SPECIMEN_INTAKE,
        UNACTIVATED
    }
    private GyroState currentState = GyroState.UNACTIVATED;

    public GyroHandler(PinpointGyro pinpoint, DepoWristServo wristServo, DepoTurretServo turretServo) {
        this.pinpoint = pinpoint;
        this.wristServo = wristServo;
        this.turretServo = turretServo;
    }

    public GyroHandler(RobotRevampedITD robot) {
        this(robot.pinpoint, robot.depoWrist, robot.depoTurret);
    }

    public double getTargetHeading() {
        return targetHeading;
    }

    public void start(GyroState gyroState) {
        start(gyroState, true, true);
    }

    public void start(GyroState gyroState, boolean setWristState, boolean setTurretState) {
        currentState = gyroState;

        if (gyroState == GyroState.RUNG_DOWN) {
            if (setTurretState) {
                turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_RUNG);
            }
            if (setWristState) {
                wristServo.setCurrentState(DepoWristServo.WristState.GYRO_RUNG_DOWN);
            }
        } else if (gyroState == GyroState.BASKET) {
            if (setTurretState) {
                turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_BASKET);
            }
            if (setWristState) {
                wristServo.setCurrentState(DepoWristServo.WristState.GYRO_BASKET);
            }
        } else if (gyroState == GyroState.SPECIMEN_INTAKE) {
            if (setTurretState) {
                turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_SPECIMEN_INTAKE);
            }
            if (setWristState) {
                wristServo.setCurrentState(DepoWristServo.WristState.GYRO_SPECIMEN_INTAKE);
            }
        } else if (gyroState == GyroState.RUNG_UP) {
            if (setTurretState) {
                turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            }
            if (setWristState) {
                wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
            }
        } else if (gyroState == GyroState.BASKET_TURRET) {
            if (setTurretState) {
                turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_BASKET);
            }

            if (setWristState) {
                wristServo.setCurrentState(DepoWristServo.WristState.GYRO_BASKET);
            }
        }
    }

    public void stop() {
        currentState = GyroState.UNACTIVATED;
    }

    public void update() {
        if (currentState != GyroState.UNACTIVATED) {
            if (currentState == GyroState.BASKET) {
                updateBasket();
            } else if (currentState == GyroState.RUNG_DOWN) {
                updateRung();
            } else if (currentState == GyroState.SPECIMEN_INTAKE) {
                updateSpecimenIntake();
            } else if (currentState == GyroState.BASKET_TURRET) {
                updateBasketTurret();
            }
        }
    }

    private float angleToServoPos(double targetPosition, float right, float left) {
        double target = right + 2 * (targetPosition + Math.PI / 4) * (left - right) / Math.PI;

        if (left >= right) {
            target = Range.clip(target, right, left);
        } else {
            target = Range.clip(target, left, right);
        }

        return (float) target;
    }

    public float angleToServoPos(double rangeRadius, double targetPosition, float right, float left, float rightLimit, float leftLimit) {
        if (targetPosition >= 0) {
            return ITDConstants.DEPO_TURRET_NEUTRAL;
        }

        double target = right + 2 * (targetPosition + rangeRadius) * (left - right) / (4*rangeRadius);

        if (leftLimit >= rightLimit) {
            target = Range.clip(target, rightLimit, leftLimit);
        } else {
            target = Range.clip(target, leftLimit, rightLimit);
        }

        return (float) target;
    }

    public double findAngleToTarget(double current, double target) {
        return target - current;
    }

    private void updateBasket() {
        if (wristServo.getCurrentState() == DepoWristServo.WristState.GYRO_BASKET) {
            wristServo.setCurrentState(DepoWristServo.WristState.GYRO_BASKET, angleToServoPos(MathUtil.normalizeAngleHalfPi(findAngleToTarget(pinpoint.getHeading(), 7 * Math.PI / 4)), ITDConstants.DEPO_WRIST_RIGHT_DIAGONAL_BASKET, ITDConstants.DEPO_WRIST_LEFT_DIAGONAL_BASKET));
        }
    }

    private void updateRung() {
        if (turretServo.getCurrentState() == DepoTurretServo.TurretState.GYRO_RUNG) {
            turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_RUNG, angleToServoPos(MathUtil.normalizeAngleHalfPi(findAngleToTarget(pinpoint.getHeading(), Math.PI)), ITDConstants.DEPO_TURRET_RUNG_RIGHT, ITDConstants.DEPO_TURRET_RUNG_LEFT));
        }
    }

    private void updateSpecimenIntake() {
        if (turretServo.getCurrentState() == DepoTurretServo.TurretState.GYRO_SPECIMEN_INTAKE) {
            turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_SPECIMEN_INTAKE, angleToServoPos(Math.toRadians(Math.abs(ITDConstants.DEPO_TURRET_RUNG_RIGHT - ITDConstants.DEPO_TURRET_NEUTRAL)*355),MathUtil.normalizeAngleHalfPi(findAngleToTarget(pinpoint.getHeading(), 0)), ITDConstants.DEPO_TURRET_RUNG_RIGHT, ITDConstants.DEPO_TURRET_RUNG_LEFT, ITDConstants.DEPO_TURRET_RUNG_RIGHT, ITDConstants.DEPO_TURRET_NEUTRAL + 10/255f));
        }
    }

    public void updateBasketTurret() {
        /*
        if (turretServo.getCurrentState() == DepoTurretServo.TurretState.GYRO_BASKET) {
            turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_BASKET, angleToServoPos(Math.PI/3, MathUtil.normalizeAngleHalfPi(findAngleToTarget(pinpoint.getHeading(), 7 * Math.PI / 4)), ITDConstants.DEPO_TURRET_BASKET_RIGHT, ITDConstants.DEPO_TURRET_BASKET_LEFT, ITDConstants.DEPO_TURRET_BASKET_RIGHT, ITDConstants.DEPO_TURRET_NEUTRAL));
        }

         */
    }

    public void updateBasketTurret(Pose currentPose, Telemetry telemetry) {
        Pose basketPose = new Pose(6, 138, Math.toRadians(-45));
        double deltaX = basketPose.getX() - currentPose.getX();
        double deltaY = basketPose.getY() - currentPose.getY();

        telemetry.addData("deltaX",deltaX);
        telemetry.addData("deltaY",deltaY);

        Vector globalTurretPosition = new Vector();
        globalTurretPosition.setOrthogonalComponents(deltaX, deltaY);
        telemetry.addData("turretAngle",Math.toDegrees(globalTurretPosition.getTheta()));
        double robotTurretAngle = globalTurretPosition.getTheta() - currentPose.getHeading();
        telemetry.addData("turretRobotAngle",Math.toDegrees(robotTurretAngle));
        turretServo.setCurrentState(DepoTurretServo.TurretState.GYRO_BASKET, angleToServoPos(Math.PI/3, MathUtil.normalizeAngleHalfPi(robotTurretAngle), ITDConstants.DEPO_TURRET_BASKET_RIGHT, ITDConstants.DEPO_TURRET_BASKET_LEFT, ITDConstants.DEPO_TURRET_BASKET_RIGHT, ITDConstants.DEPO_TURRET_NEUTRAL));
    }

    public GyroState getCurrentState() {
        return currentState;
    }

    public void toggleUpAndDownRung() {
        if (currentState == GyroState.RUNG_DOWN) {
            start(GyroState.RUNG_UP);
        } else if (currentState == GyroState.RUNG_UP) {
            start(GyroState.RUNG_DOWN);
        }
    }
}