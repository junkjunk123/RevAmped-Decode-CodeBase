package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.vSlideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayManager;

public class AutoMethods {
    private final RobotRevampedITD robot;
    private final DelayManager delays;
    private final IntakeFourbar intakeFourbar;
    private final DepositionArm depositionArm;

    public AutoMethods(RobotRevampedITD robot, DelayManager delays, IntakeFourbar intakeFourbar, DepositionArm depositionArm) {
        this.robot = robot;
        this.delays = delays;
        this.intakeFourbar = intakeFourbar;
        this.depositionArm = depositionArm;
    }

    public void transferPreload(Runnable action) {
        intakeFourbar.transfer();
        depositionArm.transfer();
        robot.vslides.startTransfer();
        robot.setCycleState(RobotRevampedITD.CycleState.TRANSFER);
        robot.hslides.retract(() -> delays.addOptionalDelay(robot.depoClaw.isOpen(), new Delay(() -> robot.vslides.getCurrentState() == vSlideMotor.VSlideState.IN, () -> {
            delays.addOptionalDelay(robot.depoClaw.isOpen(), new Delay(300, () -> {
                robot.depoClaw.looseClose();
                delays.add(new Delay(robot.depoClaw::hasSample, () -> {
                    robot.intake.stop();
                    delays.add(new Delay(150, () -> {action.run(); delays.clear();}));
                }).setCancelation(500, () -> {
                    intakeFourbar.intake();
                    robot.depoClaw.open();
                    delays.add(new Delay(150, () -> {
                        action.run();
                        delays.clear();
                        robot.intakeTrapdoor.spitSample(robot.intake);
                    }));
                }));
            }));
            robot.depoClaw.open();
        })));
    }

    public void retractIntakePreload() {
        robot.hslides.runToPositionAuto(ITDConstants.HSLIDES_MAX/2, false);
        intakeFourbar.transfer();
        depositionArm.transfer();
        robot.setCycleState(RobotRevampedITD.CycleState.NEUTRAL);
    }
}
