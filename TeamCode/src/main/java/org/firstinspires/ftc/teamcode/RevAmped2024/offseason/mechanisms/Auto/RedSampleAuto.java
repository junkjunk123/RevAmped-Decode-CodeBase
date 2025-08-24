package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;
import static org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot.OpModeType.AUTO;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Depo.Depo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.VslideMotor;

import revAmped.components.Pedro.FollowerManager;
import revAmped.components.CommandScheduler.Command;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayChain;
import revAmped.components.Misc.Globals;

@Autonomous(group = "game")
public class RedSampleAuto extends ITDRevAmpedOpMode {
    private Robot robot;
    private FollowerManager dt;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Red);
        dt = new FollowerManager(hardwareMap, new SampleAutoPaths());
        build();
    }

    @Override
    public void RevAmpedStart() {
        scheduler.run("auto");
    }

    @Override
    public void RevAmpedLoop() {}

    public void build() {
        new DelayChain("auto",
                //Drop the first sample and intake the second
                new DelayChain(
                        new Command(() -> {
                            dt.start();
                            robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                            robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                            robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                        }),
                        new Delay(
                                () -> !dt.follower.isBusy() && robot.vslides().closeEnough(15),
                                () -> {
                                    robot.depoClaw().close();
                                    robot.intake.clawGrab();
                                }
                        ).setMaxTimeToRun(3500)
                ),

                //Drop the second sample and intake the third
                new DelayChain(
                        transfer(),
                        new Delay (200, () -> {
                            robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                            robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                            robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                        }),
                        new Delay(
                                () -> robot.vslides().closeEnough(15),
                                () -> {
                                    robot.depoClaw().close();
                                    robot.intake.clawGrab();
                                }
                        )
                ),

                //Drop the third sample and intake the fourth
                new DelayChain(
                        new Command(dt::followNextChain),
                        transfer(),
                        new Delay (200, () -> {
                            robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                            robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                            robot.hslides().setCurrentState(HslideMotor.HslideMotorState.OUT);
                            robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_LOWER);
                        }),
                        new Delay(
                                () -> !dt.follower.isBusy() && robot.vslides().closeEnough(15),
                                () -> {
                                    robot.depoClaw().close();
                                    robot.intake.clawGrab();
                                }
                        ).setMaxTimeToRun(2000)
                ),

                //Drop the fourth sample
                new DelayChain(
                        transfer(),
                        new Delay (200, () -> {
                            robot.depo.setCurrentState(Depo.DepoState.BASKET_DEPO);
                            robot.vslides().setCurrentState(VslideMotor.VslideMotorState.BASKET);
                        }),
                        new Delay(
                                () -> robot.vslides().closeEnough(15),
                                () -> {
                                    robot.depoClaw().close();
                                    robot.intake.clawGrab();
                                }
                        ).setMaxTimeToRun(2000)
                )
        );
    }

    public DelayChain transfer() {
        return new DelayChain(
                new Command(() -> {
                    robot.depo.setCurrentState(Depo.DepoState.TRANSFER);
                    robot.hslides().retract();
                }),
                new Delay(
                        () -> robot.hslides().getCurrentPosition() < HslideMotor.HALF_EXTENSION,
                        () -> robot.intake.setCurrentState(Intake.IntakeState.TRANSFER)
                ),
                new Delay(
                        () -> robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.IN,
                        () -> robot.depoClaw().open()
                ),
                new Delay(
                        200,
                        () -> robot.intakeClaw().open()
                ),
                new Delay(
                        400,
                        () -> robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER)
                )
        );
    }

    @Override
    public void update() {
        dt.update();
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return AUTO;
    }
}
