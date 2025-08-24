package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;
import revAmped.components.Hardware.HwSlide;
import revAmped.components.Hardware.HwSwitch;
import revAmped.components.MathUtil.STD.CustomSTDCoefficients;
import revAmped.components.MathUtil.STD.STDController;
import revAmped.util.Range;

public class vSlideMotor {
    public final HwSlide leftMotor;
    public final HwSlide rightMotor;
    private int startPos = -1;
    private HwSwitch limitSwitch;

    public enum VSlideState {
        OUT,
        RETRACTION,
        IN
    }
    private VSlideState currentState = VSlideState.IN;
    private STDController controller;
    private boolean useController = false;
    private DelayManager delays;
    private boolean transfer = false;
    //private CustomSquIDConstants basketConstants = new CustomSquIDConstants(0.06, 0, 0);
    //private CustomSquIDConstants rungConstants = new CustomSquIDConstants(0.06, 0.15, 0);
    private CustomSTDCoefficients constants = new CustomSTDCoefficients(0.65, 0.05, 0.001, 2);
    private int targetPosition = 0;
    private Boolean atBottom;

    public static final int STRAIGHT_CLIP = ITDConstants.VSLIDE_SPECIMEN_HIGH_RUNG;
    public static final int HIGH_BASKET = ITDConstants.VSLIDE_HIGH_BASKET;
    public static final int LOW_BASKET = ITDConstants.VSLIDE_LOW_BASKET;
    public static final int DOWN_CLIP_INITAL = ITDConstants.VSLIDE_DOWN_CLIP;
    public static final int DOWN_CLIP_FINAL = ITDConstants.VSLIDE_DOWN_CLIP_FINAL;
    public static final int MAX_POS = ITDConstants.VSLIDE_MAX;

    public vSlideMotor(OpMode op, String idLeft, String idRight, DcMotorSimple.Direction directionLeft, DcMotorSimple.Direction directionRight) {
        HwSlide leftMotor = null;
        try {
            leftMotor = new HwSlide(op.hardwareMap, idLeft, directionLeft);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorLeft" + e.getMessage());
            op.telemetry.addLine("missing: vslideMotorLeft");
        }
        this.leftMotor = leftMotor;

        HwSlide rightMotor = null;
        try {
            rightMotor = new HwSlide(op.hardwareMap, idRight, directionRight);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorRight" + e.getMessage());
            op.telemetry.addLine("missing: vslideMotorRight");
        }
        this.rightMotor = rightMotor;
    }

    public vSlideMotor(OpMode op, String idLeft, String idRight, DcMotorSimple.Direction directionBottom, DcMotorSimple.Direction directionTop, HwSwitch limitSwitch) {
        HwSlide leftMotor = null;
        try {
            leftMotor = new HwSlide(op.hardwareMap, idLeft, directionBottom);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorLeft" + e.getMessage());
            op.telemetry.addLine("missing: vslideMotorLeft");
        }
        this.leftMotor = leftMotor;

        HwSlide rightMotor = null;
        try {
            rightMotor = new HwSlide(op.hardwareMap, idRight, directionTop);
        } catch (Exception e) {
            RobotLog.e("missing: vslideMotorRight" + e.getMessage());
            op.telemetry.addLine("missing: vslideMotorRight");
        }
        this.rightMotor = rightMotor;
        this.limitSwitch = limitSwitch;
    }

