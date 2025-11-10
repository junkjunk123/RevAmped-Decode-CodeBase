package org.firstinspires.ftc.teamcode.revAmped.mechanisms;

import static org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.allianceColor;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwMotor2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeCommunication;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals.AllianceColor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.RevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Commands.Delay;
import org.firstinspires.ftc.teamcode.revAmped.components.scheduler.Scheduler;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Hood;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Shooter;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.ShootingMechanism;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeUltrasonic;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.Popper;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTable;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TurnTableInternals;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.DecodeRevAmpedAutonomous;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Robot {
    public static Robot INSTANCE;
    private List<LynxModule> allHubs;
    private Scheduler scheduler;
    public TurnTable turnTable;
    public ShootingMechanism<Types.ShooterState> shooter;
    public static EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> allHardwareClasses = buildHardwareClassMap();
    public static HashMap<Class<? extends HwDevice>, HashSet<String>> idMap = buildIDMap();
    public HwVoltageSensor voltageSensor;

    public enum OpModeType implements org.firstinspires.ftc.teamcode.revAmped.components.misc.OpModeType {
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
        opModeType = (OpModeType) opMode.getOpModeType();
        updateOpMode(opMode, allianceColor);
        HardwareMap hardwareMap = opMode.hardwareMap;
        init(hardwareMap);
        INSTANCE = this;
    }

    public Robot(RevAmpedOpMode opMode) {
        this(opMode, Globals.AllianceColor.None);
    }

    public Robot(DecodeRevAmpedAutonomous opMode, Globals.AllianceColor allianceColor, OpModeType opModeType) {
        this.opModeType = opModeType;
        updateOpMode(opMode, allianceColor);
        Globals.OpModeType = opModeType;
        HardwareMap hardwareMap = opMode.hardwareMap;
        init(hardwareMap);
        INSTANCE = this;
    }

    public void updateOpMode(RevAmpedOpMode opMode, Globals.AllianceColor allianceColor) {
        Globals.OpModeType = opModeType;
        Globals.allianceColor = allianceColor;
        scheduler = opMode.scheduler;
    }

    public void init(HardwareMap hardwareMap) {
        allHubs = hardwareMap.getAll(LynxModule.class);
        setBulkReadMode(LynxModule.BulkCachingMode.MANUAL);
        voltageSensor = new HwVoltageSensor(hardwareMap);
        boolean setPositions = !opModeType.isTeleOp();
        shooter = new Shooter(hardwareMap, setPositions, scheduler);
        turnTable = new TurnTable(scheduler, setPositions, hardwareMap, shooter::turntableAutomationControls, voltageSensor);
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
                        turnTable.hardware().intakeColor().getSensorOne(),
                        turnTable.hardware().intakeColor().getSensorTwo()
                )
        ));

        turnTable.hardware().turntable().resetPosition(OpModeCommunication.turntablePos);
        shooter.hardware().flywheel().resetPosition();
        shooter.hardware().turretMotor().resetPosition(OpModeCommunication.turretPos);
        intakeMotor().setVoltageCompensation();
    }

    public void update() {
        clearBulkCache();
        voltageSensor.update();
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
                s.setPosition(s.getPosition());
            }
        }
    }

    public HwMotor2<Types.IntakeState> intakeMotor() {
        return turnTable.hardware().intakeMotor();
    }

    public Hood hoodServo() {
        return shooter.hardware().hoodServo();
    }

    public TurretMotor turretMotor() {
        return shooter.hardware().turretMotor();
    }

    public Flywheel flywheel() {
        return shooter.hardware().flywheel();
    }

    public ColorManager intakeColor() {
        return turnTable.hardware().intakeColor();
    }

    public TableServo tableServo() {
        return turnTable.hardware().turntable();
    }

    public HwServo2<Types.PopperState> popper() {
        return turnTable.hardware().popper();
    }

    public static EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> buildHardwareClassMap() {
        EnumMap<HwDevice.DeviceType, HashSet<Class<? extends HwDevice>>> map = new EnumMap<>(HwDevice.DeviceType.class);
        map.put(HwDevice.DeviceType.COLOR_SENSOR, new HashSet<>(List.of(DecodeColorSensor.class)));
        map.put(HwDevice.DeviceType.MOTOR, new HashSet<>(List.of(Flywheel.class, TurretMotor.class, IntakeMotor.class)));
        map.put(HwDevice.DeviceType.SERVO, new HashSet<>(List.of(HoodServo.class, TableServo.class, Popper.class)));
        map.put(HwDevice.DeviceType.VOLTAGE_SENSOR, new HashSet<>(List.of(HwVoltageSensor.class)));
        return map;
    }

    public static HashMap<Class<? extends HwDevice>, HashSet<String>> buildIDMap() {
        HashMap<Class<? extends HwDevice>, HashSet<String>> map = new HashMap<>();
        map.put(DecodeColorSensor.class, new HashSet<>(List.of("colorOne", "colorTwo")));
        map.put(IntakeUltrasonic.class, new HashSet<>(List.of("intake_ultrasonic")));
        map.put(Flywheel.class, new HashSet<>(List.of("flywheel1", "flywheel2")));
        map.put(TurretMotor.class, new HashSet<>(List.of("turret")));
        map.put(IntakeMotor.class, new HashSet<>(List.of("intake")));
        map.put(HoodServo.class, new HashSet<>(List.of("hood")));
        map.put(Popper.class, new HashSet<>(List.of("popper")));
        map.put(TableServo.class, new HashSet<>(List.of("table")));
        map.put(HwVoltageSensor.class, new HashSet<>(List.of("voltageSensor")));
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

    public void updateLock() {
        if (hoodServo().isUpdateLock() && flywheel().isUpdateLock()) return;

        hoodServo().setUpdateLock(true);
        flywheel().setUpdateLock(true);

        Delay delay = new Delay(() -> turnTable.getNumberOfBalls() == 0, () -> {
            hoodServo().setUpdateLock(false);
            flywheel().setUpdateLock(false);
        }).setMaxTimeToRun(3500);

        if (opModeType.isTeleOp()) {
            delay.setCancellation(() -> turnTable.getCurrentState().getCycleState() != TurnTableInternals.TurnTableCycleState.POP)
                    .setCancellation(() -> shooter.pinpoint().getVelocityVector().getMagnitude() > 6);
        }

        scheduler.add(delay);
    }

    public Delay lockTillLaunchedAll(Runnable andThen) {
        Delay delay = new Delay(() -> turnTable.getNumberOfBalls() == 0, andThen).setMaxTimeToRun(3500);
        if (opModeType == OpModeType.TELEOP) {
            delay.setCancellation(() -> turnTable.getCurrentState().getCycleState() != TurnTableInternals.TurnTableCycleState.POP)
                    .setCancellation(() -> shooter.pinpoint().getVelocityVector().getMagnitude() > 6);
        }
        return delay;
    }
}
