package org.firstinspires.ftc.teamcode.ExampleCode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import revAmped.components.Hardware.Hw2mDistanceSensor;
import revAmped.components.Hardware.LowPassFilter;
import revAmped.components.MathUtil.LinearFunction;

@LowPassFilter(order = 2, alpha = 0.5)
public class ExampleDistanceSensor extends Hw2mDistanceSensor {
    protected ExampleDistanceSensor(HardwareMap hardwareMap) {
        super(hardwareMap, "sensor", new LinearFunction(0, 1));
    }
}
