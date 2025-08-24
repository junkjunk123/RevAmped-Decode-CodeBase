package revAmped.components.GamepadDevices;

import com.qualcomm.robotcore.hardware.Gamepad;

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

    public RevAmpedGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
        gamepad.reset();
        b = new Button2(gamepad.b);
        a = new Button2(gamepad.a);
        x = new Button2(gamepad.x);
        y = new Button2(gamepad.y);
        left_bumper = new Button2(gamepad.left_bumper);
        right_bumper = new Button2(gamepad.right_bumper);
        dpad_down = new Button2(gamepad.dpad_down);
        dpad_left = new Button2(gamepad.dpad_left);
        dpad_right = new Button2(gamepad.dpad_right);
        dpad_up = new Button2(gamepad.dpad_up);
        start = new Button2(gamepad.start);
        back = new Button2(gamepad.back);
        right_stick_button = new Button2(gamepad.right_stick_button);
        left_stick_button = new Button2(gamepad.left_stick_button);
        touchpad = new Button2(gamepad.touchpad);
    }

    public static float smoothControls (float value) {
        return (float) (0.5f*Math.tan(1.12f*value));
    }

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
    }

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

    public void setLED(int r, int g, int b, int ms) {
        gamepad.setLedColor(r,g,b,ms);
    }

    public void setLED(Gamepad.LedEffect effect) {
        gamepad.runLedEffect(effect);
    }
}
