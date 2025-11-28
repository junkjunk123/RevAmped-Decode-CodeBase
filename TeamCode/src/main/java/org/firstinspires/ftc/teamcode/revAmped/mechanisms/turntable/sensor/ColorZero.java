package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxGreenDistanceZero;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxGreenHueZero;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxPurpleDistanceZero;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxPurpleHueZero;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.minGreenHueZero;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.minPurpleHueZero;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.EnumMap;

public class ColorZero extends DecodeColorV2 {
    public ColorZero(HardwareMap hardwareMap) {
        super(hardwareMap, "colorOne");
    }

    @Override
    protected EnumMap<DetectionColor, ColorEvaluator> buildColorEvaluatorMap() {
        EnumMap<DetectionColor, ColorEvaluator> map = new EnumMap<>(DetectionColor.class);
        map.put(DetectionColor.PURPLE, (hue, distance) ->
                hue >= minPurpleHueZero && hue <= maxPurpleHueZero && distance <= maxPurpleDistanceZero
        );
        map.put(DetectionColor.GREEN, (hue, distance) ->
                hue >= minGreenHueZero && hue <= maxGreenHueZero && distance <= maxGreenDistanceZero
        );

        return map;
    }
}
