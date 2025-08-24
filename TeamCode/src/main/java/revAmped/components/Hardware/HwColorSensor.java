package revAmped.components.Hardware;

import static android.graphics.Color.red;

import android.graphics.Color;
import android.graphics.ColorSpace;

import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import revAmped.components.MathUtil.HSVValue;
import revAmped.components.MathUtil.RGBValue;

public class HwColorSensor implements HwDevice {
    private RevColorSensorV3 colorSensor;
    private RGBValue rgbValues;
    private HSVValue hsvValues;
    private double distance = -1;
    private Float alpha;
    private final String id;

    public HwColorSensor(HardwareMap hardwareMap, String id) {
        this.id = id;
        colorSensor = hardwareMap.get(RevColorSensorV3.class, id);
        rgbValues = new RGBValue(0,0,0);
        hsvValues = new HSVValue(0,0,0);
        alpha = 0f;
    }

    public float getNormalizedAlpha() {
        if (alpha == null) {
            updateRGBValues();
        }

        return alpha;
    }

    protected void updateRGBValues() {
        NormalizedRGBA values = ((NormalizedColorSensor) (colorSensor)).getNormalizedColors();
        rgbValues = new RGBValue(values.red * 255.0, values.green * 255.0, values.blue * 255.0);
        alpha = values.alpha;
    }

    protected void updateHSVValues() {
        if (rgbValues == null) {
            updateRGBValues();
        }

        hsvValues = rgbValues.toHSV();
    }

    @Override
    public String getId() {
        return id;
    }

    public void update() {
        rgbValues = null;
        hsvValues = null;
        alpha = null;
        distance = -1;
    }

    public HSVValue getHSVValues() {
        if (hsvValues == null) {
            updateHSVValues();
        }

        return hsvValues;
    }

    public RGBValue getRGBValues() {
        if (rgbValues == null) {
            updateRGBValues();
        }

        return rgbValues;
    }

    public double getDistanceMM() {
        if (distance == -1) {
            distance = colorSensor.getDistance(DistanceUnit.MM);
        }

        return distance;
    }

    public void setBrandBrushlands() {
        ((LynxI2cDeviceSynch) getColorSensor().getDeviceClient()).setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
    }

    public RevColorSensorV3 getColorSensor() {
        return colorSensor;
    }
}
