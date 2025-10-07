package org.firstinspires.ftc.teamcode.examples;
import com.qualcomm.robotcore.hardware.HardwareMap;
import revAmped.components.hardware.Hw2mDistanceSensor;
import revAmped.components.hardware.LowPassFilter;
import revAmped.components.math.LinearFunction;

@LowPassFilter(order = 2, alpha = 0.5)
public class ExampleDistanceSensor extends Hw2mDistanceSensor {
    protected ExampleDistanceSensor(HardwareMap hardwareMap) {
        super(hardwareMap, "sensor", new LinearFunction(0, 1));
    }
}
