package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class ColorManager {
    private final ColorZero compartmentZeroColor;
    private final ColorOne compartmentOneColor;
    private final ColorTwo compartmentTwoColor;
    private int currentSensor = 1;

    public static float minPurpleHueZero = 270;
    public static float maxPurpleHueZero = 290;
    public static float maxPurpleDistanceZero = 100;
    public static float minPurpleHueOne = 270;
    public static float maxPurpleHueOne = 290;
    public static float maxPurpleDistanceOne = 100;
    public static float minPurpleHueTwo = 270;
    public static float maxPurpleHueTwo = 290;
    public static float maxPurpleDistanceTwo = 100;

    public static float minGreenHueZero = 100;
    public static float maxGreenHueZero = 140;
    public static float maxGreenDistanceZero = 100;
    public static float minGreenHueOne = 100;
    public static float maxGreenHueOne = 140;
    public static float maxGreenDistanceOne = 100;
    public static float minGreenHueTwo = 100;
    public static float maxGreenHueTwo = 140;
    public static float maxGreenDistanceTwo = 100;

    public ColorManager(HardwareMap hardwareMap) {
        compartmentOneColor = null;
        compartmentZeroColor = null;
        compartmentTwoColor = null;
        //compartmentZeroColor = new ColorZero(hardwareMap);
        //compartmentOneColor = new ColorOne(hardwareMap);
        //compartmentTwoColor = new ColorTwo(hardwareMap);
    }

    public void update() {
        currentSensor = (currentSensor % 2) + 1;
        //compartmentZeroColor.update();
        //compartmentOneColor.update();
        //compartmentTwoColor.update();
    }

    public DecodeColorSensor.ArtifactColor getCompartmentOneColor() {
        return DecodeColorSensor.ArtifactColor.NONE; //compartmentOneColor.getColor();
    }

    public DecodeColorSensor.ArtifactColor getCompartmentTwoColor() {
        return DecodeColorSensor.ArtifactColor.NONE; //compartmentTwoColor.getColor();
    }

    public DecodeColorSensor.ArtifactColor getCompartmentZeroColor() {
        return DecodeColorSensor.ArtifactColor.NONE; //compartmentZeroColor.getColor();
    }

    public DecodeColorV2 getSensorOne() {
        return compartmentOneColor;
    }

    public DecodeColorV2 getSensorTwo() {
        return compartmentTwoColor;
    }

    public DecodeColorV2 getSensorZero() {return compartmentZeroColor;}
}
