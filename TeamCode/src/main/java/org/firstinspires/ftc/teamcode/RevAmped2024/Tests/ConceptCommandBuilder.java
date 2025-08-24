package org.firstinspires.ftc.teamcode.RevAmped2024.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDConstants;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedGamepad;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils.ITDRevAmpedOpMode;

import revAmped.components.CommandScheduler.Scheduler;

@TeleOp(name = "ConceptCommandBuilder", group = "Game")
public class ConceptCommandBuilder extends ITDRevAmpedOpMode {
    private RobotRevampedITD robot;
    private ITDRevAmpedGamepad gamepad_1;
    private DepositionArm depositionArm;

    @Override
    public void RevAmpedInit() {
        robot = new RobotRevampedITD(this);
        gamepad_1 = new ITDRevAmpedGamepad(gamepad1);
        depositionArm = new DepositionArm(robot, scheduler.delays);
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) {
            //Basket Deposition
            Scheduler.commandBuilder()
                    .addNode(() -> {
                        robot.intake.setPower(0.5f);
                        robot.vslides.runToPosition(ITDConstants.VSLIDE_BASKET_AUTO - 50);
                        robot.setCycleState(RobotRevampedITD.CycleState.HIGH_BASKET_DEPO);
                    })
                    .waitUntil(() -> robot.vslides.getCurrentPosition() > ITDConstants.VSLIDE_MIN)
                    .addNode(depositionArm::teleopInitialBasketDepo)
                    .waitMillis(600)
                    .addNode(robot.intake::stop)
                    .build()
                    .run();
        }
    }

    @Override
    public void update() {
        robot.update();
        gamepad_1.update();
    }

    @Override
    public RobotRevampedITD.OpModeType getOpModeType() {
        return RobotRevampedITD.OpModeType.TELEOP_TEST;
    }
}
