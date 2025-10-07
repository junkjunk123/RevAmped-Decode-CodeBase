package org.firstinspires.ftc.teamcode.revAmped.components.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A wrapper for the SDK Gamepad class that contains all the buttons.
 * The class contains additional functionality like built-in logic as well as the ability to create custom buttons.
 */

public class RevAmpedGamepad {
    //The name was created to avoid ambiguity with SDK's Gamepad class
    public final Button2 b;
    public final Button2 a;
    public final Button2 x;
    public final Button2 y;
    public final Button2 left_bumper;
    public final Button2 right_bumper;
    public final Button2 start;
    public final Button2 back;
    public final Button2 dpad_left;
    public final Button2 dpad_up;
    public final Button2 dpad_down;
    public final Button2 dpad_right;
    public final Button2 left_stick_button;
    public final Button2 right_stick_button;
    public final Button2 touchpad;

    public volatile float left_stick_x = 0f;

    /**
     * left analog stick vertical axis
     */
    public volatile float left_stick_y = 0f;

    /**
     * right analog stick horizontal axis
     */
    public volatile float right_stick_x = 0f;

    /**
     * right analog stick vertical axis
     */
    public volatile float right_stick_y = 0f;
    /**
     * dpad up
    /**
     * left trigger
     */
    public volatile float left_trigger = 0f;

    /**
     * right trigger
     */
    public volatile float right_trigger = 0f;

    private final Gamepad gamepad;
    public List<CustomButton> customButtons = new ArrayList<>();

    private Button2 lastButton = null;

