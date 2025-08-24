package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.HSlideLockServo;

import java.util.function.DoubleSupplier;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.Hardware.HwSlide2;
import revAmped.components.SlideController.STD.CustomSTDCoefficients;
import revAmped.components.SlideController.STD.STDController;

public class HslideMotor extends HwSlide2<HslideMotor.HslideMotorState> {
    private final STDController controller = new STDController(new CustomSTDCoefficients(1.2, 0, 0, 1.5), Constants.HSLIDE_MAX_EXTENSION);
    private final DoubleSupplier xVel;
    private final HSlideLockServo slideLock;

    public static final int FULL_EXTENSION = Constants.HSLIDE_MAX_EXTENSION;
    public static final int HALF_EXTENSION = Constants.HSLIDE_MAX_EXTENSION / 2;
    public static final float TICKS_PER_INCH = 29.2f;

    @Override
    public void buildPowerMap() {
        powerMap.put(HslideMotorState.IN, () -> {
            if (xVel != null) {
                double vel = xVel.getAsDouble();
                int pos = getCurrentPosition();
                if (pos > 10) return -0.6;
                else if ((vel > 3.5 && vel < 8) || pos > 5) return -0.2;
                else if (vel >= 8) return -0.6;
                else if (vel >= 3.5) return -0.2;
                else return 0;
            } else return 0;
        });
        powerMap.put(HslideMotorState.RETRACTION, () -> -1f);
        powerMap.put(HslideMotorState.OUT, () -> controller.run(getCurrentPosition()));
    }

    @Override
    public void buildInitMap() {
        if (slideLock != null) {
            initMap.put(HslideMotorState.IN, new InitActions(slideLock::close, 0));
            initMap.put(HslideMotorState.OUT, new InitActions(slideLock::open, 100));
            initMap.put(HslideMotorState.RETRACTION, new InitActions());
            return;
        }

        initMap.put(HslideMotorState.IN, new InitActions());
        initMap.put(HslideMotorState.OUT, new InitActions());
        initMap.put(HslideMotorState.RETRACTION, new InitActions());
    }

    @Override
    public void updateController(int targetPosition) {
        controller.setTargetPosition(targetPosition);
    }

    public enum HslideMotorState implements SlideState {
        RETRACTION(0),
        OUT(FULL_EXTENSION),
        IN(0);

        private final int position;

        HslideMotorState(int position) {
            this.position = position;
        }

        @Override
        public int position() {
            return position;
        }

        @Override
        public boolean isRetraction() {
            return this == RETRACTION;
        }
    }

    public HslideMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, SlideRetractionDetector limitSwitch, DoubleSupplier xVel) {
        super(hardwareMap, id, direction, limitSwitch, HslideMotorState.IN);
        this.xVel = xVel;
        slideLock = null;
        init();
    }

    public HslideMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, SlideRetractionDetector limitSwitch) {
        super(hardwareMap, id, direction, limitSwitch, HslideMotorState.IN);
        this.xVel = null;
        slideLock = null;
        init();
    }

    public HslideMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, SlideRetractionDetector limitSwitch, HSlideLockServo slideLock) {
        super(hardwareMap, id, direction, limitSwitch, HslideMotorState.IN, HslideMotorState.RETRACTION);
        this.xVel = null;
        this.slideLock = slideLock;
        init();
    }

    public HslideMotor(HardwareMap hardwareMap, HSlideLockServo slideLock) {
        super(hardwareMap, "hslides", Constants.HSLIDE_DIRECTION, () -> false, HslideMotorState.IN, HslideMotorState.IN);
        this.xVel = null;
        this.slideLock = slideLock;
        retractionDetector = this::retracted;
        init();
    }

    private boolean retracted() {
        return getVelocity() < 0.75 && getCurrentPosition() < 5;
    }

    public static int inchesToTicks(double inches) {
        return Math.max((int) (inches * TICKS_PER_INCH - 1), 0);
    }


    public static double ticksToInches(int ticks) {
        return Math.max((ticks + 1)/TICKS_PER_INCH, 0);
    }
    @Override
    public int getTargetPosition() {
        return controller.getTargetPosition();
    }

    @Override
    public Class<HslideMotorState> getStateClass() {
        return HslideMotorState.class;
    }

    public void retraction() {
        setCurrentState(HslideMotorState.RETRACTION);
    }

    @Override
    public void init() {
        setTargetPosition(0);
        super.init();
    }
}
