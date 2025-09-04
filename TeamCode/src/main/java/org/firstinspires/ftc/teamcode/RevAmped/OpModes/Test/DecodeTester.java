package org.firstinspires.ftc.teamcode.RevAmped.OpModes.Test;

import com.pedropathing.telemetry.SelectableOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "DecodeTester")
public class DecodeTester extends SelectableOpMode {
    public DecodeTester() {
        super("Select a Testing OpMode.", s -> {
            s.folder("Pre-Match Tester", l -> {
                l.add("Automated Test", AutomatedTest::new);
            });
        });
    }
}
