package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public class SplitTableServo extends TableServo {
    private boolean usingBoth = false;
    private final HwCRServo secondaryServo;

    public SplitTableServo(HardwareMap hardwareMap, Types.TableState state, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, state, encoder);
        secondaryServo = new HwCRServo(hardwareMap, "table_two", TableServo.direction);
    }

    public boolean isUsingBoth() {
        return usingBoth;
    }

    public void setUsingBoth(boolean usingBoth) {
        this.usingBoth = usingBoth;
    }

    @Override
    public void setPower(float power) {
        super.setPower(power);
        if (usingBoth)
            secondaryServo.setPower(power);
    }
}
