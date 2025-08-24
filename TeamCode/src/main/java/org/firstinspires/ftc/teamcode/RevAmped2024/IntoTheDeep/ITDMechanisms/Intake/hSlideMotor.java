package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake;

import com.pedropathing.util.CustomPIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepoClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import java.util.concurrent.Callable;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.Hardware.HwSlide;
import revAmped.components.Hardware.HwSwitch;
import revAmped.components.MathUtil.STD.CustomSTDCoefficients;
import revAmped.components.MathUtil.STD.STDController;
import revAmped.components.MathUtil.SquID.CustomSquIDConstants;
import revAmped.components.MathUtil.SquID.SquIDController;
import revAmped.util.Range;

public class hSlideMotor extends HwSlide {
    private final Callable<RobotRevampedITD.CycleState> cycleStateCallable;
    private final Callable<vSlideMotor.VSlideState> vSlideStateCallable;
    private final Callable<IntakeFourbarArmServo.IntakeArmState> intakeArmStateCallable;
    private final DepoClawServo claw;
    private final DelayManager delays;
    private boolean useSquID;
    private STDController controller;
    private hSlideLockServo slideLock;
    private float mult = 1;

    private CustomSTDCoefficients fullExtension = new CustomSTDCoefficients(1.2, 0, 0, 1.5);
    private CustomSTDCoefficients halfExtension = new CustomSTDCoefficients(1, 0, 0, 1);

    public static final int MAX_ETENSION = ITDConstants.HSLIDES_MAX;
    public static final int MAX_EXTENSION_SNIPE = ITDConstants.HSLIDES_MAX_SNIPING;
    public static final int HALF_EXTENSION = ITDConstants.HSLIDES_MAX/2;

    public enum HslideState {
        OUT,
        IN,
        RETRACTION
    }
    private HslideState currentState = HslideState.IN;

    public enum ExtensionState {
        NONE,
        HALF,
        FULL,
        FINETUNE
    }
    private ExtensionState extensionState = ExtensionState.NONE;

