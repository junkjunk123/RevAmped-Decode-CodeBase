package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.DriveTrainAndHang;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class HangMethods {
    private final RobotRevampedITD robot;
    private final DelayManager delays;

    public HangMethods(RobotRevampedITD robot, DelayManager delays) {
        this.robot = robot;
        this.delays = delays;
    }

    public void hangStage1() {
        robot.slideHang.setCurrentState(SlideHangServo.SlideHangState.ACTIVE);
    }

    public void resetStage1() {
        robot.ptoServo.setCurrentState(PTOServo.PTOState.INACTIVE);
        robot.slideHang.setCurrentState(SlideHangServo.SlideHangState.INACTIVE);
    }
}
