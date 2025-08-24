package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils;

import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;

import revAmped.components.Misc.RevAmpedOpMode;

public abstract class ITDRevAmpedOpMode extends RevAmpedOpMode {
    public abstract RobotRevampedITD.OpModeType getOpModeType();
}