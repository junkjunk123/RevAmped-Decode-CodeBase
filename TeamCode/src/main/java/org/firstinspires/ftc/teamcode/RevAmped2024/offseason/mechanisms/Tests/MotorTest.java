package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Constants;

import java.util.HashMap;
import java.util.List;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;
import revAmped.components.Misc.RevAmpedTestOpMode;

@Config
@TeleOp(name = "MotorTest", group = "Test")
public class MotorTest extends RevAmpedTestOpMode {
    private DcMotor motor;
    public static int port = 0;
    public static boolean isReversed;
    public static boolean stopped = false;
    public static float power = 1f;
    private List<String> motors;
    private RevAmpedGamepad gamepad_1;
    private HardwareMap hwMap;
    private HashMap<String, DcMotorSimple.Direction> directions = new HashMap<>();

    @Override
    public void RevAmpedInit() {
        motors = List.of("hslides", "motor_lf","motor_rf","motor_lb","motor_rb","vslides","vslides2");
        directions.put("hslides", Constants.HSLIDE_DIRECTION);
        directions.put("motor_lf",Constants.DRIVE_DIRECTIONS.get(0));
        directions.put("motor_rf",Constants.DRIVE_DIRECTIONS.get(3));
        directions.put("motor_lb",Constants.DRIVE_DIRECTIONS.get(1));
        directions.put("motor_rb",Constants.DRIVE_DIRECTIONS.get(2));
    }

    @Override
    public void RevAmpedInitLoop() {
        gamepad_1.update();
        telemetry.addData("motor", motors.get(port));
        telemetry.addData("isReversed", isReversed);
        telemetry.addData("stopped", stopped);
        telemetry.addData("power", power);
        telemetry.update();

        if (gamepad_1.dpad_up.isPress()) {
            port++;
            port %= motors.size();
            if (port < 0) {
                port += motors.size();
            }
        }

        if (gamepad_1.dpad_down.isPress()) {
            port--;
            port %= motors.size();
            if (port < 0) {
                port += motors.size();
            }
        }

        if (gamepad_1.b.isPress()) {
            isReversed = !isReversed;
        }

        if (gamepad_1.x.isPress()) {
            stopped = !stopped;
        }

        if (gamepad_1.y.isPress()) {
            power += 0.1f;
            power = Math.min(power, 1f);
        }

        if (gamepad_1.left_bumper.isPress()) {
            power -= 0.1f;
            power = Math.max(power, -1f);
        }
    }

    @Override
    public void RevAmpedStart() {
        motor = hwMap.get(DcMotor.class, motors.get(port));
    }

    @Override
    public void RevAmpedLoop() {
        float power = 0;

        if (gamepad_1.b.isPress()) {
            isReversed = !isReversed;
        }

        if (gamepad_1.y.isPress()) {
            power += 0.1f;
            power = Math.min(power, 1f);
        }

        if (gamepad_1.left_bumper.isPress()) {
            power -= 0.1f;
            power = Math.max(power, -1f);
        }

        if (gamepad_1.x.isPress()) {
            stopped = !stopped;
        }

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

    @Override
    public void update() {
        gamepad_1.update();
        telemetry.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return new OpModeType() {
            @Override
            public boolean isTeleOp() {
                return true;
            }

            @Override
            public boolean isAuto() {
                return false;
            }

            @Override
            public boolean isTest() {
                return true;
            }
        };
    }

    @Override
    public void initialize(RevAmpedOpMode op) {
        gamepad_1 = new RevAmpedGamepad(op.gamepad1);
        hwMap = op.hardwareMap;
    }
}
