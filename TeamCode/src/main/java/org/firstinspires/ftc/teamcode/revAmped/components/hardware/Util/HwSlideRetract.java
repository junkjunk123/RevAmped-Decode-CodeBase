package org.firstinspires.ftc.teamcode.revAmped.components.hardware.Util;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Supplier;

import java.util.function.BooleanSupplier;

import revAmped.components.hardware.HwMotor;
import revAmped.components.hardware.HwSwitch;

public class HwSlideRetract {
    private boolean retract = false;
    private Supplier<Float> power;
    private HwSwitch limitSwitch;
    private HwMotor motor;
    private Runnable action;
    private BooleanSupplier atBottom;

    public HwSlideRetract(HwMotor motor, HwSwitch limitSwitch) {
        this.motor = motor;
        this.limitSwitch = limitSwitch;
    }

    public boolean isRetract() {
        return retract;
    }

    public void setRetract(Supplier<Float> power, Runnable action) {
        this.retract = true;
        this.power = power;
        this.action = action;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setRetract(Supplier<Float> power, Runnable action, BooleanSupplier atBottom) {
        this.retract = true;
        this.power = power;
        this.action = action;
        this.atBottom = atBottom;
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void stop() {
        retract = false;
    }

    public void update() {
        if (retract) {
            if (limitSwitch.isTouch() && (atBottom == null || atBottom.getAsBoolean())) {
                retract = false;
                action.run();
                action = null;
            } else {
                float currentPower = -1 * Math.abs(power.get());

                motor.setPower(currentPower);
            }
        }
    }
}
