package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.CommandScheduler.Command;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayChain;
import revAmped.components.Misc.Globals;

@TeleOp
public class CommandTest extends ITDRevAmpedOpMode {
    private Robot robot;
    private DelayChain chain;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Red);

        chain = new DelayChain("test",
                new DelayChain(
                        new Command(() -> {
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                            robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                        }),
                        new Delay(
                                () -> robot.hslides().closeEnough(10),
                                () -> robot.intake.clawGrab()
                        ).setMaxTimeToRun(1000)
                ),
                new DelayChain(
                        new Delay(3000, () -> {
                            robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                            robot.hslides().retract();
                        }),
                        new Delay(
                                () -> robot.hslides().getCurrentPosition() < -100,
                                () -> robot.intake.setCurrentState(Intake.IntakeState.TRANSFER)
                        ).setMaxTimeToRun(2000),
                        new Delay(
                                () -> robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.IN,
                                robot.depoClaw()::close
                        ),
                        new Delay(200, robot.intakeClaw()::open),
                        new Delay(600, () -> robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER))
                )
        );
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("chainIndex",chain.getCurrentIndex());
        telemetry.addData("complete",chain.currentIndexComplete());
        telemetry.update();
    }

    @Override
    public void RevAmpedStart() {
        scheduler.run("test");
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
