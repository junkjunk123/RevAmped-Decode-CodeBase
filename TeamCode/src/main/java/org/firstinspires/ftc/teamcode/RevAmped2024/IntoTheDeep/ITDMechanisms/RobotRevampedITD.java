package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms;

import com.pedropathing.localization.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoTurretServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoWristServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.PinpointGyro;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.PTOServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.SlideHangServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColor.DetectionColor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ITDUltrasonic;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarArmServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbarTiltServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.TrapdoorServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideLockServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.hSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.AutoTeleopCommunication;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;

import java.util.ArrayList;
import java.util.List;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.Hardware.HwAnalogEncoder;
import revAmped.components.Hardware.HwMotor;
import revAmped.components.Hardware.HwServo2;
import revAmped.components.Hardware.HwSlide;
import revAmped.components.Hardware.HwSwitch;
import revAmped.components.MathUtil.LinearFunction;

public class RobotRevampedITD {
    public enum OpModeType implements revAmped.components.Misc.OpModeType {
        TELEOP,
        SAMPLE_AUTO,
        SPECIMEN_AUTO,
        TELEOP_TEST,
        AUTO_TEST;

        @Override
        public boolean isTeleOp() {
            return this == TELEOP || this == TELEOP_TEST;
        }

        @Override
        public boolean isAuto() {
            return this == SAMPLE_AUTO || this == SPECIMEN_AUTO || this == AUTO_TEST;
        }

        @Override
        public boolean isTest() {
            return this == TELEOP_TEST || this == AUTO_TEST;
        }
    }

    public Telemetry telemetry;

    public List<LynxModule> allHubs;

    public IntakeFourbarArmServo intakeArm;
    public IntakeFourbarTiltServo intakeTilt;

    public TrapdoorServo intakeTrapdoor;

    public HwMotor intake;
    public hSlideMotor hslides;
    public hSlideLockServo hslideLock;
    public vSlideMotor vslides;

    public HwSwitch hSwitch;
    public HwSwitch vSwitch;
    public DepoArmServo depoArm; //Primary arm motion - changes between basket and specimen position
    public DepoTiltServo depoTilt; //Up and down motion
    public DepoWristServo depoWrist; //Side to side motion
    public DepoClawServo depoClaw;
    public DepoTurretServo depoTurret;

    public PTOServo ptoServo;
    public SlideHangServo slideHang;
    //public DriveTrainHangServo hang;

    public PinpointGyro pinpoint;

    public ITDColorSensor colorSensor;

    public TeleOpMecanumDrive drive;

    public ITDUltrasonic ultrasonic;
    private Pose startPose;

    public enum AllianceColor {
        None,
        Blue,
        Red
    }

    private AllianceColor allianceColor;

    private boolean specimen = false;

    public List<HwServo2> servos = new ArrayList<>();

    public enum CycleState {
        NEUTRAL(true),
        SAMPLE_INTAKE(true),
        TRANSFER(true),
        LOW_BASKET_DEPO(true),
        HIGH_BASKET_DEPO(true),
        GIVE_TO_HUMAN_PLAYER(false),
        SPECIMEN_INTAKE(false),
        SPECIMEN_DEPO_UP_CLIP(false),
        SPECIMEN_DEPO_STRAIGHT_CLIP(false),
        HANG(false);

        public boolean isBasketDepo() {
            return this == HIGH_BASKET_DEPO || this == LOW_BASKET_DEPO;
        }

        public boolean isSpecimenDepo() {
            return this == SPECIMEN_DEPO_UP_CLIP || this == SPECIMEN_DEPO_STRAIGHT_CLIP;
        }

        boolean isSample;

        CycleState(boolean isSample) {
            this.isSample = isSample;
        }

        public boolean isSample() {
            return isSample;
        }
    }

    private CycleState cycleState = CycleState.NEUTRAL;
    private boolean allianceSpecificIntakeOnly = false;

    private ITDRevAmpedOpMode opMode;
    private OpModeType opModeType;
    private final DelayManager delays;

    public RobotRevampedITD(OpMode op) {
        this(op, new DelayManager());
    }

    public RobotRevampedITD(ITDRevAmpedOpMode op) {
        this(op, AllianceColor.None);
    }

