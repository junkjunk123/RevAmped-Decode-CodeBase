package org.firstinspires.ftc.teamcode.revAmped.mechanisms.vision;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwDevice;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;

public class DecodeHuskyLens implements HwDevice {
    private static Vector2D CROSSHAIR = new Vector2D(160, 120);
    private final HuskyLens huskyLens;
    private boolean running;
    private Vector2D target;

    private record ImageArtifact(Vector2D pos, DecodeColorSensor.ArtifactColor color) {
        private static Comparator<ImageArtifact> comparator() {
            Vector2D center = new Vector2D(160, 120);
            Comparator<ImageArtifact> dist = (u, v) -> Comparator.comparingDouble((ToDoubleFunction<? super ImageArtifact>) a -> a.pos.distSquared(center))
                    .compare(u, v);
            return dist.reversed();
        }
    }

    public DecodeHuskyLens(HardwareMap hardwareMap) {
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");
        stop();
    }

    @Override
    public String getId() {
        return "huskylens";
    }

    public void stop() {
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.NONE);
        running = false;
    }

    public void start() {
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
        running = true;
    }

    @Override
    public void update() {

    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.CAMERA;
    }
}
