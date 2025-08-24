package revAmped.components.Hardware;

public class InitializationException extends IllegalArgumentException {
    public InitializationException(HwDevice device) {
        super("Failed to initialize " + device.getType() + " (" + device.getType() + ")");
    }
}