    public RobotRevampedITD(ITDRevAmpedOpMode op, AllianceColor allianceColor) {
        this(op, allianceColor, new Pose());
    }

    public RobotRevampedITD(ITDRevAmpedOpMode op, AllianceColor allianceColor, Pose startPose) {
        this(op, op.getOpModeType(), allianceColor, startPose);
    }

    public RobotRevampedITD(OpMode op, DelayManager delays) {
        this(op, OpModeType.TELEOP_TEST, AllianceColor.None, new Pose(), delays);
    }

    public RobotRevampedITD(OpMode op, OpModeType opModeType) {
        this(op, opModeType, AllianceColor.None, new DelayManager());
    }

    public RobotRevampedITD(OpMode op, OpModeType opModeType, AllianceColor allianceColor, DelayManager delays) {
        this(op, opModeType, allianceColor, new Pose(), delays);
    }

    public RobotRevampedITD(OpMode op, OpModeType opModeType, AllianceColor allianceColor, Pose startPose, DelayManager delays) {
        this.allianceColor = allianceColor;
        int numberMissing = 0;
        HardwareMap hardwareMap = op.hardwareMap;
        this.delays = delays;
        this.telemetry = op.telemetry;
        opMode = null;
        this.opModeType = opModeType;
        this.startPose = startPose;

        hardwareMap.logDevices();

        allHubs = hardwareMap.getAll(LynxModule.class);

        numberMissing = autoInit(numberMissing, op, startPose, true);
        numberMissing = initSensors(hardwareMap, numberMissing, true);
        numberMissing = initializeServos(op, numberMissing, delays);
        numberMissing = initMotors(numberMissing, hardwareMap, delays);

        setBulkReadManual();

        if (numberMissing > 0) {
            telemetry.addData("Missing Devices", numberMissing);
        }
        telemetry.update();
    }

    public RobotRevampedITD(ITDRevAmpedOpMode op, OpModeType opModeType, AllianceColor allianceColor, Pose startPose) {
        this.allianceColor = allianceColor;
        int numberMissing = 0;
        HardwareMap hardwareMap = op.hardwareMap;
        delays = op.scheduler.delays;
        this.telemetry = op.telemetry;
        opMode = op;
        this.opModeType = opModeType;
        this.startPose = startPose;

        hardwareMap.logDevices();

        allHubs = hardwareMap.getAll(LynxModule.class);

        numberMissing = autoInit(numberMissing, op, startPose, true);
        numberMissing = initSensors(hardwareMap, numberMissing, true);

        if (opModeType != OpModeType.TELEOP) {
            numberMissing = initializeServos(op, numberMissing, op.scheduler.delays);
        }

        numberMissing = initMotors(numberMissing, hardwareMap, op.scheduler.delays);

        setBulkReadManual();

        if (numberMissing > 0) {
            telemetry.addData("Missing Devices", numberMissing);
        }
        telemetry.update();
    }

    public int autoInit(int numberMissing, OpMode op, Pose startPose, boolean useI2C) {
        if (useI2C && opModeType != OpModeType.SAMPLE_AUTO && opModeType != OpModeType.SPECIMEN_AUTO) {
            PinpointGyro pinpoint = null;
            try {
                pinpoint = new PinpointGyro(op.hardwareMap, "pinpoint", startPose);
            } catch (Exception e) {
                RobotLog.e("missing: pinpoint" + e.getMessage());
                telemetry.addLine("missing: pinpoint");
                numberMissing++;
            }
            this.pinpoint = pinpoint;
        }

        if (isAuto()) {
            drive = null;
        } else {
            drive = new TeleOpMecanumDrive(op, pinpoint, ITDConstants.DIRECTIONS);
        }

        return numberMissing;
    }

