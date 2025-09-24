package org.firstinspires.ftc.teamcode.RevAmped.Components;

import static revAmped.components.Misc.Globals.allianceColor;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Flywheel;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.HoodServo;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.Shooter;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.ShootingMechanism;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ArtifactQueuer;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor.IntakeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TableServo;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1.TurnTable;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto.DecodeRevAmpedAutonomous;
import org.firstinspires.ftc.teamcode.RevAmped.Util.Types;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.Hardware.HwDevice;
import revAmped.components.Hardware.HwMotor2;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.Globals.AllianceColor;
import revAmped.components.Misc.RevAmpedOpMode;

public class Robot {
    public static Robot INSTANCE;
    private List<LynxModule> allHubs;
    private final Scheduler scheduler;
    public ArtifactQueuer turnTable;
    public ShootingMechanism<Types.ShooterState> shooter;
    public static EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> allHardwareClasses = buildHardwareClassMap();
    public static HashMap<Class<? extends HwDevice>, HashSet<String>> idMap = buildIDMap();

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

    public enum CycleState {
        UNKNOWN,
        INTAKE,
        SHOOT
    }
    public static CycleState cycleState = CycleState.UNKNOWN;

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
        allDevices.put(HwDevice.DeviceType.MOTOR, new HashSet<>(
                List.of(
                        turnTable.hardware().intakeMotor(),
                        shooter.hardware().turretMotor(),
                        shooter.hardware().flywheel()
                )
        ));
        allDevices.put(HwDevice.DeviceType.SERVO, new HashSet<>(
                List.of(
                        turnTable.hardware().turntable(),
                        turnTable.hardware().popper(),
                        shooter.hardware().hoodServo()
                )
        ));
        allDevices.put(HwDevice.DeviceType.COLOR_SENSOR, new HashSet<>(
                List.of(
                        turnTable.hardware().intakeColor()
                )
        ));
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

    public HwMotor2<Types.IntakeState> intakeMotor() {
        return turnTable.hardware().intakeMotor();
    }

    public HwServo2<Types.HoodState> hoodServo() {
        return shooter.hardware().hoodServo();
    }

    public TurretMotor turretMotor() {
        return shooter.hardware().turretMotor();
    }

    public Flywheel flywheel() {
        return shooter.hardware().flywheel();
    }

    public DecodeColorSensor intakeColor() {
        return turnTable.hardware().intakeColor();
    }

    public HwCRServo2<Types.TableState> tableServo() {
        return turnTable.hardware().turntable();
    }

    public HwServo2<Types.PopperState> popper() {
        return turnTable.hardware().popper();
    }

    public static EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> buildHardwareClassMap() {
        EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> map = new EnumMap<>(HwDevice.DeviceType.class);
        map.put(HwDevice.DeviceType.COLOR_SENSOR, new HashSet<>(List.of(IntakeColor.class)));
        map.put(HwDevice.DeviceType.MOTOR, new HashSet<>(List.of(Flywheel.class, TurretMotor.class, IntakeMotor.class)));
        map.put(HwDevice.DeviceType.SERVO, new HashSet<>(List.of(HoodServo.class, TableServo.class, Popper.class)));
        return map;
    }

    public static HashMap<Class<? extends HwDevice>, HashSet<String>> buildIDMap() {
        HashMap<Class<? extends HwDevice>, HashSet<String>> map = new HashMap<>();
        map.put(IntakeColor.class, new HashSet<>(List.of("intakeColor")));
        map.put(Flywheel.class, new HashSet<>(List.of("flywheel")));
        map.put(TurretMotor.class, new HashSet<>(List.of("turret")));
        map.put(IntakeMotor.class, new HashSet<>(List.of("intake")));
        map.put(HoodServo.class, new HashSet<>(List.of("hood")));
        map.put(Popper.class, new HashSet<>(List.of("popper")));
        map.put(TableServo.class, new HashSet<>(List.of("table")));
        return map;
    }

    public static String[] getID(Class<? extends HwDevice> deviceClass) {
        return idMap.get(deviceClass).toArray(new String[0]);
    }

    public static HashSet<String> getComponentClassNames(HwDevice.DeviceType type) {
        return allHardwareClasses.get(type).stream().map(Class::getCanonicalName).collect(Collectors.toCollection(HashSet::new));
    }

    public static HashSet<String> getComponentNames(HwDevice.DeviceType type) {
        return allHardwareClasses.get(type).stream().map(t -> idMap.get(t)).flatMap(Set::stream).collect(Collectors.toCollection(HashSet::new));
    }
}
