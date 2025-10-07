package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.ColorSensor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class DualIntakeColor implements DecodeColorSensor {
    private boolean rightColorRead = true;
    private static boolean useLeft = false;
    private static boolean useRight = true;
    private final IntakeColor leftColor;
    private final IntakeColor rightColor;

    public static double LEFT_OFFSET;

    public DualIntakeColor(HardwareMap hardwareMap) {
        leftColor = new IntakeColor(hardwareMap, "color_left", LEFT_OFFSET);
        rightColor = new IntakeColor(hardwareMap, "color_right",0);
    }

    public ArtifactColor getColor() {
        rightColorRead = !rightColorRead;
        if (rightColorRead) {
            if (useLeft) return leftColor.getColor();
            if (useRight) return rightColor.getColor();
            else return ArtifactColor.NONE;
        } else {
            if (useRight) return rightColor.getColor();
            if (useLeft) return leftColor.getColor();
            else return ArtifactColor.NONE;
        }
    }

    @Override
    public String getId() {
        return leftColor.getId() + ", " + rightColor.getId();
    }

    @Override
    public void update() {
        leftColor.update();
        rightColor.update();
    }

    public void disable() {
        useLeft = false;
        useRight = false;
    }

    public void enableLeft() {
        useLeft = true;
    }

    public void enableRight() {
        useRight = true;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.COLOR_SENSOR;
    }
}