    public int initMotors(int numberMissing, HardwareMap hardwareMap, DelayManager delays) {
        HwMotor intake = null;
        try {
            intake = new HwMotor(hardwareMap, "intake", DcMotorSimple.Direction.REVERSE);
        } catch (Exception e) {
            RobotLog.e("missing: intake" + e.getMessage());
            telemetry.addLine("missing: intake");
            numberMissing++;
        }
        this.intake = intake;

        HwSlide leftMotor = null;
        try {
            leftMotor = new HwSlide(hardwareMap, "vslideMotorLeft", DcMotorSimple.Direction.REVERSE, vSwitch);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorLeft" + e.getMessage());
            telemetry.addLine("missing: vslideMotorLeft");
            numberMissing++;
        }

        HwSlide rightMotor = null;
        try {
            rightMotor = new HwSlide(hardwareMap, "vslideMotorRight", DcMotorSimple.Direction.FORWARD, vSwitch);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorRight" + e.getMessage());
            telemetry.addLine("missing: vslideMotorRight");
            numberMissing++;
        }

        vslides = new vSlideMotor(leftMotor, rightMotor, vSwitch, true, delays);

        if (opModeType != OpModeType.TELEOP) {
            hSlideMotor hslides = null;
            try {
                hslides = new hSlideMotor(hardwareMap, "hslides", DcMotorSimple.Direction.FORWARD, this.hSwitch, this::getCycleState, vslides::getCurrentState, intakeArm::getCurrentState, this.depoClaw, delays,true, hslideLock);
            } catch (Exception e) {
                RobotLog.e("missing: hslides" + e.getMessage());
                telemetry.addLine("missing: hslides");
                numberMissing++;
            }
            this.hslides = hslides;
        }

        return numberMissing;
    }

    public RobotRevampedITD(ITDRevAmpedOpMode op, boolean initMotors, boolean initServos, boolean initSensors, boolean autoInit) {
        this.allianceColor = AllianceColor.None;
        int numberMissing = 0;
        HardwareMap hardwareMap = op.hardwareMap;
        delays = op.scheduler.delays;
        this.telemetry = op.telemetry;
        opMode = op;
        opModeType = opMode.getOpModeType();

        hardwareMap.logDevices();

        allHubs = hardwareMap.getAll(LynxModule.class);

        if (autoInit) {
            numberMissing = autoInit(numberMissing, op, new Pose(), initSensors);
        }

        numberMissing = initSensors(hardwareMap, numberMissing, initSensors);

        if (initServos) {
            numberMissing = initializeServos(op, numberMissing, op.scheduler.delays);
        }

        if (initMotors) {
            numberMissing = initMotors(numberMissing, hardwareMap, op.scheduler.delays);
        }

        setBulkReadManual();

        if (numberMissing > 0) {
            telemetry.addData("Missing Devices", numberMissing);
        }
        telemetry.update();
    }

    public int initSensors(HardwareMap hardwareMap, int numberMissing, boolean useI2C) {
        HwSwitch vSwitch = null;
        try {
            vSwitch = new HwSwitch(hardwareMap, "vswitch", true, true);
        } catch (Exception e) {
            RobotLog.e("missing: vSwitch" + e.getMessage());
            telemetry.addLine("missing: vSwitch");
            numberMissing++;
        }
        this.vSwitch = vSwitch;

        HwSwitch hSwitch = null;
        try {
            hSwitch = new HwSwitch(hardwareMap,
                    "hswitch", true, true);
        } catch (Exception e) {
            RobotLog.e("missing: hswitch" + e.getMessage());
            telemetry.addLine("missing: hswitch");
            numberMissing++;
        }
        this.hSwitch = hSwitch;

        if (useI2C) {
            ITDColorSensor colorSensor = null;
            try {
                colorSensor = new ITDColorSensor(hardwareMap, "Color");
            } catch (Exception e) {
                RobotLog.e("missing: colorSensor" + e.getMessage());
                telemetry.addLine("missing: colorSensor");
                numberMissing++;
            }
            this.colorSensor = colorSensor;
        }

        ITDUltrasonic ultrasonic = null;
        try {
            ultrasonic = new ITDUltrasonic(hardwareMap, "ultrasonic", new LinearFunction(86.9, -12.2));
        } catch (Exception e) {
            RobotLog.e("missing: ultrasonic" + e.getMessage());
            telemetry.addLine("missing: ultrasonic");
            numberMissing++;
        }
        this.ultrasonic = ultrasonic;

        return numberMissing;
    }

    public boolean isAuto() {
        return opModeType == OpModeType.SAMPLE_AUTO || opModeType == OpModeType.SPECIMEN_AUTO || opModeType == OpModeType.AUTO_TEST;
    }

    public boolean isTest() {
        return opModeType == OpModeType.TELEOP_TEST || opModeType == OpModeType.AUTO_TEST;
    }

