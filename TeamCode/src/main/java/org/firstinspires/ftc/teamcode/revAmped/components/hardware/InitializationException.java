package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

public class InitializationException extends IllegalArgumentException {
    public InitializationException(HwDevice device) {
        super("Failed to initialize " + device.getDeviceType() + " (" + device.getDeviceType() + ")");
    }
}


