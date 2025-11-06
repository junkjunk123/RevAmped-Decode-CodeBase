package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor;

import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.maxGreenDistanceTwo;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.maxGreenHueTwo;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.maxPurpleDistanceTwo;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.maxPurpleHueTwo;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.minGreenHueTwo;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.colorsensor.ColorManager.minPurpleHueTwo;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.EnumMap;

public class ColorTwo extends DecodeColorV2{
    public ColorTwo(HardwareMap hardwareMap) {
        super(hardwareMap, "colorTwo");
    }

    @Override
    protected EnumMap<DecodeColorV2.DetectionColor, DecodeColorV2.ColorEvaluator> buildColorEvaluatorMap() {
        EnumMap<DecodeColorV2.DetectionColor, DecodeColorV2.ColorEvaluator> map = new EnumMap<>(DecodeColorV2.DetectionColor.class);
        map.put(DecodeColorV2.DetectionColor.PURPLE, (hue, distance) ->
                hue >= minPurpleHueTwo && hue <= maxPurpleHueTwo && distance <= maxPurpleDistanceTwo
        );
        map.put(DecodeColorV2.DetectionColor.GREEN, (hue, distance) ->
                hue >= minGreenHueTwo && hue <= maxGreenHueTwo && distance <= maxGreenDistanceTwo
        );
        return map;
    }
}
