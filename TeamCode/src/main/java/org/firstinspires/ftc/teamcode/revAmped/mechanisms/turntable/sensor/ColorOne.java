package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxGreenDistanceOne;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxGreenHueOne;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxPurpleDistanceOne;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.maxPurpleHueOne;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.minGreenHueOne;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.ColorManager.minPurpleHueOne;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.EnumMap;

public class ColorOne extends DecodeColorV2 {
    public ColorOne(HardwareMap hardwareMap) {
        super(hardwareMap, "colorOne");
    }

    @Override
    protected EnumMap<DetectionColor, ColorEvaluator> buildColorEvaluatorMap() {
        EnumMap<DetectionColor, ColorEvaluator> map = new EnumMap<>(DetectionColor.class);
        map.put(DetectionColor.PURPLE, (hue, distance) ->
                hue >= minPurpleHueOne && hue <= maxPurpleHueOne && distance <= maxPurpleDistanceOne
        );
        map.put(DetectionColor.GREEN, (hue, distance) ->
                hue >= minGreenHueOne && hue <= maxGreenHueOne && distance <= maxGreenDistanceOne
        );

        return map;
    }
}
