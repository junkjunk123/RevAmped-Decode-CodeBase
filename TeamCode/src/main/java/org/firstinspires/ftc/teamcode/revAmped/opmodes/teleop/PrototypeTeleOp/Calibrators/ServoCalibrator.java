package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Calibrators;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwServo2;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.OctocanumBack;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.OctocanumFront;
/*
Eric Zhao
10/20/2025
Servo Calibrator for prototyping teleop
Not used
 */

@TeleOp
public class ServoCalibrator extends DecodeRevAmpedOpMode {
    //Variable declarations
    private float position;
    private RevAmpedGamepad gamepad_1;
    private static OctocanumFront octocanumFront;
    private static OctocanumBack octocanumBack;
    //enum of servos
    private enum Servos{
        OCTO_FRONT,
        OCTO_BACK;
    }
    Servos servo = Servos.OCTO_FRONT;
    float increment = 5;
    //Init
    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        octocanumFront = new OctocanumFront(hardwareMap,false);
        octocanumBack = new OctocanumBack(hardwareMap,false);
    }
    //Loop
    @Override
    public void RevAmpedLoop() {
        if(gamepad_1.a.isPress()){
            position+=increment;
        }
        if(gamepad_1.b.isPress()){
            position-=increment;
        }
        if(gamepad_1.dpad_down.isPress()){
            increment = 1;
        }
        if(gamepad_1.dpad_up.isPress()){
            increment = 5;
        }
        //Cycling through enum
        if(gamepad_1.y.isPress()){
            if (servo.ordinal()+1 < 2) {
                servo = Servos.values()[servo.ordinal() + 1];
            } else{
                servo = Servos.values()[0];
            }
        }
        if(gamepad_1.x.isPress()) {
            if (servo.ordinal() - 1 > 0) {
                servo = Servos.values()[servo.ordinal() - 1];
            } else {
                servo = Servos.values()[2];
            }
        }
        if(servo == Servos.OCTO_FRONT){
            octocanumFront.setPosition(position/255f);
        }
        if(servo == Servos.OCTO_BACK){
            octocanumBack.setPosition(position/255f);
        }
        telemetry.addData("Position",position);
        telemetry.addData("Servo", servo);
        telemetry.addData("Tick amount", increment);
    }

    @Override
    public void update() {
        gamepad_1.update();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }

}
