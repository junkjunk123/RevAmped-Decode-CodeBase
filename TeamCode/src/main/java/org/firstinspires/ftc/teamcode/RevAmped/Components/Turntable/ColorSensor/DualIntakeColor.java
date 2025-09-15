package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.ColorSensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class DualIntakeColor implements Color {
    private boolean rightColorRead = true;
    private final IntakeColor leftColor;
    private final IntakeColor rightColor;

    public DualIntakeColor(HardwareMap hardwareMap, IntakeColor.Color neutral){
        leftColor = new IntakeColor(hardwareMap, neutral);
        rightColor = new IntakeColor(hardwareMap, neutral);
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

