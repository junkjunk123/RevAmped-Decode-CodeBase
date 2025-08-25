package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.Architecture.AutonomousActionExecutor;
import revAmped.components.Pedro.FollowerManager;
import revAmped.components.Misc.Globals;

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
        DecodeAutonomousMethods.initialize();
        Globals.aae = this;
    }
}
