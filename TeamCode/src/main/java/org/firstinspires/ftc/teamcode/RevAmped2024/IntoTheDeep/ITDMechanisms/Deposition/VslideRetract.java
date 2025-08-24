package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition;

import com.qualcomm.robotcore.hardware.DcMotor;

import revAmped.components.Hardware.HwMotor;
import revAmped.util.Range;

public class VslideRetract {
    private volatile boolean running = false;
    private int highPosition = 0;
    private final HwMotor topMotor;
    private final HwMotor bottomMotor;

    public VslideRetract(HwMotor motorTop, HwMotor motorBottom) {
        this.topMotor = motorTop;
        this.bottomMotor = motorBottom;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setHighPosition(int highPosition) {
        this.highPosition = highPosition;
    }

    public float calcPowerAbsolute(int currentPos, float maxPower) {
        if (maxPower > 0) {
            maxPower = -maxPower;
        }

        float power = Math.abs(highPosition - currentPos) * 2 * maxPower / (float) highPosition - maxPower;

        power = Range.clip(power, maxPower, -0.4f);

        return power;
    }

    public float retractSlowEnd(int currentPos) {
        if (currentPos < 100) {
            return -0.4f;
        }

        return -0.75f;
    }

    public void update(int currentPos) {
        if (running) {
            float power = retractSlowEnd(currentPos);
            bottomMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            topMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bottomMotor.setPower(power);
            topMotor.setPower(power);
        }
    }
}