    public vSlideMotor(HwSlide leftMotor, HwSlide rightMotor, HwSwitch limitSwitch, boolean useController, DelayManager delays) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.limitSwitch = limitSwitch;
        this.useController = useController;
        this.delays = delays;
        controller = new STDController(constants, HIGH_BASKET);
    }

    public void runToPosition(int position, boolean useSquID, int tolerance) {

        int finalPosition = Range.clip(position, -100000, ITDConstants.VSLIDE_MAX);

        boolean squid = this.useController;

        if (useSquID != this.useController) {
            delays.add(new Delay(() -> Math.abs(finalPosition - getCurrentPosition()) < tolerance, () -> this.useController = squid));
        }

        if (!useSquID) {
            leftMotor.runToPosition(finalPosition);
            rightMotor.runToPosition(finalPosition);
        } else {
            controller.runToPosition(finalPosition);
        }

        if (position > 0) {
            currentState = VSlideState.OUT;
        }

        transfer = false;
        targetPosition = finalPosition;
    }

    public void runToPosition(int position, boolean useSquID) {
        runToPosition(position, useSquID, 20);
    }

    public void runToPosition(int position) {
        runToPosition(position, useController);
    }

    public void finetune(int change) {
        int newPos = getTargetPosition() + change;
        runToPosition(newPos);
    }

    public void retract() {
        /*
        currentState = VSlideState.RETRACTION;

        if (!useController) {
            startPos = getCurrentPosition();
            rightMotor.retract(() -> -0.5f - (float) (Math.abs(startPos - getCurrentPosition())) * 0.25f / (float) startPos, () -> currentState = VSlideState.IN, false);
            leftMotor.retract(() -> -0.5f - (float) (Math.abs(startPos - getCurrentPosition())) * 0.25f / (float) startPos, false);
        } else {
            controller.runToPosition(-20, 20);
            delays.add(new Delay(limitSwitch::isTouch, () -> {controller.setHomed(); setPower(0f); currentState = VSlideState.IN; resetPosition();}));
        }

        transfer = false;
        targetPosition = 0;

         */

        fullRetract();
    }

    public void retractNoSquID() {
        currentState = VSlideState.RETRACTION;
        useController = false;
        controller.runToPosition(0);
        setPower(-1f);
        delays.add(new Delay(this::atBottom, () -> {
            useController = true;
            currentState = VSlideState.IN;
            setPower(0f);
        }));
    }

    public void retract(Runnable action) {
        startPos = getCurrentPosition();
        currentState = VSlideState.RETRACTION;
        if (!useController) {
            rightMotor.retract(() -> Range.clip(-0.4f - (float) (Math.abs(startPos - getCurrentPosition())) * 0.25f / (float) startPos, -0.6f, -0.4f), () -> {
                action.run();
                currentState = VSlideState.IN;
            }, false);
            leftMotor.retract(() -> Range.clip(-0.4f - (float) (Math.abs(startPos - getCurrentPosition())) * 0.25f / (float) startPos, -0.6f, -0.4f), false);
        } else {
            controller.runToPosition(0);
            delays.add(new Delay(this::atBottom, () -> {setPower(0f); action.run(); currentState = VSlideState.IN;}));
        }

        transfer = false;
        targetPosition = 0;
    }

    public void retractAuto(Follower follower) {
        delays.add(new Delay(() -> getCurrentPosition() < 450, () -> follower.setGuardAgainstFlying(true)));
        vslideInit(() -> follower.setGuardAgainstFlying(false));
    }

    public int getCurrentPosition() {
        return leftMotor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void stop() {
        leftMotor.stop();
        rightMotor.stop();
    }

    public void update() {
        atBottom = null;
        leftMotor.update();
        rightMotor.update();

        if (limitSwitch != null) {
            limitSwitch.update();
        }

        if (useController && (currentState != VSlideState.IN || transfer)) {
            if (leftMotor.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }

            if (rightMotor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) {
                rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }

            if (currentState != VSlideState.IN || transfer) {
                setPower(controller.run(getCurrentPosition()));
            } else {
                setPower(0f);
            }
        }
    }

    public void resetPosition() {
        leftMotor.resetPosition();
        startPos = -1;
    }

    public void vslideInit(Runnable action) {
        if (!(leftMotor.getCurrentPosition() < 10)) {
            if (useController) {
                useController = false;
                retract(() -> {
                    useController = true; action.run();});
            } else {
                retract(action);
            }
        }
    }

    public VSlideState getCurrentState() {
        return currentState;
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        leftMotor.setZeroPowerBehavior(zeroPowerBehavior);
        rightMotor.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setMode(DcMotor.RunMode runMode) {
        leftMotor.setMode(runMode);
        rightMotor.setMode(runMode);
    }

    public void setPower(double power) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
    }

    public void startTransfer() {
        transfer = true;
        fullRetract(() -> controller.runToPosition(0));
    }

    public void retractToPos(int pos) {
        currentState = VSlideState.RETRACTION;
        useController = false;
        setPower(-1f);
        delays.add(new Delay(() -> getCurrentPosition() <= pos, () -> {controller.setTargetPosition(pos); useController = true;}));
    }

    public void fullRetract(Runnable action) {
        currentState = VSlideState.RETRACTION;
        leftMotor.retract(-0.75f, () -> {
            useController = true;
            currentState = VSlideState.IN;
            action.run();
        });
        rightMotor.retract(-0.75f);
        useController = false;
        controller.runToPosition(0);
    }

    public void fullRetract() {
        fullRetract(() -> {});
    }

    public HwSwitch getLimitSwitch() {
        return limitSwitch;
    }

    public void startHang() {
        useController = false;
        setPower(0f);
    }

    public void stopTransfer() {
        transfer = false;
        setPower(0f);
    }

    public boolean atBottom() {
        if (atBottom == null) atBottom = leftMotor.getCurrentPosition() < 10 && limitSwitch.isTouch();
        return atBottom;
    }

    public boolean isUseController() {
        return useController;
    }
}
