package revAmped.components.Hardware;

import static revAmped.components.Misc.Globals.testing;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HwSwitch implements HwDevice, HwSlide2.SlideRetractionDetector {
    private AnalogInput analog;
    private DigitalChannel digital;
    private final boolean isDigital;
    private final boolean flipped;
    private Boolean isTouch;
    private final String id;

    public HwSwitch(HardwareMap hardwareMap, String switchName, boolean isDigital, boolean flipped) {
        this.id = switchName;
        this.flipped = flipped;
        this.isDigital = isDigital;

        if (!testing) {
            if (isDigital) {
                try {
                    // get a reference to our digitalTouch object.
                    digital = hardwareMap.get(DigitalChannel.class, switchName);

                    // set the digital channel to input.
                    digital.setMode(DigitalChannel.Mode.INPUT);
                } catch (Exception e) {
                    throw new InitializationException(this);
                }
            } else {
                try {
                    analog = hardwareMap.get(AnalogInput.class, switchName);
                } catch (Exception e) {
                    throw new InitializationException(this);
                }
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void update(){
        isTouch = null;
    }

    @Override
    public Boolean getReading() {
        return isTouch();
    }

    @Override
    public DeviceType getDeviceType() {
        return isDigital ? DeviceType.DIGITAL : DeviceType.ANALOG;
    }

    public boolean isTouch () {
        if (isTouch == null) {
            isTouch = touchingUncached();
        }

        return isTouch;
    }

    public boolean touchingUncached() {
        if (!testing) {
            if (isDigital) {
                return (flipped != digital.getState());
            } else {
                return (flipped != (analog.getVoltage() / analog.getMaxVoltage() <= 0.99));
            }
        }

        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return getId();
    }
}
