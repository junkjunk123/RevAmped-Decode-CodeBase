package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Robot;
import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.Auto.Paths.LM0AutoPaths;
import org.firstinspires.ftc.teamcode.RevAmped.OpModes.DecodeRevAmpedOpMode;

import revAmped.components.CommandScheduler.Command;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayChain;
import revAmped.components.CommandScheduler.OptionalDelay;
import revAmped.components.Misc.Globals;
import revAmped.components.Pedro.FollowerManager;

@Autonomous
public class RedLM0Auto extends DecodeRevAmpedOpMode {
    private Robot robot;
    private FollowerManager dt;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.Red);
        dt = new FollowerManager(hardwareMap, new LM0AutoPaths());
        build();
    }

    @Override
    public void RevAmpedStart() {
        scheduler.run("ian loves jasmine");
    }

    @Override
    public void RevAmpedLoop() {}

    @Override
    public void update() {
        dt.update();
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.AUTO;
    }

    private void build() {
        new DelayChain("ian loves jasmine",
                //Shoot Preloads
                new DelayChain(
                        popAll(0),
                        new Delay(
                                Popper.POP_TIME_MILLIS,
                                robot.shooter::stopAll
                        )
                ),
                //GO_TO_FIRST_SET
                new DelayChain(
                        new Command(
                                dt::start
                        ),
                        new Delay(
                                () -> dt.follower.getCurrentTValue() > 0.5,
                                () -> robot.turnTable.intake()
                        ).setMaxTimeToRun(1000),
                        new Delay(
                                () -> robot.turnTable.full(),
                                () -> {}
                        )
                )
        );
    }

    private DelayChain popAll(int set) {
        if (!robot.turnTable.canPop()) return new DelayChain();
        if (set == 0) {
            return new DelayChain(
                    new Command(
                            () -> robot.shooter.shootAutoSet(set)
                    ),
                    new Delay(
                            300,
                            () -> robot.turnTable.pop()
                    ),
                    new Delay(
                            Popper.POP_TIME_MILLIS,
                            () -> robot.turnTable.pop()
                    ),
                    new Delay(
                            Popper.POP_TIME_MILLIS,
                            () -> robot.turnTable.pop()
                    )
            );
        }
        return new DelayChain(
                new Command(
                        () -> robot.shooter.shootAutoSet(set)
                ),
                new OptionalDelay (
                        robot.turnTable.canPop(),
                        300,
                        () -> {
                            if (robot.turnTable.canPop()) robot.turnTable.pop();
                        }
                ),
                new OptionalDelay (
                        robot.turnTable.canPop(),
                        Popper.POP_TIME_MILLIS,
                        () -> {
                            if (robot.turnTable.canPop()) robot.turnTable.pop();
                        }
                ),
                new OptionalDelay (
                        robot.turnTable.canPop(),
                        Popper.POP_TIME_MILLIS,
                        () -> {
                            if (robot.turnTable.canPop()) robot.turnTable.pop();
                        }
                )
        );
    }
}
