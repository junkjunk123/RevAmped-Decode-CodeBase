package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.blueMax;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.blueMin;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.maxBlueDistance;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.maxDistance;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.redMax;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.redMin;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.yellowMax;
import static org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.ColorSensorPrototypes.ITDColorSensor.yellowMin;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import revAmped.components.MathUtil.RGBValue;

@Config
@TeleOp(name = "ColorTest", group = "Test")
public class ColorTest extends OpMode {
    private RevColorSensorV3 colorSensor;

    @Override
    public void init() {
        RevColorSensorV3 colorSensor = null;
        try {
            colorSensor = hardwareMap.get(RevColorSensorV3.class,"Color");
        } catch (Exception e) {
            RobotLog.e("missing: color" + e.getMessage());
            telemetry.addLine("missing: color");
        }
        this.colorSensor = colorSensor;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        double hue = new RGBValue(((NormalizedColorSensor) colorSensor).getNormalizedColors()).toHSV().getH();
        boolean red = (hue >= redMin && hue <= redMax);
        boolean blue = hue >= blueMin && hue <= blueMax;
        boolean yellow = hue >= yellowMin && hue <= yellowMax;
        double distance = colorSensor.getDistance(DistanceUnit.MM);

        if (distance <= maxDistance || (distance <= maxBlueDistance && blue)) {
            telemetry.addData("hue", hue);
            telemetry.addData("red", red);
            telemetry.addData("blue", blue);
            telemetry.addData("yellow", yellow);
        }
        telemetry.addData("distance", colorSensor.getDistance(DistanceUnit.MM));
        telemetry.update();
    }
}
