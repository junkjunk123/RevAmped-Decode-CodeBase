package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public abstract class Hood extends HwServo2<Types.HoodState> {
    protected Hood(HardwareMap hardwareMap, String id, boolean setPosition, Types.HoodState initialState, ServoBrand brand) throws IllegalArgumentException {
        super(hardwareMap, id, setPosition, initialState, brand);
    }

    protected Hood(HardwareMap hardwareMap, String id, float initialPosition, Types.HoodState initialState, ServoBrand brand) throws IllegalArgumentException {
        super(hardwareMap, id, initialPosition, initialState, brand);
    }

    public abstract void toggleShootingPreset();

    public abstract void switchToPreset(double dist);

    public abstract boolean isPreset();

    public abstract void updatePreset(double dist);

    public abstract void setPreset(double dist);

    public abstract void setUpdateLock(boolean lock);
}
