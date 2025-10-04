package revAmped.components.GamepadDevices;

import com.pedropathing.math.AbstractBijectiveMap;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;

public class GamepadMapper extends RevAmpedGamepad {
    private final HashMap<Button2, Button2> map = new HashMap<>();

    public GamepadMapper(Gamepad gamepad) {
        super(gamepad);
    }

    public GamepadMapper add(Button2 from, Button2 to) {
        map.put(from, to);
        return this;
    }

    public void update() {
        for (Button2 from : map.keySet()) {
            from.update(from.isTrue());
        }
    }
}
