package org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.sensor;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwSwitch;

public class IntakeDigitalDistance extends HwSwitch implements IntakeDistance {
    public IntakeDigitalDistance(HardwareMap hardwareMap) {
        super(hardwareMap, "intakeDistance", true, false);
    }

    @Override
    public boolean hasArtifact() {
        return isTouch();
    }
}
