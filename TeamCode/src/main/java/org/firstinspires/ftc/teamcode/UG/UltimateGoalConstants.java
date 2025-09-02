package org.firstinspires.ftc.teamcode.UG;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.List;

import revAmped.components.Misc.Constants;

public final class UltimateGoalConstants implements Constants {
    public static List<DcMotorSimple.Direction> DRIVE_DIRECTIONS = List.of(
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE,
            DcMotorSimple.Direction.FORWARD,
            DcMotorSimple.Direction.REVERSE
    );

    public void build() {

    }
}
