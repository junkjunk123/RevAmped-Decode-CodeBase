package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import java.text.DecimalFormat;
import revAmped.components.GamepadDevices.Button;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.linear.util.SelectLinear;

@Config
@TeleOp(name = "ServoCalibrateTest", group = "Test")
public class ServoCalibrateTest extends LinearOpMode {
    private Servo servo;
    private RevAmpedGamepad gamepad_1;
    public static int denominator = 255;
    private AnalogInput analog;
    private AnalogInput analog1;
    private AnalogInput analog2;
    public static int TICK_CHANGE = 1;
    private final static DecimalFormat numberFormatter = new DecimalFormat("#.###");
    private int port = 0;
    public static int prevPort = 0;
    public static boolean isControlHub = true;
    private DcMotor intake;
    private Telemetry telemetryA;
    private boolean singleTickMode = false;
    private int selectedPort = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        analog = hardwareMap.get(AnalogInput.class, "servo0_analog");
        analog1 = hardwareMap.get(AnalogInput.class, "analog1");
        analog2 = hardwareMap.get(AnalogInput.class, "analog2");
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        intake = hardwareMap.get(DcMotor.class, "intake");
        telemetryA = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        while (opModeInInit()) {
            gamepad_1.update();
            telemetry.addData("port", selectedPort);
            telemetry.addData("isControlHub", isControlHub);
            telemetry.update();

            if (gamepad_1.dpad_up.isPress()) {
                selectedPort++;
                selectedPort %= 6;
                if (selectedPort < 0) {
                    selectedPort += 6;
                }
            }

            if (gamepad_1.dpad_down.isPress()) {
                selectedPort--;
                selectedPort %= 6;
                if (selectedPort < 0) {
                    selectedPort += 6;
                }
            }

            if (gamepad_1.dpad_right.isPress()) {
                isControlHub = !isControlHub;
            }
        }

        waitForStart();
        port = selectedPort;
        if (isControlHub) {
            servo = hardwareMap.get(Servo.class, "servo" + port);
        } else {
            servo = hardwareMap.get(Servo.class, "servo" + (port + 6));
        }
        prevPort = port;
        int initialPos = (int) servo.getPosition() * 255;
        int posJoy1;
        if (initialPos == 0) {
            posJoy1 = denominator / 2;
        } else {
            posJoy1 = initialPos;
        }

        while (opModeIsActive()) {
            gamepad_1.update();
            long timeStamp = System.currentTimeMillis();

            if (!singleTickMode) {
                if (gamepad1.x && Button.BTN_MINUS.canPress4Short(timeStamp)) {
                    posJoy1 -= TICK_CHANGE;
                } else if (gamepad1.b && Button.BTN_PLUS.canPress4Short(timeStamp)) {
                    posJoy1 += TICK_CHANGE;
                }
            } else {
                if (gamepad1.x && Button.BTN_MINUS.canPress(timeStamp)) {
                    posJoy1 -= TICK_CHANGE;
                } else if (gamepad1.b && Button.BTN_PLUS.canPress(timeStamp)) {
                    posJoy1 += TICK_CHANGE;
                }
            }

            if (gamepad1.y && Button.BTN_MAX.canPress(timeStamp)) {
                singleTickMode = !singleTickMode;
            } else if (gamepad1.right_stick_button && Button.BTN_MID.canPress(timeStamp)) {
                posJoy1 = 128;
            }

            posJoy1 = Range.clip(posJoy1, 0, denominator);
            servo.setPosition((float) posJoy1 / denominator);

            if (gamepad_1.right_bumper.isPress()) {
                TICK_CHANGE += 5;
                TICK_CHANGE = Range.clip(TICK_CHANGE, 1, 100);
            } else if (gamepad_1.left_bumper.isPress()) {
                TICK_CHANGE -= 5;
                TICK_CHANGE = Range.clip(TICK_CHANGE, 1, 100);
            } else if (gamepad_1.right_stick_button.isPress()) {
                TICK_CHANGE = 1;
            }

            if (gamepad_1.dpad_right.isPress()) {
                intake.setPower(-1f);
            } else if (gamepad_1.dpad_left.isPress()) {
                intake.setPower(0f);
            }

            if (gamepad_1.left_trigger > 0.3) {
                intake.setPower(1f);
            }

            telemetryA.addData("Servo", "servo"+port);
            telemetryA.addData("singleTickMode",singleTickMode);
            telemetryA.addData("Position", Integer.toString(posJoy1));
            telemetryA.addData("TICK_CHANGE",TICK_CHANGE);
            telemetry.addData("PWM",
                   Integer.toString((int) (posJoy1*1805.2/255f+591.68)));
            telemetryA.addData("DepoArmVoltage", numberFormatter.format(analog.getVoltage()));
            telemetryA.addData("IntakeArmVoltage",numberFormatter.format(analog1.getVoltage()));
            telemetryA.addData("Ultrasonic",numberFormatter.format(analog2.getVoltage()));
            telemetryA.update();
            idle();
        }
    }
}
