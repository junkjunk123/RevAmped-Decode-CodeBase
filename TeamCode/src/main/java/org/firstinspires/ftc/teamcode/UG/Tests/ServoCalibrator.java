package org.firstinspires.ftc.teamcode.UG.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.UG.Depo.GateServo;
import org.firstinspires.ftc.teamcode.UG.Intake.IntakeTiltServo;
import org.firstinspires.ftc.teamcode.UG.UGRobot;

import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;

@TeleOp
public class ServoCalibrator extends RevAmpedOpMode {
    private RevAmpedGamepad gp1;
    private RevAmpedGamepad gp2;
    private UGRobot robot;
    private IntakeTiltServo intakeTiltServo;
    private GateServo gateServo;
    private enum CurrentServo{
        TILT,
        GATE,
        NONE;

    }
    private float position;
    private CurrentServo currentServo;
    @Override
    public void RevAmpedInit(){
        robot = new UGRobot(this, Globals.AllianceColor.None);
        gp1 = new RevAmpedGamepad(gamepad1);
        position = 0.5f;
        currentServo = CurrentServo.TILT;
    }
    @Override
    public void RevAmpedLoop(){
        switch (currentServo){
            case GATE -> gateServo.setPosition(position);
            case TILT -> gateServo.setPosition(position);
        }
        if(gp1.left_bumper.isPress()){

        }
        if(gp1.right_bumper.isPress()){

        }
    }
    @Override
    public void update(){
        telemetry.update();
        gp1.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return UGRobot.OpModeType.TEST;
    }
}
