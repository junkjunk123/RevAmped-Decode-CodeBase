package revAmped.components.Hardware;

public class HwCRServo implements HwDevice{

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void update() {

    }

    @Override
    public DeviceType getDeviceType() {
        return null;
    }

    @Override
    public boolean testResponse() {
        return HwDevice.super.testResponse();
    }

    @Override
    public String getType() {
        return HwDevice.super.getType();
    }
}
