package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.HoodState;

public class HoodServo extends Hood {
    public HoodServo(HardwareMap hardwareMap, boolean setPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", setPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public HoodServo(HardwareMap hardwareMap, float initialPosition) throws IllegalArgumentException {
        super(hardwareMap, "hood", initialPosition, HoodState.REST, ServoBrand.AXON_MAX);
    }

    public static float HOOD_REST;
    public static float HOOD_MAX_POS;
    public static float HOOD_MIN_POS;
    public static float HOOD_MAX_RAD;
    public static float HOOD_MIN_RAD;

    public static float AUTO_PRELOADS;
    public static float AUTO_SET_1;
    public static float AUTO_SET_2;
    public static float AUTO_SET_3;

    public static float TELE_NEAR_PRESET;
    public static float TELE_FAR_PRESET;
    public static float TELE_MED_PRESET;

    private boolean updateLock = false;

    public void toggleShootingPreset() {
        if (getCurrentState() == HoodState.FAR_PRESET) setCurrentState(HoodState.NEAR_PRESET);
        else if (getCurrentState() == HoodState.MED_PRESET) setCurrentState(HoodState.FAR_PRESET);
        else if (getCurrentState() == HoodState.NEAR_PRESET) setCurrentState(HoodState.MED_PRESET);
    }

    public void switchToPreset(double dist) {
        if (dist <= 50) setCurrentState(HoodState.NEAR_PRESET);
        else if (50 < dist && dist < 100) setCurrentState(HoodState.MED_PRESET);
        else setCurrentState(HoodState.FAR_PRESET);
    }

    public boolean isPreset() {
        return getCurrentState().isPreset();
    }

    public void updatePreset(double dist) {
        Globals.telemetry.addData("distance",dist);
        //if (updateLock || !isPreset()) return;
        if (dist <= 50) setCurrentState(HoodState.NEAR_PRESET);
        else if (50 < dist && dist < 100) setCurrentState(HoodState.MED_PRESET);
        else setCurrentState(HoodState.FAR_PRESET);
    }

    @Override
    public void setPreset(double dist) {
        //if (updateLock) return;
        if (dist <= 50) setCurrentState(HoodState.NEAR_PRESET);
        else if (50 < dist && dist < 100) setCurrentState(HoodState.MED_PRESET);
        else setCurrentState(HoodState.FAR_PRESET);
    }

    public void setUpdateLock(boolean updateLock) {
        this.updateLock = updateLock;
    }
}