    /**
     * Constructor to initialize the RevAmpedGamepad with a Gamepad instance.
     *
     * @param gamepad the Gamepad instance to wrap
     */
    public RevAmpedGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
        gamepad.reset();
        b = new Button2(this, gamepad.b, "b");
        a = new Button2(this, gamepad.a, "a");
        x = new Button2(this, gamepad.x, "x");
        y = new Button2(this, gamepad.y, "y");
        left_bumper = new Button2(this, gamepad.left_bumper, "left_bumper");
        right_bumper = new Button2(this, gamepad.right_bumper, "right_bumper");
        dpad_down = new Button2(this, gamepad.dpad_down, "dpad_down");
        dpad_left = new Button2(this, gamepad.dpad_left, "dpad_left");
        dpad_right = new Button2(this, gamepad.dpad_right, "dpad_right");
        dpad_up = new Button2(this, gamepad.dpad_up, "dpad_up");
        start = new Button2(this, gamepad.start, "start");
        back = new Button2(this, gamepad.back, "back");
        right_stick_button = new Button2(this, gamepad.right_stick_button, "right_stick_button");
        left_stick_button = new Button2(this, gamepad.left_stick_button, "left_stick_button");
        touchpad = new Button2(this, gamepad.touchpad, "touchpad");
    }

    /**
     * Smooth gamepad joystick controls based on natural human pressing input
     * @param value the joystick input
     * @return the smoothed value
     */
    public static float smoothControls (float value) {
        return (float) (0.5f*Math.tan(1.12f*value));
    }

    /**
     * Checks if the button is pressed.
     *
     * @param button the button to check
     * @return true if the button is pressed, false otherwise
     */
    public boolean isPressed(Button2 button) {
        return button.isPress();
    }

    public float getSmooth_left_stick_x(){
        return smoothControls(gamepad.left_stick_x);
    }

    public float getSmooth_right_stick_x(){
        return smoothControls(gamepad.right_stick_x);
    }

    public float getSmooth_left_stick_y(){
        return smoothControls(gamepad.left_stick_y);
    }

    public float getSmooth_right_stick_y(){
        return smoothControls(gamepad.right_stick_y);
    }

    /**
     * Call this once per loop or else the gamepad won't update to your button presses.
     */
    public void update() {
        left_stick_x = gamepad.left_stick_x;
        right_stick_x = gamepad.right_stick_x;
        left_stick_y = gamepad.left_stick_y;
        right_stick_y = gamepad.right_stick_y;
        left_trigger = gamepad.left_trigger;
        right_trigger = gamepad.right_trigger;
        b.update(gamepad.b);
        a.update(gamepad.a);
        x.update(gamepad.x);
        y.update(gamepad.y);
        right_bumper.update(gamepad.right_bumper);
        left_bumper.update(gamepad.left_bumper);
        right_stick_button.update(gamepad.right_stick_button);
        left_stick_button.update(gamepad.left_stick_button);
        dpad_up.update(gamepad.dpad_up);
        dpad_right.update(gamepad.dpad_right);
        dpad_left.update(gamepad.dpad_left);
        dpad_down.update(gamepad.dpad_down);
        start.update(gamepad.start);
        back.update(gamepad.back);
        touchpad.update(gamepad.touchpad);

        for (CustomButton button : customButtons) {
            button.update();
        }
    }

    //Rumbling is for the gamepad to create a haptic response

    /**
     * Rumbles the gamepad for a specified duration in milliseconds.
     *
     * @param durationms the duration to rumble in milliseconds
     */
    public void rumble(int durationms) {
        gamepad.rumble(durationms);
    }

    public void rumbleContinuous() {
        gamepad.rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
    }

    public void rumble(double rumble1, double rumble2, int ms) {
        gamepad.rumble(rumble1, rumble2, ms);
    }

    public void rumble(Gamepad.RumbleEffect effect) {
        gamepad.runRumbleEffect(effect);
    }

    public void rumbleBlips(int blips) {
        gamepad.rumbleBlips(blips);
    }

    public void stopRumble() {
        gamepad.stopRumble();
    }

    //This makes the PS5 controller change color

    public void setLED(int r, int g, int b, int ms) {
        gamepad.setLedColor(r,g,b,ms);
    }

    public void setLED(Gamepad.LedEffect effect) {
        gamepad.runLedEffect(effect);
    }

    /**
     * A custom button class that allows for more complex button logic.
     */
    public static class CustomButton extends Button2 {
        private BooleanSupplier button;

        /**
         * Constructor to create a custom button with a BooleanSupplier.
         *
         * @param button the BooleanSupplier that determines the button state
         */
        public CustomButton(RevAmpedGamepad gamepad, BooleanSupplier button, String id) {
            super(gamepad, button.getAsBoolean(), id);
            this.button = button;
        }

        /**
         * Constructor to create a custom button with a BooleanSupplier.
         *
         * @param button the BooleanSupplier that determines the button state
         */
        public CustomButton(BooleanSupplier button) {
            super(button.getAsBoolean());
            this.button = button;
        }

        /**
         * Updates the button state based on the BooleanSupplier.
         */
        public void update() {
            super.update(button.getAsBoolean());
        }
    }

    /**
     * A builder class to create custom buttons with specific conditions.
     */
    public static class CustomButtonCreator {
        private BooleanSupplier button = () -> true;
        private Supplier<Float> buttonVal;
        private String id;

        /**
         * Constructor to initialize the CustomButtonCreator with a BooleanSupplier.
         *
         * @param button the BooleanSupplier that determines the initial button state
         */
        public CustomButtonCreator(String id, Supplier<Float> button) {
            buttonVal = button;
            this.id = id;
        }

        /**
         * Adds a condition to the custom button that checks if the float value, returned by a trigger or stick, is greater than the threshold.
         * @param threshold the threshold value to compare against
         * @return the CustomButtonCreator instance for method chaining
         */
        public CustomButtonCreator greaterThan(float threshold) {
            button = () -> button.getAsBoolean() && buttonVal.get() > threshold;
            return this;
        }

        /**
         * Adds a condition to the custom button that checks if the button is released.
         * @param threshold the threshold value to compare against
         * @return the CustomButtonCreator instance for method chaining
         */
        public CustomButtonCreator lessThan(float threshold) {
            button = () -> button.getAsBoolean() && buttonVal.get() < threshold;
            return this;
        }

        /**
         * Builds the custom button and adds it to the specified RevAmpedGamepad.
         * @param gamepad the RevAmpedGamepad to which the custom button will be added
         */
        public void build(RevAmpedGamepad gamepad) {
            gamepad.addCustomButton(new CustomButton(gamepad, button, id));
        }
    }

    /**
     * Adds a custom button to the gamepad.
     *
     * @param button the custom button to add
     */
    public void addCustomButton(CustomButton button) {
        customButtons.add(button);
    }

    public void setLastButton(Button2 lastButton) {
        this.lastButton = lastButton;
    }
}
