package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.IntakeMotor;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;

@TeleOp
public class TurntableTest extends DecodeRevAmpedOpMode {
    private TableServo tableServo;
    private IntakeMotor intakeMotor;

    @Override
    public void RevAmpedInit() {
        intakeMotor = new IntakeMotor(hardwareMap, Types.IntakeState.INTAKE);
        tableServo = new TableServo(hardwareMap, Types.TableState.BALL1, HwCRServo.Encoder.external(intakeMotor));
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
        intakeMotor.update();
        tableServo.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
