package revAmped.components.GamepadDevices;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Button2 class is a simple button handler that can be used to track the state of a button.
 * It provides methods to check if the button is pressed, released, or toggled.
 * The button state is updated with the `update` method.
 */
public class Button2 {
    private boolean button;
    private boolean lastButton = false;
    private boolean isToggled = false;
    private RevAmpedGamepad gamepad;
    private String id;

    /**
     * Constructor to initialize the button state.
     *
     * @param button initial state of the button
     */
    public Button2(boolean button) {
        this.button = button;
    }

    /**
     * Constructor to initialize the button state with a gamepad.
     *
     * @param gamepad the gamepad to read the button state from
     * @param button the button on the gamepad to track
     */
    public Button2(RevAmpedGamepad gamepad, boolean button, String id) {
        this.gamepad = gamepad;
        this.button = button;
        this.id = id;
    }

    /**
     * Updates the button state.
     *
     * @param currentButton the current state of the button
     */
    public void update(boolean currentButton) {
        lastButton = button;
        button = currentButton;
    }

    /**
     * @return raw button value without filters
     */
    public boolean isTrue() {
        if (gamepad != null && button) {
            gamepad.setLastButton(this);
        }
        return button;
    }

    /**
     * @return Button press with rising edge trigger
     */
    public boolean isPress() {
        boolean isPress = button && !lastButton;
        if (isPress) {
            isToggled = !isToggled;
            if (gamepad != null) {
                gamepad.setLastButton(this);
            }
        }
        return isPress;
    }

    /**
     * @return Button press with falling edge trigger
     */
    public boolean isPressFalling() {
        boolean isPress = isReleased();
        if (isPress) {
            isToggled = !isToggled;
            if (gamepad != null) {
                gamepad.setLastButton(this);
            }
        }
        return isPress;
    }

    /**
     * @return if its toggled on, starts toggled off
     */
    public boolean isToggled() {
        boolean isPress = button && !lastButton;
        if (isPress) isToggled = !isToggled;
        if (isToggled) {
            if (gamepad != null) {
                gamepad.setLastButton(this);
            }
        }
        return isToggled;
    }

    /**
     * @return returns if its just released
     */
    private boolean isReleased() {
        boolean released = !button && lastButton;
        if (released && gamepad != null) {
            gamepad.setLastButton(this);
        }
        return released;
    }

    @NonNull
    @Override
    public String toString() {
        return id;
    }
}
