package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

public abstract class DecodeAutonomousActionExecutor extends AutonomousActionExecutor {
    public DecodeAutonomousParameters parameters;

    public record DecodeAutonomousParameters(Robot robot, FollowerManager followerManager) {}

    public DecodeAutonomousActionExecutor(DecodeAutonomousParameters parameters) {
        super();
        this.parameters = parameters;
        Globals.aae = this;
        DAM.initialize();
    }
}
