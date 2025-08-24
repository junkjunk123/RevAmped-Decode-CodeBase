package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes;

import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;

public class ITDDualRevColorSensor implements ITDColor {
    public final ITDColorSensor leftColorSensor;
    public final ITDColorSensor rightColorSensor;
    private DetectionColor color = DetectionColor.None;

    public ITDDualRevColorSensor(ITDColorSensor leftColorSensor, ITDColorSensor rightColorSensor) {
        this.leftColorSensor = leftColorSensor;
        this.rightColorSensor = rightColorSensor;
        ((LynxI2cDeviceSynch) leftColorSensor.getColorSensor().getDeviceClient()).setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
        ((LynxI2cDeviceSynch) rightColorSensor.getColorSensor().getDeviceClient()).setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
    }

    public void update() {
        color = null;
        leftColorSensor.update();
        rightColorSensor.update();
    }

    public DetectionColor getColor() {
        if (color == null) {
            color = updateColor();
        }

        return color;
    }

    public String detectionColorToString() {
        return getColor().name();
    }

    public DetectionColor updateColor() {
        DetectionColor leftColor = leftColorSensor.getColor(11);
        DetectionColor rightColor = rightColorSensor.getColor(-11);

        if (leftColor == DetectionColor.Red || rightColor == DetectionColor.Red) {
            return DetectionColor.Red;
        }

        if (leftColor == DetectionColor.Blue || rightColor == DetectionColor.Blue) {
            return DetectionColor.Blue;
        }

        if (leftColor == DetectionColor.Yellow && rightColor == DetectionColor.Yellow) {
            return DetectionColor.Yellow;
        }

        return DetectionColor.None;
    }
}
