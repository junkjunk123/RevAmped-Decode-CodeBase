package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;

import org.firstinspires.ftc.teamcode.revAmped.components.architecture.AutonomousActionExecutor;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.FollowerManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;

public abstract class DecodeAutonomousActionExecutor extends AutonomousActionExecutor {
    public DecodeAutonomousParameters parameters;

    public static class DecodeAutonomousParameters {
        private Robot robot;
        private FollowerManager followerManager;

        public DecodeAutonomousParameters(Robot robot, FollowerManager follower) {
            this.robot = robot;
            this.followerManager = follower;
        }

        public FollowerManager getFollowerManager() {
            return followerManager;
        }

        public Robot getRobot() {
            return robot;
        }
    }

    public DecodeAutonomousActionExecutor(DecodeAutonomousParameters parameters) {
        super();
        this.parameters = parameters;
        DAM.initialize();
        Globals.aae = this;
    }
}
