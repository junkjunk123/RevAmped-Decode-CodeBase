package org.firstinspires.ftc.teamcode.UG;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.UG.Depo.GateServo;
import org.firstinspires.ftc.teamcode.UG.Intake.IntakeTiltServo;

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
        GateServo.OPEN = 0/255f;
        GateServo.CLOSE = 0/255f;
        IntakeTiltServo.UP = 0/255f;
        IntakeTiltServo.DOWN = 0/255f;
    }
}
