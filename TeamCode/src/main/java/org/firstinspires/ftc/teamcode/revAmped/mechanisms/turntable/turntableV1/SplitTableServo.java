package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Encoder;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public class SplitTableServo extends TableServo {
    public enum SplitState {
        USING_BOTH,
        USING_ONE,
        RESETTING
    }

    private final HwCRServo secondaryServo;
    private SplitState splitState = SplitState.USING_ONE;

    public SplitTableServo(HardwareMap hardwareMap, Types.TableState state, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, state, encoder);
        secondaryServo = new HwCRServo(hardwareMap, "table_two", TableServo.direction);
    }

    @Override
    public void setPower(float power) {
        super.setPower(power);
        if (splitState == SplitState.USING_BOTH)
            secondaryServo.setPower(power);
        else if (splitState == SplitState.RESETTING)
            secondaryServo.setPower(power * 0.25f);
        else
            secondaryServo.setPower(power * 0.5f);
    }

    public SplitState getSplitState() {
        return splitState;
    }

    public void setSplitState(SplitState splitState) {
        this.splitState = splitState;
    }
}