    public hSlideMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSwitch limitSwitch, Callable<RobotRevampedITD.CycleState> cycleStateCallable, DepoClawServo claw, DelayManager delays, hSlideLockServo hSlideLockServo, Callable<IntakeFourbarArmServo.IntakeArmState> intakeArmStateCallable, Callable<vSlideMotor.VSlideState> vSlideStateCallable) {
        this(hardwareMap, id, direction, limitSwitch, cycleStateCallable, vSlideStateCallable, intakeArmStateCallable, claw, delays, false,hSlideLockServo);
    }

    public hSlideMotor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, HwSwitch limitSwitch, Callable<RobotRevampedITD.CycleState> cycleStateCallable, Callable<vSlideMotor.VSlideState> vSlideStateCallable, Callable<IntakeFourbarArmServo.IntakeArmState> intakeArmStateCallable, DepoClawServo claw, DelayManager delays, boolean useSquID, hSlideLockServo slideLock) {
        super(hardwareMap, id, direction, limitSwitch);
        this.vSlideStateCallable = vSlideStateCallable;
        this.intakeArmStateCallable = intakeArmStateCallable;
        this.claw = claw;
        this.delays = delays;
        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.cycleStateCallable = cycleStateCallable;
        this.useSquID = useSquID;
        controller = new STDController(fullExtension, ITDConstants.HSLIDES_MAX);
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.slideLock = slideLock;
    }

    public void retract() {
        currentState = HslideState.RETRACTION;

        super.retract(-1f, () -> {currentState = HslideState.IN; slideLock.close(); extensionState = ExtensionState.NONE;});

        if (useSquID) {
            controller.runToPosition(0);
        }
    }

    public void retract(Runnable action) {
        currentState = HslideState.RETRACTION;

        super.retract(-1, () -> {
            currentState = HslideState.IN;
            extensionState = ExtensionState.NONE;
            slideLock.close();
            action.run();
        });

        if (useSquID) {
            controller.runToPosition(0);
        }
    }

    public void retract(Runnable action, boolean closeLock) {
        currentState = HslideState.RETRACTION;

        super.retract(-1, () -> {
            currentState = HslideState.IN;
            extensionState = ExtensionState.NONE;

            if (closeLock) {
                slideLock.close();
            }

            action.run();
        });

        if (useSquID) {
            controller.runToPosition(0);
        }
    }

    public void retract(boolean closeLock) {
        currentState = HslideState.RETRACTION;

        super.retract(-1, () -> {
            currentState = HslideState.IN;
            extensionState = ExtensionState.NONE;

            if (closeLock) {
                slideLock.close();
            }
        });

        if (useSquID) {
            controller.runToPosition(0);
        }
    }

    public void runToPosition(int position) {
        runToPosition(position, false);
    }

    public void runToPosition(int position, boolean useIntakeArmState) {
        final int positionFinal = clipPos(position, useIntakeArmState);

        if (position >= ITDConstants.HSLIDES_MAX) {
            extensionState = ExtensionState.FULL;
        } else if (position <= ITDConstants.HSLIDES_MAX/2 + 5 && position >= ITDConstants.HSLIDES_MAX/2 - 5) {
            extensionState = ExtensionState.HALF;
        } else if (position > 0) {
            extensionState = ExtensionState.FINETUNE;
        }

        if (position > 0) {
            currentState = HslideState.OUT;
        }

        double delay = 0;

        try {
            if (slideLock.getCurrentState() != hSlideLockServo.LockState.DOWN_SLIDES_OUT) {
                delay = 100;
                slideLock.open();
            }

            if (claw.getCurrentState() != DepoClawServo.ClawState.OPEN && (cycleStateCallable.call() == RobotRevampedITD.CycleState.TRANSFER || cycleStateCallable.call() == RobotRevampedITD.CycleState.NEUTRAL) && vSlideStateCallable.call() == vSlideMotor.VSlideState.IN) {
                delay = 150;
                claw.open();
            }
        } catch (Exception e) {}

        if (!useSquID) {
            delays.add(new Delay(delay, () -> {super.runToPosition(positionFinal); delays.add(new Delay(150, () -> slideLock.setCurrentState(hSlideLockServo.LockState.DOWN_SLIDES_OUT)));}));
        } else {
            delays.add(new Delay(delay, () -> {controller.runToPosition(positionFinal); delays.add(new Delay(150, () -> slideLock.setCurrentState(hSlideLockServo.LockState.DOWN_SLIDES_OUT)));}));
        }
    }

    private int clipPos(int position, boolean useIntakeArmState) {
        int positionFinal = 0;
        try {
            if (useIntakeArmState && intakeArmStateCallable.call() == IntakeFourbarArmServo.IntakeArmState.EDGE_SAMPLES) {
                positionFinal = Range.clip(position, 0, ITDConstants.HSLIDES_MAX_SNIPING);
            } else {
                positionFinal = Range.clip(position, 0, ITDConstants.HSLIDES_MAX);
            }
        } catch (Exception e) {}

        return positionFinal;
    }

    public void runToPositionAuto(int pos, boolean delayForLock) {
        if (pos >= ITDConstants.HSLIDES_MAX) {
            extensionState = ExtensionState.FULL;
        } else if (pos <= ITDConstants.HSLIDES_MAX/2 + 5 && pos >= ITDConstants.HSLIDES_MAX/2 - 5) {
            extensionState = ExtensionState.HALF;
        } else if (pos > 0) {
            extensionState = ExtensionState.FINETUNE;
        }

        if (pos > 0) {
            currentState = HslideState.OUT;
        }

        if (pos > 200) {
            controller.updateConstants(fullExtension);
        } else if (pos > 30) {
            controller.updateConstants(halfExtension);
        }

        slideLock.open();
        delays.addOptionalDelay(!delayForLock, new Delay(200, () -> controller.runToPosition(pos)));
    }

    public void fullExtend() {
        runToPosition(ITDConstants.HSLIDES_MAX_SNIPING, true);
        extensionState = ExtensionState.FULL;
    }

    public void hslideInit() {
        if (!limitSwitch.isTouch()) {
            retract();
        }
    }

    public void runToInches(double inches) {
        int ticks = (int) (inches * ITDConstants.TICKS_PER_INCH);
        runToPosition(ticks, true);
    }

    public HslideState getCurrentState() {
        return currentState;
    }

    public ExtensionState getExtentionState() {
        return extensionState;
    }

    public void stop() {
        super.stop();
        resetPosition();
    }

    public boolean isUseSquID() {
        return useSquID;
    }

    public void setUseSquID(boolean useSquID) {
        this.useSquID = useSquID;
    }

    @Override
    public void update() {
        super.update();

        if (useSquID && currentState == HslideState.OUT) {
            if (getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }

            setPower(mult * controller.run(getCurrentPosition()));
        }
    }

    //public void

    public float getMult() {
        return mult;
    }

    public void setMult(float mult) {
        this.mult = mult;
    }

    public void resetMult() {
        mult = 1;
    }

    public double getError() {
        if (controller != null) {
            return controller.getError();
        } else {
            return getTargetPosition() - getCurrentPosition();
        }
    }

    @Override
    public int getTargetPosition() {
        if (controller != null) {
            return controller.getTargetPosition();
        }

        return super.getTargetPosition();
    }

    public STDController getController() {
        return controller;
    }
}
