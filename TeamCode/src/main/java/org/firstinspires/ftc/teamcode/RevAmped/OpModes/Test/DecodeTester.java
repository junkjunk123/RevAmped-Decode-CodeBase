package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.pedropathing.telemetry.SelectableOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RevAmped.Robot;

import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.SelectableRevAmpedOpMode;

@TeleOp(name = "DecodeTester")
public class DecodeTester extends SelectableRevAmpedOpMode {
    public DecodeTester() {
        super("Select a Testing OpMode.", s -> {
            s.folder("Pre-Match Tester", l -> {
                l.add("AutomatedTest", AutomatedTest::new);
            });
            s.folder("Calibrater", l -> {
                l.add("ServoCalibrateTest",ServoCalibrateTest::new);
            });
        });
    }

    @Override
    public OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
