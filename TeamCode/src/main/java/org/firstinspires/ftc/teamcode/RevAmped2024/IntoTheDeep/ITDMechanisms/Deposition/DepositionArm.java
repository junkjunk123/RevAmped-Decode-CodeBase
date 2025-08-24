package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import java.util.function.BooleanSupplier;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class DepositionArm {
    private final DepoClawServo clawServo;
    private final DepoTiltServo tiltServo;
    private final DepoArmServo armServo;
    private final DepoWristServo wristServo;
    private final DepoTurretServo turretServo;
    private final DelayManager delays;
    private final GyroHandler gyro;
    private boolean useGyro = true;

    public DepositionArm(GyroHandler gyro, DepoClawServo clawServo, DepoTiltServo tiltServo, DepoArmServo armServo, DepoWristServo wristServo, DepoTurretServo turretServo, DelayManager delays) {
        this.clawServo = clawServo;
        this.tiltServo = tiltServo;
        this.armServo = armServo;
        this.wristServo = wristServo;
        this.turretServo = turretServo;
        this.delays = delays;
        this.gyro = gyro;
    }

    public DepositionArm(DepoClawServo clawServo, DepoTiltServo tiltServo, DepoArmServo armServo, DepoWristServo wristServo, DepoTurretServo turretServo, DelayManager delays) {
        this(null, clawServo, tiltServo, armServo, wristServo, turretServo, delays);
        useGyro = false;
    }

    public DepositionArm(RobotRevampedITD robot, DelayManager delays) {
        this(new GyroHandler(robot.pinpoint, robot.depoWrist, robot.depoTurret), robot.depoClaw, robot.depoTilt, robot.depoArm, robot.depoWrist, robot.depoTurret, delays);
    }

    public DepositionArm(RobotRevampedITD robot, DelayManager delays, boolean useGyro) {
        this(robot, delays);
        this.useGyro = useGyro;
    }

    public DepositionArm(GyroHandler gyro, RobotRevampedITD robot, DelayManager delays) {
        this(gyro, robot.depoClaw, robot.depoTilt, robot.depoArm, robot.depoWrist, robot.depoTurret, delays);
    }

    public void basketDeposition(BooleanSupplier startWrist) {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        clawServo.close();
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_BASKET);
        delays.add(new Delay(startWrist, () -> startGyro(GyroHandler.GyroState.BASKET)));
    }

    public void basketDeposition() {
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        clawServo.close();
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_BASKET);
        startGyro(GyroHandler.GyroState.BASKET);
    }

    public void autoBasketDepo(float turretPos) {
        autoBasketDepo(turretPos, () -> {});
    }

    public void autoBasketDepo(float turretPos, Runnable action) {
        clawServo.close();
        wristServo.setCurrentState(DepoWristServo.WristState.BASKET_INITIAL);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO);
        tiltServo.sampleDepoAutoDownInitial();
        delays.add(new Delay(armServo::atBasketDepoForTurret, () -> {
            delays.add(new Delay(100, () -> {
                turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_BASKET, turretPos);
                action.run();
            }));
        }).setMaxTimeToRun(600));
    }

    public void autoBasketDepoDelayed(float turretPos, Runnable action) {
        clawServo.close();
        wristServo.setCurrentState(DepoWristServo.WristState.BASKET_INITIAL);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO);
        tiltServo.sampleDepoAutoDownInitial();
        delays.add(new Delay(armServo::atBasketDepoForTurret, () -> {
            delays.add(new Delay(100, () -> {
                turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_BASKET, turretPos);
                action.run();
            }));
        }).setMaxTimeToRun(600));
    }

    public void autoBasketDepoSub(float turretPos, Runnable action) {
        clawServo.close();
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO);
        wristServo.setCurrentState(DepoWristServo.WristState.BASKET_INITIAL);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.BASKET_INITIAL, DepoTiltServo.TiltState.BASKET_INITIAL.getPosition() - 10/255f);
        tiltServo.sampleDepoAutoDownInitial();
        delays.add(new Delay(armServo::atBasketDepoForTurret, () -> {
            turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_BASKET, turretPos);
            delays.add(100, action);
        }).setMaxTimeToRun(600));
    }

    public void autoBasketDepo() {
        stopGyro();
        clawServo.close();
        wristServo.setCurrentState(DepoWristServo.WristState.BASKET_INITIAL);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO);
        tiltServo.sampleDepoAutoDownInitial();
        delays.add(new Delay(armServo::atBasketDepoForTurret, () -> delays.add(new Delay(150, () -> {
            startGyro(GyroHandler.GyroState.BASKET_TURRET, false, true);
        }))).setMaxTimeToRun(600));
    }

    public void teleopInitialBasketDepo() {
        clawServo.close();
        delays.add(new Delay(200, () -> wristServo.setCurrentState(DepoWristServo.WristState.BASKET_INITIAL)));
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_BASKET_AUTO);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.TELEOP_INITIAL);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        delays.add(new Delay(armServo::atBasketDepoForTurret, () -> delays.add(new Delay(150,
                () -> startGyro(GyroHandler.GyroState.BASKET_TURRET, false, true)))).setMaxTimeToRun(600));
    }

    public void startGyro(GyroHandler.GyroState state) {
        if (useGyro) {
            gyro.start(state);
        }
    }

    public void startGyro(GyroHandler.GyroState state, boolean setWristState, boolean setTurretState) {
        if (useGyro) {
            gyro.start(state, setWristState, setTurretState);
        }
    }

    public void stopGyro() {
        if (useGyro) {
            gyro.stop();
        }
    }

    public void specimenDepositStraightClip() {
        stopGyro();
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL, ITDConstants.DEPO_TURRET_SAFE_POSITION);
        wristServo.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
        delays.add(new Delay((turretServo.getPosition() - ITDConstants.DEPO_TURRET_SAFE_POSITION)*255/15, () -> {
            armServo.setCurrentState(DepoArmServo.ArmState.HIGH_RUNG);
            tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_RUNG);
        }));
    }

    public void specimenDepositDownClip() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        armServo.specDepoDownInitial();
        tiltServo.specDepoDownInitial();
        delays.add(new Delay(200, () -> {
            if (useGyro) {
                startGyro(GyroHandler.GyroState.RUNG_DOWN);
            }
        }));
    }

    public void specimenDepositUpClipAuto(boolean turret) {
        stopGyro();
        if (turret) turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_RUNG);
        else turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        wristServo.setCurrentState(DepoWristServo.WristState.HIGH_RUNG);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_RUNG);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_RUNG);
    }

    public void specimenDepositDownClipAuto() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        armServo.specDepoDownInitial();
        tiltServo.specDepoDownInitial();
        delays.add(new Delay(200, () -> {
            turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_RUNG);
        }));
    }

    public void specDepoStraightClip() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL, ITDConstants.DEPO_TURRET_SAFE_POSITION);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_RUNG);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_RUNG);
        delays.add(new Delay((turretServo.getPosition() - ITDConstants.DEPO_TURRET_SAFE_POSITION)*255/15, () -> {
            if (useGyro) {
                startGyro(GyroHandler.GyroState.RUNG_DOWN);
            }
        }));
    }

    public void specimenDepositStraightClipAutoTurret(BooleanSupplier turret) {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.DOWN_CLIP);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL, ITDConstants.DEPO_TURRET_SAFE_POSITION);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_RUNG);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_RUNG);
        delays.add(new Delay((turretServo.getPosition() - ITDConstants.DEPO_TURRET_SAFE_POSITION)*255/15, () -> {
            delays.add(new Delay(turret, () -> turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_RUNG)));
        }));
    }

    public void specimenDepositStraightClipAuto() {
        stopGyro();
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        wristServo.setCurrentState(DepoWristServo.WristState.HIGH_RUNG);
        armServo.setCurrentState(DepoArmServo.ArmState.HIGH_RUNG);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.HIGH_RUNG);
    }

    public void specimenIntake(boolean forceClawOpen) {
        stopGyro();
        delays.add(new Delay(() -> clawServo.reachedOpenState() || !forceClawOpen, () -> {
            tiltServo.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
            turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            armServo.setCurrentState(DepoArmServo.ArmState.SPECIMEN_INTAKE);
            wristServo.setCurrentState(DepoWristServo.WristState.SPEC_INTAKE);
            tiltServo.setCurrentState(DepoTiltServo.TiltState.SPECIMEN_INTAKE);
            clawServo.specimenIntake();
        }));
        clawServo.close();
    }

    public void specimenIntake(boolean forceClawOpen, BooleanSupplier startTurret, Runnable action) {
        stopGyro();
        delays.add(new Delay(() -> clawServo.reachedOpenState() || !forceClawOpen, () -> {
            tiltServo.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
            wristServo.setCurrentState(DepoWristServo.WristState.SPEC_INTAKE);
            turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
            armServo.setCurrentState(DepoArmServo.ArmState.SPECIMEN_INTAKE);
            delays.add(new Delay(startTurret, () -> {
                action.run();
                tiltServo.setCurrentState(DepoTiltServo.TiltState.SPECIMEN_INTAKE);
                clawServo.specimenIntake();
                startGyro(GyroHandler.GyroState.SPECIMEN_INTAKE);
            }));
        }));
    }

    public void specimenIntakeAuto() {
        stopGyro();
        tiltServo.setCurrentState(DepoTiltServo.TiltState.SPECIMEN_INTAKE);
        wristServo.setCurrentState(DepoWristServo.WristState.SPEC_INTAKE);
        armServo.setCurrentState(DepoArmServo.ArmState.SPECIMEN_INTAKE);
        turretServo.setCurrentState(DepoTurretServo.TurretState.AUTO_SPECIMEN_INTAKE);
        clawServo.specimenIntake();
    }

    public void giveToHumanPlayer() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        armServo.setCurrentState(DepoArmServo.ArmState.GIVE_TO_HUMAN_PLAYER);
        delays.add(new Delay(600, () -> {
            tiltServo.setCurrentState(DepoTiltServo.TiltState.GIVE_TO_HUMAN_PLAYER);
            wristServo.setCurrentState(DepoWristServo.WristState.SPEC_INTAKE);
        }));
    }

    public void resetDepoArm() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        delays.addOptionalDelay(turretServo.getCurrentState() == DepoTurretServo.TurretState.NEUTRAL
                        || turretServo.getCurrentState() == DepoTurretServo.TurretState.AUTO_BASKET,
                new Delay(turretServo.timeToNeutral()*2, () -> {
                    armServo.setCurrentState(DepoArmServo.ArmState.NEUTRAL);
                    tiltServo.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
                }));
        clawServo.open();
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);

    }

    public void resetDepoArmAuto() {
        stopGyro();
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        clawServo.open();
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        delays.addOptionalDelay(turretServo.getCurrentState() == DepoTurretServo.TurretState.NEUTRAL
                        || turretServo.getCurrentState() == DepoTurretServo.TurretState.AUTO_BASKET,
                new Delay(turretServo.timeToNeutral(), () -> {
                    armServo.setCurrentState(DepoArmServo.ArmState.NEUTRAL);
                    tiltServo.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
                }));
    }

    public void transfer() {
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        armServo.setCurrentState(DepoArmServo.ArmState.TRANSFER);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.TRANSFER);
        clawServo.open();
        stopGyro();
    }

    public void transfer(boolean openClaw) {
        wristServo.setCurrentState(DepoWristServo.WristState.TRANSFER);
        turretServo.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        armServo.setCurrentState(DepoArmServo.ArmState.TRANSFER);
        tiltServo.setCurrentState(DepoTiltServo.TiltState.TRANSFER);

        if (openClaw) {
            clawServo.open();
        }

        stopGyro();
    }

    public void clipSpec() {
        clawServo.close();
        tiltServo.specDepoDownFinal();
        armServo.specDepoDownFinal();
        delays.add(new Delay(500, clawServo::open));
    }

    public void setUseGyro(boolean useGyro) {
        this.useGyro = useGyro;
    }
}
