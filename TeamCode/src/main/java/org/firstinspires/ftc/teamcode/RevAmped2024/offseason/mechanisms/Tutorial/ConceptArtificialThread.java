package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tutorial;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.IntakeClawServo;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import java.util.function.Consumer;
import java.util.function.Supplier;

import revAmped.components.CommandScheduler.ArtificialThread;
import revAmped.components.CommandScheduler.Delay;
import revAmped.components.CommandScheduler.DelayedResult;
import revAmped.components.CommandScheduler.DelayedSupplier;
import revAmped.components.Hardware.HwColorSensor;
import revAmped.components.Hardware.HwServoWithVoltage;
import revAmped.components.Hardware.HwSwitch;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;

@TeleOp
public class ConceptArtificialThread extends RevAmpedOpMode {
    private ArtificialThread<Void> intakeThread;
    private HwSwitch clawSwitch;
    private Intake intake;

    //Imagine there's a limit switch on the claw that tells you when you've successfully grabbed the sample.

    @Override
    public void RevAmpedInit() {
        intake = new Intake(scheduler, hardwareMap);
        clawSwitch = new HwSwitch(hardwareMap, "claw_switch", true,false);
        intakeThread = new ArtificialThread<>() {
            DelayedResult<Boolean> clawPressed;

            @Override
            public void loop() {
                boolean transfer = clawPressed.get();

                if (transfer) {
                    //transfer
                    intakeThread.terminate();
                }
            }

            @Override
            public void init() {
                intake.clawGrab();
            }

            @Override
            public void evaluate() {
                clawPressed = new DelayedSupplier<>(300, clawSwitch::isTouch)
                        .get(this);
            }
        };
    }

    @Override
    public void RevAmpedStart() {
        intakeThread.start();
    }

    @Override
    public void RevAmpedLoop() {

    }

    private void intakeSample() {
        //Here is how this would look without ArtificialThread<T> -- this approach is probably simpler

        DelayedResult<Boolean> clawPressed = new DelayedSupplier<>(300, clawSwitch::isTouch).get();
        clawPressed.andThen(b -> {
            if (b) {
                //transfer
            } else {
                //try again
            }
        });

        Consumer<Runnable> action = s -> {
            //action here
            scheduler.add(new Delay(100, () -> {
                //another action
                s.run(); //this specifies the full action has been completed
            }));
        };
    }

    @Override
    public void update() {
        clawSwitch.update();
        intakeThread.run();
    }

    @Override
    public void RevAmpedStop() {
        intakeThread.terminate();
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