    public void init() {
        if (opMode != null) {
            HardwareMap hardwareMap = opMode.hardwareMap;

            if (opModeType == OpModeType.TELEOP) {
                initializeServos(opMode, 0, opMode.scheduler.delays);

                hSlideMotor hslides = null;
                try {
                    hslides = new hSlideMotor(hardwareMap, "hslides", DcMotorSimple.Direction.FORWARD, this.hSwitch, this::getCycleState, vslides::getCurrentState, intakeArm::getCurrentState, this.depoClaw, opMode.scheduler.delays, true, hslideLock);
                } catch (Exception e) {
                    RobotLog.e("missing: hslides" + e.getMessage());
                    telemetry.addLine("missing: hslides");
                }
                this.hslides = hslides;
            }
        }

        hslides.hslideInit();
        //vslides.vslideInit();
        intake.stop();

        if (opModeType == OpModeType.TELEOP) {
            slideHang.setCurrentState(SlideHangServo.SlideHangState.INACTIVE);
            hslideLock.close();
            ptoServo.stop();
            intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
            intakeTrapdoor.close();
            depoClaw.open();
            depoArm.setCurrentState(DepoArmServo.ArmState.NEUTRAL);
            depoTilt.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
            depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
            depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);
        }
    }

    public int initializeServos(OpMode op, int numberMissing, DelayManager delays) {
        HardwareMap hardwareMap = op.hardwareMap;
        this.telemetry = op.telemetry;

        hSlideLockServo slideLock = null;
        try {
            slideLock = new hSlideLockServo(hardwareMap, "hlock", false, hSlideLockServo.LockState.DOWN);
        } catch (Exception e) {
            RobotLog.e("missing: hlock" + e.getMessage());
            telemetry.addLine("missing: hlock");
            numberMissing++;
        }
        this.hslideLock = slideLock;
        servos.add(hslideLock);

        HwAnalogEncoder intakeArmVoltage = null;
        try {
            intakeArmVoltage = new HwAnalogEncoder(hardwareMap, "intakeArmVoltage");
        } catch (Exception e) {
            RobotLog.e("missing: intakeArmVoltage" + e.getMessage());
            telemetry.addLine("missing: intakeArmVoltage");
            numberMissing++;
        }

        IntakeFourbarArmServo intakeFourbarArm = null;
        try {
            intakeFourbarArm = new IntakeFourbarArmServo(hardwareMap, "intakeArm",false, IntakeFourbarArmServo.IntakeArmState.TRANSFER, intakeArmVoltage);
        } catch (Exception e) {
            RobotLog.e("missing: twobar" + e.getMessage());
            telemetry.addLine("missing: twobar");
            numberMissing++;
        }
        this.intakeArm = intakeFourbarArm;
        servos.add(this.intakeArm);

        IntakeFourbarTiltServo intakeFourbarTilt = null;
        try {
            intakeFourbarTilt = new IntakeFourbarTiltServo(hardwareMap, "intakeTilt", false, IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
        } catch (Exception e) {
            RobotLog.e("missing: fourbar" + e.getMessage());
            telemetry.addLine("missing: fourbar");
            numberMissing++;
        }
        this.intakeTilt = intakeFourbarTilt;
        servos.add(this.intakeTilt);

        TrapdoorServo intakeTrapdoor = null;
        try {
            intakeTrapdoor = new TrapdoorServo(hardwareMap, "intakeTrapdoor",false, TrapdoorServo.TrapdoorState.OPEN, delays);
        } catch (Exception e) {
            RobotLog.e("missing: intakeTrapdoor" + e.getMessage());
            telemetry.addLine("missing: intakeTrapdoor");
            numberMissing++;
        }
        this.intakeTrapdoor = intakeTrapdoor;
        servos.add(this.intakeTrapdoor);

        DepoWristServo depoWrist = null;
        try {
            depoWrist = new DepoWristServo(hardwareMap, "depoWrist",false, DepoWristServo.WristState.TRANSFER);
        } catch (Exception e) {
            RobotLog.e("missing: depoWrist" + e.getMessage());
            telemetry.addLine("missing : depoWrist");
        }
        this.depoWrist = depoWrist;
        servos.add(this.depoWrist);

        DepoClawServo depoClaw = null;
        try {
            depoClaw = new DepoClawServo(hardwareMap, "depoClaw", false, DepoClawServo.ClawState.OPEN);
        } catch (Exception e) {
            RobotLog.e("missing: depoClaw" + e.getMessage());
            telemetry.addLine("missing: depoClaw");
            numberMissing++;
        }
        this.depoClaw = depoClaw;
        servos.add(this.depoClaw);

        HwAnalogEncoder armVoltage = null;
        try {
            armVoltage = new HwAnalogEncoder(hardwareMap, "armVoltage");
        } catch (Exception e) {
            RobotLog.e("missing: armVoltage" + e.getMessage());
            telemetry.addLine("missing: armVoltage");
            numberMissing++;
        }

        DepoArmServo depoArm = null;
        try {
            depoArm = new DepoArmServo(hardwareMap, "depoArm", false, DepoArmServo.ArmState.TRANSFER, armVoltage);
        } catch (Exception e) {
            RobotLog.e("missing: depoArm" + e.getMessage());
            telemetry.addLine("missing: depoArm");
            numberMissing++;
        }
        this.depoArm = depoArm;
        servos.add(this.depoArm);

        DepoTurretServo depoTurret = null;
        try {
            depoTurret = new DepoTurretServo(hardwareMap, "depoTurret", false, DepoTurretServo.TurretState.NEUTRAL);
        } catch (Exception e) {
            RobotLog.e("missing: depoTurret" + e.getMessage());
            telemetry.addLine("missing: depoTurret");
            numberMissing++;
        }
        this.depoTurret = depoTurret;
        servos.add(this.depoTurret);

        DepoTiltServo depoTilt = null;
        try {
            depoTilt = new DepoTiltServo(hardwareMap, "depoTilt", false, DepoTiltServo.TiltState.TRANSFER);
        } catch (Exception e) {
            RobotLog.e("missing: depoTilt" + e.getMessage());
            telemetry.addLine("missing : depoArm");
            numberMissing++;
        }
        this.depoTilt = depoTilt;
        servos.add(this.depoTilt);

        PTOServo ptoServo = null;
        try {
            ptoServo = new PTOServo(hardwareMap, "pto", false, PTOServo.PTOState.INACTIVE);
        } catch (Exception e) {
            RobotLog.e("missing: ptoServo" + e.getMessage());
            telemetry.addLine("missing: ptoServo");
            numberMissing++;
        }
        this.ptoServo = ptoServo;
        servos.add(ptoServo);

        SlideHangServo slideHangServo = null;
        try {
            slideHangServo = new SlideHangServo(hardwareMap, "slideHang", false, SlideHangServo.SlideHangState.INACTIVE);
        } catch (Exception e) {
            RobotLog.e("missing: slideHangServo" + e.getMessage());
            telemetry.addLine("missing: slideHangServo");
            numberMissing++;
        }
        this.slideHang = slideHangServo;
        servos.add(slideHangServo);

        if (opModeType != OpModeType.TELEOP) {
            slideHang.setCurrentState(SlideHangServo.SlideHangState.INACTIVE);
            hslideLock.open();
            ptoServo.stop();
            intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
            intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
            intakeTrapdoor.open();
            depoClaw.close();
            depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
            depoTurret.setCurrentState(DepoTurretServo.TurretState.NEUTRAL);

            if (!isTest()) {
                depoArm.setCurrentState(DepoArmServo.ArmState.NEUTRAL, ITDConstants.DEPO_ARM_AUTO_START);
                depoTilt.setCurrentState(DepoTiltServo.TiltState.NEUTRAL, ITDConstants.DEPO_TILT_AUTO_START);
            } else {
                depoArm.setCurrentState(DepoArmServo.ArmState.NEUTRAL);
                depoTilt.setCurrentState(DepoTiltServo.TiltState.NEUTRAL);
            }
        }

         return numberMissing;
    }

    /**
     * set the bulk read mode.  Default to OFF
     *
     * @param mode bulk read mode
     */
    public void setBulkReadMode(LynxModule.BulkCachingMode mode) {
        for (LynxModule module : allHubs) {
            module.setBulkCachingMode(mode);
        }
    }

    /**
     * set the bulk read mode to manual.  You must call #clearBulkCache() at the
     * beginning of the loop
     *
     * @see #clearBulkCache()
     */
    public void setBulkReadManual() {
        setBulkReadMode(LynxModule.BulkCachingMode.MANUAL);
    }

    /**
     * Will run one bulk read per cycle,
     * even as frontLeftMotor.getPosition() is called twice
     * because the caches are being handled manually and cleared
     * once a loop
     */
    public void clearBulkCache() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public void close() {
        vslides.stop();
        hslides.stop();

        if (drive != null) {
            drive.setPowers(0, 0, 0, 0);
        }

        intake.stop();

        RobotLog.vv("Close", "Stopped");
    }

    public void prepareForTeleOp() {
        retract();
        close();
    }

    public void prepareForTeleOp(Pose endPose) {
        AutoTeleopCommunication.setStartPose(endPose);
        prepareForTeleOp();
    }

    public void retract() {
    }

    public AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public boolean isRed() {
        return allianceColor == AllianceColor.Red;
    }

    public boolean isBlue() {
        return allianceColor == AllianceColor.Blue;
    }

    public void setAllianceColor(AllianceColor allianceColor) {
        this.allianceColor = allianceColor;
    }

    public void update() {
        clearBulkCache();
        ultrasonic.update();
        hSwitch.update();
        vSwitch.update();
        colorSensor.update();

        for (HwServo2 servo : servos) {
            servo.update();
        }

        intake.update();
        hslides.update();
        vslides.update();
    }

    public void update(boolean usePinpoint) {
        clearBulkCache();
        if (usePinpoint) {
            pinpoint.update();
        }
        ultrasonic.update();
        hSwitch.update();
        vSwitch.update();
        colorSensor.update();

        for (HwServo2 servo : servos) {
            servo.update();
        }

        intake.update();
        hslides.update();
        vslides.update();
    }

    public static String allianceColorToString(AllianceColor allianceColor) {
        if (allianceColor == AllianceColor.Red) {
            return "red";
        } else if (allianceColor == AllianceColor.Blue) {
            return "blue";
        }

        return "none";
    }

    public boolean isSpecimen() {
        return specimen;
    }

    public void setSpecimen(boolean specimen) {
        this.specimen = specimen;
    }

    public CycleState getCycleState() {
        return cycleState;
    }

    public void setCycleState(CycleState cycleState) {
        this.cycleState = cycleState;
    }

    public String allianceColorToString() {
        return allianceColorToString(allianceColor);
    }

    public static String cycleStateToString(CycleState state) {
        if (state == CycleState.GIVE_TO_HUMAN_PLAYER) {
            return "GIVE_TO_HUMAN_PLAYER";
        } else if (state == CycleState.TRANSFER) {
            return "TRANSFER";
        } else if (state == CycleState.SAMPLE_INTAKE) {
            return "SAMPLE_INTAKE";
        } else if (state == CycleState.NEUTRAL) {
            return "NEUTRAL";
        } else if (state == CycleState.LOW_BASKET_DEPO) {
            return "LOW_BASKET_DEPO";
        } else if (state == CycleState.SPECIMEN_INTAKE) {
            return "SPECIMEN_INTAKE";
        } else if (state == CycleState.SPECIMEN_DEPO_UP_CLIP) {
            return "SPECIMEN_DEPO_UP_CLIP";
        } else {
            return "HIGH_BASKET_DEPO";
        }
    }

    public String cycleStateToString() {
        return cycleStateToString(cycleState);
    }

    public boolean correctColor(DetectionColor detectionColor) {
        if (detectionColor == DetectionColor.Yellow) {
            return true;
        } else if (detectionColor == DetectionColor.None) {
            return false;
        } else return (detectionColor == DetectionColor.Red && allianceColor == AllianceColor.Red) || (detectionColor == DetectionColor.Blue && allianceColor == AllianceColor.Blue);
    }

    public boolean correctColor() {
        return correctColor(colorSensor.getColor());
    }

    public boolean incorrectColor(DetectionColor detectionColor) {
        if (detectionColor == DetectionColor.Yellow) {
            return false;
        } else if (detectionColor == DetectionColor.None) {
            return false;
        } else return (detectionColor == DetectionColor.Red && allianceColor == AllianceColor.Blue) || (detectionColor == DetectionColor.Blue && allianceColor == AllianceColor.Red);
    }

    public DetectionColor allianceColorToDetectionColor() {
        if (allianceColor == AllianceColor.Red) {
            return DetectionColor.Red;
        } else if (allianceColor == AllianceColor.Blue) {
            return DetectionColor.Blue;
        } else {
            return DetectionColor.None;
        }
    }

    public boolean isAllianceSpecificIntakeOnly() {
        return allianceSpecificIntakeOnly;
    }

    public void setAllianceSpecificIntakeOnly(boolean allianceSpecificIntakeOnly) {
        this.allianceSpecificIntakeOnly = allianceSpecificIntakeOnly;
    }

    public void swapAllianceSpecificIntakeOnly() {
        allianceSpecificIntakeOnly = !allianceSpecificIntakeOnly;

        if (allianceSpecificIntakeOnly) {
            specimen = true;
        }
    }

    public void intake() {
        intakeTrapdoor.close();
        intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
        intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
        setCycleState(CycleState.SAMPLE_INTAKE);
        intake.setPower(1f);
    }

    public void intake(int hslidePos) {
        hslides.runToPosition(hslidePos);
        intakeTrapdoor.close();
        intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.INTAKE);
        intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.INTAKE);
        setCycleState(CycleState.SAMPLE_INTAKE);
        intake.setPower(1f);
    }
    
    public void transfer(Runnable action) {
        depoClaw.open();
        depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
        setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        depoArm.setCurrentState(DepoArmServo.ArmState.TRANSFER);
        depoTilt.setCurrentState(DepoTiltServo.TiltState.TRANSFER);
        intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
        delays.add(new Delay(150, () -> hslides.retract(() -> {
            delays.add(new Delay(50, () -> intake.setPower(0f)));
            delays.add(new Delay(200, () -> {
                depoClaw.looseClose();
                setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                action.run();
            }));
        })));
    }

    public void transfer() {
        depoClaw.open();
        depoWrist.setCurrentState(DepoWristServo.WristState.TRANSFER);
        setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        depoArm.setCurrentState(DepoArmServo.ArmState.TRANSFER);
        depoTilt.setCurrentState(DepoTiltServo.TiltState.TRANSFER);
        intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.TRANSFER);
        intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.TRANSFER);
        delays.add(new Delay(150, () -> hslides.retract(() -> {
            delays.add(new Delay(50, () -> intake.setPower(0f)));
            delays.add(new Delay(200, () -> {
                depoClaw.looseClose();
                setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
            }));
        })));
    }

    public void spitToHumanPlayer() {
        intakeTrapdoor.unlockSample();
        intake.setPower(-0.5f);
        intakeArm.setCurrentState(IntakeFourbarArmServo.IntakeArmState.MIDDLE);
        intakeTilt.setCurrentState(IntakeFourbarTiltServo.IntakeTiltState.MIDDLE);
    }

    public void giveToHumanPlayer() {

    }

    public void specimenDepo() {

    }

    public void lowBasketDepo() {

    }

    public void highBasketDepo() {

    }

    public void specimenDepoClip() {

    }

    public void specIntakeClampIntoSpecDepo() {

    }

    public OpModeType getOpModeType() {
        return opModeType;
    }

    public void clawToggle() {
        if (depoClaw.getCurrentState() == DepoClawServo.ClawState.OPEN || depoClaw.getCurrentState() == DepoClawServo.ClawState.SPECIMEN_INTAKE_OPEN) {
            closeClaw();
        } else if (getCycleState() != RobotRevampedITD.CycleState.GIVE_TO_HUMAN_PLAYER && getCycleState() != RobotRevampedITD.CycleState.SPECIMEN_INTAKE) {
            depoClaw.open();
        } else {
            depoClaw.specimenIntake();
        }
    }

    public void closeClaw() {
        if (getCycleState() != RobotRevampedITD.CycleState.NEUTRAL && getCycleState() != RobotRevampedITD.CycleState.SAMPLE_INTAKE) {
            if (getCycleState() != RobotRevampedITD.CycleState.TRANSFER) {
                depoClaw.close();
            } else {
                depoClaw.looseClose();
            }
        }
    }

    public void raiseVSlidesToBasket() {
        intake.setPower(1f);
        vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO + 15);
        delays.add(new Delay(() -> vslides.getCurrentPosition() > ITDConstants.VSLIDE_MIN, () -> delays.add(new Delay(200, intake::stop))));
    }

    public Pose getStartPose() {
        return startPose;
    }
}
