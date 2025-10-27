package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;

import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

public class FlywheelTicksCalibrater extends DecodeRevAmpedOpMode {
    private Flywheel flywheel;

    @Override
    public void RevAmpedInit() {
        flywheel = new Flywheel(hardwareMap, Types.FlywheelState.STOPPED);
    }

    @Override
    public void RevAmpedLoop() {
        telemetry.addData("pos", flywheel.getCurrentPosition());
    }

    @Override
    public void update() {
        flywheel.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
