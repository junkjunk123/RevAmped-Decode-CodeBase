package org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Auto;

import org.firstinspires.ftc.teamcode.RevAmped2024.offseason.mechanisms.Robot;

import revAmped.components.Architecture.AutonomousActionExecutor;
import revAmped.components.Pedro.FollowerManager;
import revAmped.components.Misc.Globals;

public abstract class ITDAutonomousActionExecutor extends AutonomousActionExecutor {
    public ITDAutonomousParameters parameters;

    public static class ITDAutonomousParameters {
        private Robot robot;
        private FollowerManager followerManager;

        public ITDAutonomousParameters(Robot robot, FollowerManager follower) {
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

    public ITDAutonomousActionExecutor(ITDAutonomousParameters parameters) {
        super();
        this.parameters = parameters;
        ITDSpecAutonomousMethods.initialize();
        Globals.aae = this;
    }
}
