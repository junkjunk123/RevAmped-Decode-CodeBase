package org.firstinspires.ftc.teamcode.revAmped.components.drive;

import com.pedropathing.geometry.Pose;
import com.pedropathing.localization.Localizer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;

import java.util.List;

public class TeleOpDrivetrainHandler extends TeleOpMecanumDrive {
    public static TeleOpDrivetrainHandler INSTANCE;
    private RevAmpedGamepad gamepad_1;
    public static float SLOW_MULT = 0.5f;
    public static float MAGNITUDE_ZERO = 0.15f;
    public static float ANGLE_ZERO = (float) Math.tan(Math.toRadians(15));
    public static float MAX_POWER = 1.0f;
    private boolean slowMode = false;
    private boolean invertedControls = false;
    private Localizer localizer;
    private static Pose initialPose = new Pose(72, 72, Math.toRadians(90));

    public TeleOpDrivetrainHandler(OpMode op, List<DcMotorSimple.Direction> directions) {
        super(op, directions);
        INSTANCE = this;
    }

    public void arcadeDrive() {
        if (!invertedControls) arcadeDrive(gamepad_1, slowMode, SLOW_MULT, MAGNITUDE_ZERO, ANGLE_ZERO, MAX_POWER);
        else arcadeDrive(-gamepad_1.left_stick_x, -gamepad_1.left_stick_y, gamepad_1.right_stick_x, slowMode, SLOW_MULT, MAGNITUDE_ZERO, ANGLE_ZERO, MAX_POWER);
    }

    public void updateLocalization() {
        if (localizer != null) {
            localizer.update();
        }
    }

    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
        getHeading = localizer.getPose()::getHeading;
    }

    public void setGamepad_1(RevAmpedGamepad gamepad_1) {
        this.gamepad_1 = gamepad_1;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public Pose getVelocity() {
        return localizer.getVelocity();
    }

    public Pose getPose() {
        return localizer.getPose();
    }

    public void toggleSlowMode() {
        slowMode = !slowMode;
    }

    public void toggleFieldCentric() {
        fieldCentric = !fieldCentric;
    }

    public void setInvertedControls(boolean invertedControls) {
        this.invertedControls = invertedControls;
    }

    public static class TeleOpDrivetrainBuilder {
        private OpMode op;
        private List<DcMotorSimple.Direction> directions;
        private RevAmpedGamepad gamepad_1;
        private Localizer localizer;
        private boolean invertedControls = false;

        public TeleOpDrivetrainBuilder setDirections(List<DcMotorSimple.Direction> directions) {
            this.directions = directions;
            return this;
        }

        public TeleOpDrivetrainBuilder setOpMode(OpMode op) {
            this.op = op;
            return this;
        }

        public TeleOpDrivetrainBuilder setGamepad(RevAmpedGamepad gamepad_1) {
            this.gamepad_1 = gamepad_1;
            return this;
        }

        public TeleOpDrivetrainBuilder setLocalizer(Localizer localizer) {
            this.localizer = localizer;
            return this;
        }

        public TeleOpDrivetrainBuilder invertControls() {
            this.invertedControls = true;
            return this;
        }

        public TeleOpDrivetrainHandler build() {
            TeleOpDrivetrainHandler dt = new TeleOpDrivetrainHandler(op, directions);
            dt.setGamepad_1(gamepad_1);
            localizer.setStartPose(initialPose);
            dt.setLocalizer(localizer);
            dt.setInvertedControls(invertedControls);
            return dt;
        }
    }

    public static void setInitialPose(Pose pose) {
        initialPose = pose;
    }

    public static Pose getInitialPose() {
        return initialPose;
    }
}
