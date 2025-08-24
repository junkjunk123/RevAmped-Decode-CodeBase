package revAmped.components.Hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HwSwitch implements HwDevice {
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
        if (isDigital) {
            // get a reference to our digitalTouch object.
            digital = hardwareMap.get(DigitalChannel.class, switchName);

            // set the digital channel to input.
            digital.setMode(DigitalChannel.Mode.INPUT);
        }
        else {
            analog = hardwareMap.get(AnalogInput.class, switchName);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void update(){
        isTouch = null;
    }

    public boolean isTouch () {
        if (isTouch == null) {
            if (isDigital) {
                isTouch = (flipped != digital.getState());
            } else {
                isTouch = (flipped != (analog.getVoltage() / analog.getMaxVoltage() <= 0.99));
            }
        }

        return isTouch;
    }

    public boolean touchingUncached() {
        if (isDigital) {
            return (flipped != digital.getState());
        } else {
            return (flipped != (analog.getVoltage() / analog.getMaxVoltage() <= 0.99));
        }
    }
}
