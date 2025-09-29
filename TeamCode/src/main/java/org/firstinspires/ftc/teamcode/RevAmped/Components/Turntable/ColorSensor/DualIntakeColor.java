package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class DualIntakeColor implements DecodeColorSensor {
    private boolean rightColorRead = true;
    private final IntakeColor leftColor;
    private final IntakeColor rightColor;

    public static double LEFT_OFFSET;

    public DualIntakeColor(HardwareMap hardwareMap) {
        leftColor = new IntakeColor(hardwareMap, LEFT_OFFSET);
        rightColor = new IntakeColor(hardwareMap, 0);
    }

    public ArtifactColor getColor() {
        if (rightColorRead) {
            return rightColor.getColor();
        } else{
            return leftColor.getColor();
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
        rightColorRead = !rightColorRead;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.COLOR_SENSOR;
    }
}

