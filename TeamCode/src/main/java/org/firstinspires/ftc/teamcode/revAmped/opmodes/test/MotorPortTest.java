package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;

@Disabled
@TeleOp
public class MotorPortTest extends DecodeRevAmpedOpMode {
    RevAmpedGamepad gp1;
    private DcMotor port0Motor;
    private DcMotor port1Motor;
    private DcMotor port2Motor;
    private DcMotor port3Motor;


    @Override
    public void RevAmpedInit() {
        gp1 = new RevAmpedGamepad(gamepad1);
        port0Motor = hardwareMap.get(DcMotor.class,"port0");
        port1Motor = hardwareMap.get(DcMotor.class,"port1");
        port2Motor = hardwareMap.get(DcMotor.class,"port2");
        port3Motor = hardwareMap.get(DcMotor.class,"port3");
        /*
        Ports:
        port0 - FL
        port1 - BL
        port2 - BR
        port3 - FR
        */
    }

    @Override
    public void RevAmpedLoop() {
        if(gp1.y.isTrue()){port0Motor.setPower(0.5);} else {port0Motor.setPower(0);}
        if(gp1.b.isTrue()){port1Motor.setPower(0.5);} else {port1Motor.setPower(0);}
        if(gp1.a.isTrue()){port2Motor.setPower(0.5);} else {port2Motor.setPower(0);}
        if(gp1.x.isTrue()){port3Motor.setPower(0.5);} else {port3Motor.setPower(0);}
    }

    @Override
    public void update() {
        gp1.update();
    }

    @Override
    public Robot.OpModeType getOpModeType(){
        return Robot.OpModeType.TEST;
    }
}
