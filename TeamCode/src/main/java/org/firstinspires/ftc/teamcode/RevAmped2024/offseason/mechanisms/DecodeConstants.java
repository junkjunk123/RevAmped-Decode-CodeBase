package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

    }
}
