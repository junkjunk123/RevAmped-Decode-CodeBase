package org.firstinspires.ftc.teamcode.RevAmped.Components;

import static revAmped.components.Misc.Globals.allianceColor;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Shooter;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurnTable;
import org.firstinspires.ftc.teamcode.RevAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto.DecodeRevAmpedAutonomous;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Drive.TeleOpDrivetrainHandler;
import revAmped.components.Drive.TeleOpMecanumDrive;
import revAmped.components.Hardware.HwDevice;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.Globals.AllianceColor;
import revAmped.components.Misc.RevAmpedOpMode;

public class Robot {
    public static Robot INSTANCE;
    private List<LynxModule> allHubs;
    private final Scheduler scheduler;
    public TurnTable turnTable;
    public Shooter shooter;

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

    public final EnumMap<HwDevice.DeviceType, HashSet<HwDevice>> allDevices = new EnumMap<>(HwDevice.DeviceType.class);

    private OpModeType opModeType;

    public Robot(RevAmpedOpMode opMode, Globals.AllianceColor allianceColor) {
        Globals.OpModeType = opModeType;
        Globals.allianceColor = allianceColor;
        scheduler = opMode.scheduler;
        HardwareMap hardwareMap = opMode.hardwareMap;
        opModeType = (OpModeType) opMode.getOpModeType();
        init(hardwareMap);
        INSTANCE = this;
    }

    public Robot(RevAmpedOpMode opMode) {
        this(opMode, Globals.AllianceColor.None);
    }

    public Robot(DecodeRevAmpedAutonomous opMode, Globals.AllianceColor allianceColor, OpModeType opModeType) {
        Globals.OpModeType = opModeType;
        Globals.allianceColor = allianceColor;
        scheduler = opMode.scheduler;
        HardwareMap hardwareMap = opMode.hardwareMap;
        this.opModeType = opModeType;
        init(hardwareMap);
        INSTANCE = this;
    }

    public void init(HardwareMap hardwareMap) {
        allHubs = hardwareMap.getAll(LynxModule.class);
        setBulkReadMode(LynxModule.BulkCachingMode.MANUAL);
        boolean setPositions = !opModeType.isTeleOp();
        turnTable = new TurnTable(scheduler, setPositions, hardwareMap);
        shooter = new Shooter(hardwareMap, setPositions, scheduler);
    }

    public void update() {
        clearBulkCache();
        turnTable.update();
        shooter.update();
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

    public OpModeType getOpModeType() {
        return opModeType;
    }

    public void teleopInitialize() {
        for (HwDevice servo : allDevices.get(HwDevice.DeviceType.SERVO)) {
            if (servo instanceof HwServo2<?> s) {
                s.setPosition(s.getInitialPosition());
            }
        }
    }
}
