package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.EnumMap;

@Config
public class ColorManager {
    private final DecodeColorV2 compartmentOneColor;
    private final DecodeColorV2 compartmentTwoColor;
    private int currentSensor = 1;

    public static float minPurpleHueOne = 270;
    public static float maxPurpleHueOne = 290;
    public static float maxPurpleDistanceOne = 100;
    public static float minPurpleHueTwo = 270;
    public static float maxPurpleHueTwo = 290;
    public static float maxPurpleDistanceTwo = 100;

    public static float minGreenHueOne = 100;
    public static float maxGreenHueOne = 140;
    public static float maxGreenDistanceOne = 100;
    public static float minGreenHueTwo = 100;
    public static float maxGreenHueTwo = 140;
    public static float maxGreenDistanceTwo = 100;

    public enum ReadMode {
        BOTH,
        ALTERNATING
    }

    private ReadMode readMode = ReadMode.BOTH;

    public ColorManager(HardwareMap hardwareMap) {
        compartmentOneColor = new DecodeColorV2(hardwareMap, "colorOne") {
            @Override
            public EnumMap<DetectionColor, ColorEvaluator> buildColorEvaluatorMap() {
                EnumMap<DetectionColor, ColorEvaluator> map = new EnumMap<>(DetectionColor.class);
                map.put(DetectionColor.PURPLE, (hue, distance) ->
                    hue >= minPurpleHueOne && hue <= maxPurpleHueOne && distance <= maxPurpleDistanceOne
                );
                map.put(DetectionColor.GREEN, (hue, distance) ->
                    hue >= minGreenHueOne && hue <= maxGreenHueOne && distance <= maxGreenDistanceOne
                );

                return map;
            }
        };

        compartmentTwoColor = new DecodeColorV2(hardwareMap, "colorTwo") {
            @Override
            public EnumMap<DetectionColor, ColorEvaluator> buildColorEvaluatorMap() {
                EnumMap<DetectionColor, ColorEvaluator> map = new EnumMap<>(DetectionColor.class);
                map.put(DetectionColor.PURPLE, (hue, distance) ->
                    hue >= minPurpleHueTwo && hue <= maxPurpleHueTwo && distance <= maxPurpleDistanceTwo
                );
                map.put(DetectionColor.GREEN, (hue, distance) ->
                    hue >= minGreenHueTwo && hue <= maxGreenHueTwo && distance <= maxGreenDistanceTwo
                );
                return map;
            }
        };
    }

    public void update() {
        currentSensor = (currentSensor % 2) + 1;
        compartmentOneColor.update();
        compartmentTwoColor.update();
    }

    public DecodeColorSensor.ArtifactColor getCompartmentOneColor() {
        if (readMode == ReadMode.ALTERNATING && currentSensor == 2) return DecodeColorSensor.ArtifactColor.NONE;
        return compartmentOneColor.getColor();
    }

    public DecodeColorSensor.ArtifactColor getCompartmentTwoColor() {
        if (readMode == ReadMode.ALTERNATING && currentSensor == 1) return DecodeColorSensor.ArtifactColor.NONE;
        return compartmentTwoColor.getColor();
    }

    public void setReadMode(ReadMode readMode) {
        this.readMode = readMode;
    }

    public ReadMode getReadMode() {
        return readMode;
    }

    public DecodeColorV2 getSensorOne() {
        return compartmentOneColor;
    }

    public DecodeColorV2 getSensorTwo() {
        return compartmentTwoColor;
    }
}
