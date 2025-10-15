package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@TeleOp
public class ServoCalibrator extends DecodeRevAmpedOpMode {
    private float position;
    private RevAmpedGamepad gamepad_1;
    private TableServo tableServo;
    private PopperServo popperServo;
    private HoodServo hoodServo;
    private enum Servos{
        TABLE,
        POPPER,
        HOOD,
        NONE;
    }
    Servos servo = Servos.TABLE;
    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        tableServo = new TableServo(hardwareMap,true);
        popperServo = new PopperServo(hardwareMap, true);
        hoodServo = new HoodServo(hardwareMap,true);
    }

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
