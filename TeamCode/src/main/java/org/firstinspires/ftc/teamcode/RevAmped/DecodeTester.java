package org.firstinspires.ftc.teamcode.RevAmped;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import revAmped.components.GamepadDevices.Button;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;

@Config
@TeleOp(group = "Test")
public class DecodeTester extends RevAmpedOpMode {
    private Servo servo;
    private int posJoy1;
    private long timeStamp = 0;
    private RevAmpedGamepad gamepad_1;
    public static int denominator = 255;
    public static int TICK_CHANGE = 1;
    private final static DecimalFormat numberFormatter = new DecimalFormat("#.###");
    private Telemetry telemetryA;
    private boolean singleTickMode = false;
    private int selectedPort = 0;
    private List<String> servos;
    private String currentServo;
    private boolean selecting = true;
    private HashMap<String, Integer> calibratedPositions = new HashMap<>();
    private enum Test{
        SERVO;
    }
    private Test test;

    @Override
    public void RevAmpedInit() {
        servos = List.of("");
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        telemetryA = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();
    }

    @Override
    public void RevAmpedInitLoop() {
        gamepad_1.update();
        telemetry.addData("servo", servos.get(selectedPort));
        telemetry.update();

        if (gamepad_1.dpad_up.isPress()) {
            selectedPort++;
            selectedPort %= servos.size();
            if (selectedPort < 0) {
                selectedPort += servos.size();
            }
        }

        if (gamepad_1.dpad_down.isPress()) {
            selectedPort--;
            selectedPort %= servos.size();
            if (selectedPort < 0) {
                selectedPort += servos.size();
            }
        }
    }

    @Override
    public void RevAmpedStart() {
        currentServo = servos.get(selectedPort);
        servo = hardwareMap.get(Servo.class, currentServo);
        int initialPos = (int) servo.getPosition() * 255;
        if (initialPos == 0) {
            posJoy1 = denominator / 2;
        } else {
            posJoy1 = initialPos;
        }
        calibratedPositions.put(currentServo, posJoy1);
    }

    @Override
    public void RevAmpedLoop() {
        switch (test){
            case SERVO -> {
                servoTest();
            }
        }

    }

    public void getServo() {
        servo = hardwareMap.get(Servo.class, currentServo);
        calibratedPositions.put(currentServo, posJoy1);
    }

    public void servoTest(){
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

        if (gamepad_1.dpad_up.isPress()) {
            selectedPort++;
            selectedPort %= servos.size();
            if (selectedPort < 0) {
                selectedPort += servos.size();
            }
            currentServo = servos.get(selectedPort);
            if (selecting && !calibratedPositions.containsKey(currentServo)) getServo();
        }

        if (gamepad_1.dpad_down.isPress()) {
            selectedPort--;
            selectedPort %= servos.size();
            if (selectedPort < 0) {
                selectedPort += servos.size();
            }
            currentServo = servos.get(selectedPort);
            if (selecting && !calibratedPositions.containsKey(currentServo)) getServo();
        }

        if (gamepad_1.left_stick_button.isPress()) {
            selecting = !selecting;
        }

        telemetryA.addData("Servo", currentServo);
        calibratedPositions.forEach((u, v) -> telemetryA.addData(u, Integer.toString(v)));
        telemetryA.addData("singleTickMode",singleTickMode);
        telemetryA.addData("selecting",selecting);
        telemetryA.addData("TICK_CHANGE",TICK_CHANGE);
        telemetry.addData("PWM",
                Integer.toString((int) (posJoy1*1805.2/255f+591.68)));
        telemetryA.update();
    }

    @Override
    public void update() {
        gamepad_1.update();
        timeStamp = System.currentTimeMillis();
        if (calibratedPositions.containsKey(currentServo)) calibratedPositions.replace(currentServo, posJoy1);
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
