package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.dfrobot.HuskyLens.Block;

import org.firstinspires.ftc.teamcode.revAmped.components.math.Vector2D;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor.DecodeColorSensor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

public class ConceptHuskyLens extends DecodeRevAmpedOpMode {
    private HuskyLens camera;

    @Override
    public void RevAmpedInit() {
        camera = hardwareMap.get(HuskyLens.class, "huskylens");
        camera.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
    }

    @Override
    public void RevAmpedInitLoop() {
        Block[] blocks = camera.blocks();
        int green = 0;
        int purple = 0;

        for (Block block : blocks) {
            if (block.height < 0.75 * block.width)
                continue;
            if (block.width < 0.75 * block.height)
                continue;

            DecodeColorSensor.ArtifactColor color = block.id == 1 ?
                    DecodeColorSensor.ArtifactColor.GREEN : DecodeColorSensor.ArtifactColor.PURPLE;

            if (color == DecodeColorSensor.ArtifactColor.GREEN) {
                green++;
                telemetry.addData(color.name() + " " + green, new Vector2D(block.x, block.y));
            } else {
                purple++;
                telemetry.addData(color.name() + " " + purple, new Vector2D(block.x, block.y));
            }
        }

        telemetry.addData("total", green + purple);
        telemetry.update();
    }

    @Override
    public void RevAmpedLoop() {
        Block[] blocks = camera.blocks();
        PriorityQueue<Artifact> artifactPQ = new PriorityQueue<>(Artifact.comparator());
        int green = 0;
        int purple = 0;

        for (Block block : blocks) {
            if (block.height < 0.75 * block.width)
                continue;
            if (block.width < 0.75 * block.height)
                continue;

            DecodeColorSensor.ArtifactColor color = block.id == 1 ?
                    DecodeColorSensor.ArtifactColor.GREEN : DecodeColorSensor.ArtifactColor.PURPLE;

            if (color == DecodeColorSensor.ArtifactColor.GREEN) {
                green++;
            } else {
                purple++;
            }

            artifactPQ.offer(new Artifact(new Vector2D(block.x, block.y), color));
        }

        for (int i = 0; i < 3; i++)
            telemetry.addData(String.valueOf(i), artifactPQ.poll());

        telemetry.addData("totalGreen", green);
        telemetry.addData("totalPurple", purple);
        telemetry.update();
    }

    private record Artifact(Vector2D pos, DecodeColorSensor.ArtifactColor color) {
        private static Comparator<Artifact> comparator() {
            Vector2D center = new Vector2D(160, 120);
            Comparator<Artifact> dist = (u, v) -> Comparator.comparingDouble((ToDoubleFunction<? super Artifact>) a -> a.pos.distSquared(center))
                    .compare(u, v);
            return dist.reversed();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
