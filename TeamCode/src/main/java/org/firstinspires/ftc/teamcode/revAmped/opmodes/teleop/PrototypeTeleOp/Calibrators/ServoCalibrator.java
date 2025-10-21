package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Calibrators;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.HoodServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.PopperServo;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms.TableServo;
/*
Eric Zhao
10/20/2025
Servo Calibrator for prototyping teleop
Not used
 */

@TeleOp
@Disabled
public class ServoCalibrator extends DecodeRevAmpedOpMode {
    //Variable declarations
    private float position;
    private RevAmpedGamepad gamepad_1;
    private TableServo tableServo;
    private PopperServo popperServo;
    private HoodServo hoodServo;
    //enum of servos
    private enum Servos{
        TABLE,
        POPPER,
        HOOD,
        NONE;
    }
    Servos servo = Servos.TABLE;
    //Init
    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        tableServo = new TableServo(hardwareMap,true);
        popperServo = new PopperServo(hardwareMap, true);
        hoodServo = new HoodServo(hardwareMap,true);
    }
    //Loop
    @Override
    public void RevAmpedLoop() {
        if(servo == Servos.TABLE){
            tableServo.setPosition(position);
        }
        if(servo == Servos.POPPER){
            popperServo.setPosition(position);
        }
        if(servo == Servos.HOOD){
            hoodServo.setPosition(position);
        }
        if(gamepad_1.a.isPress()){
            position+=5/255f;
        }
        if(gamepad_1.b.isPress()){
            position-=5/255f;
        }
        //Cycling through enum
        if(gamepad_1.y.isPress()){
            if (servo.ordinal()+1 < 3) {
                servo = Servos.values()[servo.ordinal() + 1];
            } else{
                servo = Servos.values()[0];
            }
        }
        if(gamepad_1.x.isPress()) {
            if (servo.ordinal() - 1 > 0) {
                servo = Servos.values()[servo.ordinal() - 1];
            } else {
                servo = Servos.values()[3];
            }
        }
        telemetry.addData("Position",position);
        telemetry.addData("Servo", servo);
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
