package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@TeleOp
public class HoodReset extends DecodeRevAmpedOpMode {
    private Robot robot;
    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.allianceColor);
        robot.hoodServo().setPosition(1f);
    }

    @Override
    public void RevAmpedLoop() {

    }

    @Override
    public void update() {
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
