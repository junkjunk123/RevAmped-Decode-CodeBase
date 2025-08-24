package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Tutorial;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.ITDRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.GamepadDevices.RevAmpedGamepad;

@Disabled
@TeleOp(name = "GamepadTutorial",group = "Test")
public class GamepadTutorial extends ITDRevAmpedOpMode {
    private RevAmpedGamepad gamepad_1; //Avoid naming this gamepad1 since this creates ambiguity with the SDK gamepad

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1); //Associates this custom gamepad with the physical gamepad1

        //I've never used this before but I made it just in case, you can create optional "custom" buttons
        new RevAmpedGamepad.CustomButtonCreator(() -> gamepad_1.left_trigger)
                .greaterThan(0.5f)
                .lessThan(0.1f)
                .build(gamepad_1);
    }

    @Override
    public void RevAmpedLoop() {
        if (gamepad_1.a.isPress()) { //Rising edge trigger built into isPress(), this will be the main button press condition
            //Action here
        }

        if (gamepad_1.b.isPressFalling()) { //Falling edge trigger here -- avoid using instead of isPress() unless specific reason
            //Action here
        }

        if (gamepad_1.x.isTrue()) { //Works with holding down and the action will execute a ton of times
            //Action here
        }

        if (gamepad_1.y.isToggled()) { //Returns true is toggled to true, starts false and with every press the value switches
            //Action here
        }

        if (gamepad_1.customButtons.get(0).isPress()) { //Our custom button
            //Action here
        }
    }

    @Override
    public void update() {
        gamepad_1.update(); //Make sure to call this here. This is VERY important-- the RevAmpedGamepad will not work without it.
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
