package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tutorial;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.HslideMotor;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Intake.Intake;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.CommandScheduler.Delay;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;

@Disabled
@TeleOp(name = "SchedulerTutorial",group = "Test")
public class SchedulerTutorial extends ITDRevAmpedOpMode {
    private Robot robot;
    private RevAmpedGamepad gamepad_1;

    @Override
    public void RevAmpedInit() {
        robot = new Robot(this, Globals.AllianceColor.None);
        gamepad_1 = new RevAmpedGamepad(gamepad1);
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) {
            //Moves the intake to the intake position 200ms after the method call
            scheduler.add(new Delay(200, () -> robot.intake.setCurrentState(Intake.IntakeState.INTAKE)));
        }

        if (gamepad_1.b.isPress()) {
            //Retracts the hslides and then waits until they are all the way in until the intake transfers. This delay is based on the hslide limit switch.
            //If the hslide limit switch hasn't been hit in 2000ms, the intake instead drops to neutral.
            robot.hslides().retract();
            scheduler.add(new Delay(() -> robot.hslides().getCurrentState() == HslideMotor.HslideMotorState.IN,
                    () -> robot.intake.setCurrentState(Intake.IntakeState.TRANSFER))
                    .setCancellation(2000, () -> robot.intake.setCurrentState(Intake.IntakeState.NEUTRAL_HIGHER)));
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
        robot.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
