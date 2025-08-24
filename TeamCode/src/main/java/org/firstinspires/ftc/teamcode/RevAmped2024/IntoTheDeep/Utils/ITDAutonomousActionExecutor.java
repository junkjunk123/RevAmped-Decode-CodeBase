package org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Utils;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.Autonomous.Actions.ITDAutonomousMethods;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Deposition.DepositionArm;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.Intake.IntakeFourbar;
import org.firstinspires.ftc.teamcode.RevAmped2024.IntoTheDeep.ITDMechanisms.RobotRevampedITD;

import revAmped.components.CommandScheduler.Scheduler;
import revAmped.components.PedroEnhancements.AutonomousActionExecutor;
import revAmped.components.PedroEnhancements.FollowerManager;

public abstract class ITDAutonomousActionExecutor extends AutonomousActionExecutor {
    public static ITDAutonomousParameters parameters;

    public static class ITDAutonomousParameters {
        private RobotRevampedITD robot;
        private FollowerManager followerManager;
        private Follower follower;
        private Scheduler scheduler;
        private IntakeFourbar intakeFourbar;
        private DepositionArm depositionArm;
        private Telemetry telemetry;

        public ITDAutonomousParameters(RobotRevampedITD robot, FollowerManager follower, Scheduler scheduler, IntakeFourbar intakeFourbar, DepositionArm depositionArm, Telemetry telemetry) {
            this.robot = robot;
            this.followerManager = follower;
            this.follower = followerManager.getFollower();
            this.scheduler = scheduler;
            this.intakeFourbar = intakeFourbar;
            this.depositionArm = depositionArm;
            this.telemetry = telemetry;
        }

        public Follower getFollower() {
            return follower;
        }

        public DepositionArm getDepositionArm() {
            return depositionArm;
        }

        public FollowerManager getFollowerManager() {
            return followerManager;
        }

        public RobotRevampedITD getRobot() {
            return robot;
        }

        public Scheduler getScheduler() {
            return scheduler;
        }

        public IntakeFourbar getIntakeFourbar() {
            return intakeFourbar;
        }

        public Telemetry getTelemetry() {
            return telemetry;
        }
    }

    public ITDAutonomousActionExecutor(ITDAutonomousParameters parameters) {
        super();
        ITDAutonomousActionExecutor.parameters = parameters;
        ITDAutonomousMethods.initialize();
    }
}
