package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;

@Config
public class DualIntakeColor implements DecodeColorSensor {
    private boolean rightColorRead = true;
    private static boolean useLeft = true;
    private static boolean useRight = true;
    private final IntakeColor leftColor;
    private final IntakeColor rightColor;

    public static double LEFT_OFFSET;

    public DualIntakeColor(HardwareMap hardwareMap) {
        leftColor = new IntakeColor(hardwareMap, "color_left", LEFT_OFFSET);
        rightColor = new IntakeColor(hardwareMap, "color_right",0);
        useLeft = true;
        useRight = true;
    }

    public ArtifactColor getColor() {
        rightColorRead = !rightColorRead;
        if (!rightColorRead) {
            if (useLeft) return leftColor();
            if (useRight) return rightColor();
            else return ArtifactColor.NONE;
        } else {
            if (useRight) return rightColor();
            if (useLeft) return leftColor();
            else return ArtifactColor.NONE;
        }
    }

    private ArtifactColor leftColor() {
        IntakeColor.Color.currentSensor = IntakeColor.CurrentSensor.LEFT;
        return leftColor.getColor();
    }

    private ArtifactColor rightColor() {
        IntakeColor.Color.currentSensor = IntakeColor.CurrentSensor.RIGHT;
        return rightColor.getColor();
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

    public static void disableSensors() {
        useLeft = false;
        useRight = false;
    }

    public static void enableSensors() {
        useLeft = true;
        useRight = true;
    }
    public void setOffset(double leftOffset){
        LEFT_OFFSET = leftOffset;
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