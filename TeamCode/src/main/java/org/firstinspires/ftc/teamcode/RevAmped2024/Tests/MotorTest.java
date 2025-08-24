package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
@TeleOp(name = "MotorTest", group = "Test")
public class MotorTest extends OpMode {
    private DcMotor motor;
    public static int port = 0;
    public static boolean isControlHub = false;
    public static boolean isReversed;
    public static boolean stopped = false;
    public static float power = 1f;

    @Override
    public void init() {
        if (!isControlHub) {
            motor = hardwareMap.get(DcMotor.class, "motor" + port);
        } else {
            motor = hardwareMap.get(DcMotor.class, "motor" + (port + 4));
        }
    }

    @Override
    public void loop() {
        float power = 0;

        if (!stopped) {
            if (!isReversed) {
                power = MotorTest.power;
            } else {
                power = -1 * MotorTest.power;
            }
        }

        motor.setPower(power);

        telemetry.addData("power",motor.getPower());
    }
}
