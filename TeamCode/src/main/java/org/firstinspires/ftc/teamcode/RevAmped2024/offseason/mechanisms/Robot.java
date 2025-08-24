package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import static revAmped.components.Misc.Globals.allianceColor;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.hardware.configuration.ServoHubConfiguration;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto.ITDRevAmpedAutonomous;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoMGNServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.DepoTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.HSlideLockServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeWristServo;

import java.util.ArrayList;
import java.util.List;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Drive.TeleOpMecanumDrive;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.Globals.AllianceColor;
import revAmped.components.Misc.RevAmpedOpMode;

public class Robot {
    public static Robot INSTANCE;
    public Intake intake;
    public Depo depo;
    public TeleOpMecanumDrive drivetrain;
    private List<LynxModule> allHubs;
    private final Scheduler scheduler;

    public enum OpModeType implements revAmped.components.Misc.OpModeType {
        TELEOP,
        AUTO,
        TEST;

        @Override
        public boolean isTeleOp() {
            return this == TELEOP;
        }

        @Override
        public boolean isAuto() {
            return this == AUTO;
        }

        @Override
        public boolean isTest() {
            return this == TEST;
        }
    }

    private OpModeType opModeType;

    public Robot(RevAmpedOpMode opMode, Globals.AllianceColor allianceColor) {
        Globals.allianceColor = allianceColor;
        scheduler = opMode.scheduler;
        HardwareMap hardwareMap = opMode.hardwareMap;
        opModeType = (OpModeType) opMode.getOpModeType();
        init(opMode, hardwareMap);
        INSTANCE = this;
    }

    public Robot(ITDRevAmpedAutonomous opMode, Globals.AllianceColor allianceColor, OpModeType opModeType) {
        Globals.allianceColor = allianceColor;
        scheduler = opMode.scheduler;
        HardwareMap hardwareMap = opMode.hardwareMap;
        this.opModeType = opModeType;
        init(opMode, hardwareMap);
        INSTANCE = this;
    }

    public void init(RevAmpedOpMode opMode, HardwareMap hardwareMap) {
        allHubs = hardwareMap.getAll(LynxModule.class);
        intake = new Intake(scheduler, hardwareMap);
        depo = new Depo(scheduler, hardwareMap);
        drivetrain = new TeleOpMecanumDrive(opMode, Constants.DRIVE_DIRECTIONS);
        setBulkReadMode(LynxModule.BulkCachingMode.MANUAL);
    }

    public void update() {
        clearBulkCache();
        intake.update();
        depo.update();
    }

    public void setBulkReadMode(LynxModule.BulkCachingMode mode) {
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(mode);
        }
    }

    public void clearBulkCache() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public void setAllianceColor(AllianceColor allianceColor) {
        Globals.allianceColor = allianceColor;
    }

    public void toggleAllianceColor() {
        int allianceColorOrdinal = allianceColor.ordinal() % 3;
        setAllianceColor(AllianceColor.values()[allianceColorOrdinal]);
    }

    public IntakeClawServo intakeClaw() {
        return intake.servos.claw();
    }

    public IntakeWristServo intakeWrist() {
        return intake.servos.wrist();
    }

    public IntakeTurretServo intakeTurret() {
        return intake.servos.turret();
    }

    public IntakeTiltServo intakeTilt() {
        return intake.servos.tilt();
    }

    public IntakeArmServo intakeArm() {
        return intake.servos.arm();
    }

    public DepoArmServo depoArm() {
        return depo.servos.arm();
    }

    public DepoClawServo depoClaw() {
        return depo.servos.claw();
    }

    public DepoMGNServo depoMGN() {
        return depo.servos.mgn();
    }

    public OpModeType getOpModeType() {
        return opModeType;
    }

    public HslideMotor hslides() {
        return intake.hslides;
    }

    public HSlideLockServo lockServo() {
        return intake.lockServo;
    }

    public VslideMotor vslides() {
        return depo.vslides;
    }
}
