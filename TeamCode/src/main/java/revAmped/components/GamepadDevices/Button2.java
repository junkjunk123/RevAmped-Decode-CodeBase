package revAmped.components.GamepadDevices;

import java.util.function.BooleanSupplier;

public class Button2 {
    private volatile boolean button;
    private volatile boolean lastButton = false;
    private boolean isToggled = false;

    public Button2(boolean button) {
        this.button = button;
    }

    public void update(boolean currentButton) {
        lastButton = button;
        button = currentButton;
    }

    /**
     * @return raw button value without filters
     */
    public boolean isTrue() {
        return button;
    }

    /**
     * @return Button press with rising edge trigger
     */
    public boolean isPress() {
        boolean isPress = button && !lastButton;
        if (isPress) {
            isToggled = !isToggled;
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
        }
        return isPress;
    }

    /**
     * @return if its toggled on, starts toggled off
     */
    public boolean isToggled() {
        isPress();
        return isToggled;
    }

    /**
     * @return returns if its just released
     */
    public boolean isReleased() {
        return !button && lastButton;
    }
}
