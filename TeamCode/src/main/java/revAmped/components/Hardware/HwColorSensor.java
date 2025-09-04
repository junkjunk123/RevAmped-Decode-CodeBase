package revAmped.components.Hardware;

import static android.graphics.Color.red;

import static revAmped.components.Misc.Globals.testing;

import android.graphics.Color;
import android.graphics.ColorSpace;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import revAmped.components.MathUtil.HSVValue;
import revAmped.components.MathUtil.RGBValue;

public class HwColorSensor implements HwDevice, HwDevice.Sensor<HSVValue> {
    private RevColorSensorV3 colorSensor;
    private RGBValue rgbValues;
    private HSVValue hsvValues;
    private double distance = -1;
    private Float alpha;
    private final String id;

    public HwColorSensor(HardwareMap hardwareMap, String id) {
        this.id = id;
        if (!testing) {
            try {
                colorSensor = hardwareMap.get(RevColorSensorV3.class, id);
            } catch (Exception e) {
                throw new InitializationException(this);
            }
        }
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
        NormalizedRGBA values;

        if (!testing) {
            values = ((NormalizedColorSensor) (colorSensor)).getNormalizedColors();
            rgbValues = new RGBValue(values.red * 255.0, values.green * 255.0, values.blue * 255.0);
            alpha = values.alpha;
        } else {
            rgbValues = new RGBValue(255, 255, 255);
            alpha = 1f;
        }
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
            if (!testing) distance = colorSensor.getDistance(DistanceUnit.MM);
            else distance = 1;
        }

        return distance;
    }

    public void setBrandBrushlands() {
        if (!testing) ((LynxI2cDeviceSynch) getColorSensor().getDeviceClient()).setBusSpeed(LynxI2cDeviceSynch.BusSpeed.FAST_400K);
    }

    public RevColorSensorV3 getColorSensor() {
        return colorSensor;
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }

    @Override
    public HSVValue getReading() {
        return getHSVValues();
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.COLOR_SENSOR;
    }
}
