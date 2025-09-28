package org.firstinspires.ftc.teamcode.RevAmped;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.Popper;

import java.util.List;

import revAmped.components.Misc.Constants;

public final class DecodeConstants implements Constants {
    public static List<DcMotorSimple.Direction> DRIVE_DIRECTIONS = List.of(
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE,
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE
    );

    public void build() {
        Popper.POP = 72/255f; Popper.NEUTRAL = 110/255f;
        Popper.
    }
}
