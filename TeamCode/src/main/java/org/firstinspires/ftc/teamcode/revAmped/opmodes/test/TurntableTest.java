package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwVoltageSensor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Config
@TeleOp
public class TurntableTest extends DecodeRevAmpedOpMode {
    private TableServo tableServo;
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
        tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intake));
    }

    @Override
    public void RevAmpedLoop() {
        tableServo.updatePIDFCoefficients(new PIDFCoefficients(P, I, D, F));
        tableServo.setMult(mult);

        if (gamepad1.xWasPressed()) {
            tableServo.setAbsoluteRotationTarget(target + tableServo.getCurrentPosition());
        }

        if (gamepad1.aWasPressed()) {
            tableServo.setCurrentState(Types.TableState.BALL1);
        }

        if (gamepad1.bWasPressed()) {
            tableServo.setCurrentState(Types.TableState.BALL2);
        }

        if (gamepad1.xWasPressed()) {
            tableServo.setCurrentState(Types.TableState.BALL3);
        }
    }

    @Override
    public void update() {
        //intakeMotor.update();
        tableServo.update();
        telemetry.addData("turntablePos", tableServo.getCurrentPosition());
        //telemetry.addData("intakePos", intakeMotor.motor.getCurrentPosition());
        telemetry.addData("targetPos", tableServo.getAbsoluteRotationTarget());
        telemetry.addData("error", tableServo.getLastError());

        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
