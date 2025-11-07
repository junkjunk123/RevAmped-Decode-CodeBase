package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

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

@TeleOp
public class TurntableTest extends DecodeRevAmpedOpMode {
    private TableServo tableServo;
    //private IntakeMotor intakeMotor;
    private Flywheel flywheel;
    private DcMotorEx intake;

    @Override
    public void RevAmpedInit() {
        //intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.STOP, new HwVoltageSensor(hardwareMap));
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
        //intakeMotor.setVoltageCompensation();
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intake));
    }

    @Override
    public void RevAmpedLoop() {
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
        telemetry.addData("flywheelPos", flywheel.motor2.motor.getCurrentPosition());
        telemetry.addData("intake", intake.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
