package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class DualIntakeColor implements DecodeColorSensor {
    private boolean rightColorRead = true;
    private final IntakeColor leftColor;
    private final IntakeColor rightColor;

    public DualIntakeColor(HardwareMap hardwareMap) {
        leftColor = new IntakeColor(hardwareMap);
        rightColor = new IntakeColor(hardwareMap);
    }

    public ArtifactColor getColor() {
        if (rightColorRead) {
            return rightColor.getColor();
        } else{
            return leftColor.getColor();
        }
    }

    @Override
    public void update() {
        leftColor.update();
        rightColor.update();
        rightColorRead=!rightColorRead;
    }
}

