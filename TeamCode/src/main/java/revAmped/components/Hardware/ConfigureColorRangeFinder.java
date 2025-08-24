package revAmped.components.Hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import revAmped.util.ColorRangeFinder;

@Disabled
@Config
@TeleOp
public class ConfigureColorRangeFinder extends LinearOpMode {
    public static int brightness = 20;
    public static boolean secondary = false;
    public static double blueMin = 155.0, blueMax = 180.0, yellowMin = 50.0, yellowMax = 75.0, redMin = 10.0, redMax = 40.0;
    public static double distance = 35;

    @Override
    public void runOpMode() throws InterruptedException {
        ColorRangeFinder crf;
        if (secondary) {
            crf = new ColorRangeFinder(hardwareMap.get(RevColorSensorV3.class, "Color2"));
        } else {
            crf = new ColorRangeFinder(hardwareMap.get(RevColorSensorV3.class, "Color"));
        }

        /*
        Using this example configuration, you can detect all three sample colors based on which pin is reading true:
        both      --> yellow
        only pin0 --> blue
        only pin1 --> red
        neither   --> no object
         */

        crf.setPin0Digital(ColorRangeFinder.DigitalMode.HSV, blueMin, blueMax); // blue
        crf.setPin0Digital(ColorRangeFinder.DigitalMode.HSV, yellowMin, yellowMax); // yellow
        crf.setPin1Digital(ColorRangeFinder.DigitalMode.HSV, redMin, redMax); //red
        crf.setPin1Digital(ColorRangeFinder.DigitalMode.HSV, 240.0, 250.0);
        crf.setPin1Digital(ColorRangeFinder.DigitalMode.HSV, yellowMin, yellowMax); //yellow
        crf.setPin0DigitalMaxDistance(ColorRangeFinder.DigitalMode.HSV, distance);
        crf.setPin1DigitalMaxDistance(ColorRangeFinder.DigitalMode.HSV, distance);
        crf.setLedBrightness(brightness);
        //crf.setPin0Analog(ColorRangeFinder.AnalogMode.HSV);

        waitForStart();
        stop();
    }
}