package org.firstinspires.ftc.teamcode.RevAmped.Components.Intake;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Components.DecodeColor;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Turntable;

public class IntakeColor extends DecodeColor<Turntable.Color> {
    public static float minPurpleHue = 0f;
    public static float maxPurpleHue = 0f;
    public static float minGreenHue = 0f;
    public static float maxGreenHue = 0f;

    public IntakeColor(HardwareMap hardwareMap, String id, Turntable.Color neutral) {
        super(hardwareMap, id, neutral);
    }
}
