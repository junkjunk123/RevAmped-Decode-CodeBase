package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServoV2;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Config
@TeleOp
public class TurntableTest extends DecodeRevAmpedOpMode {
    private TableServoV2 tableServo;
    //private IntakeMotor intakeMotor;
    private DcMotorEx intake;

    public static double P = 0.00028;
    public static double I = 0;
    public static double D = 0.00001;
    public static double F = 0.02;

    public static int target = 0;
    public static float mult = 1f;

    @Override
    public void RevAmpedInit() {
        //intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
        //intakeMotor.setVoltageCompensation();
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        tableServo = new TableServoV2(hardwareMap, true, Types.TableState2.BALL2, Encoder.external(intake).reverse());
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad1.dpadDownWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL1_END);
        }

        if (gamepad1.dpadRightWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL2_END);
        }

        if (gamepad1.dpadUpWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL3_END);
        }

        if (gamepad1.aWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL1);
        }

        if (gamepad1.bWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL2);
        }

        if (gamepad1.xWasPressed()) {
            tableServo.setCurrentState(Types.TableState2.BALL3);
        }
    }

    @Override
    public void update() {
        //intakeMotor.update();
        tableServo.update();
        telemetry.addData("currentPos", (int) (tableServo.getPosition() * 255f));
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
