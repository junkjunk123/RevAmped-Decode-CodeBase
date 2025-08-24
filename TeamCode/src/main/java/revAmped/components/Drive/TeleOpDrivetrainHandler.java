package revAmped.components.Drive;
import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import java.util.List;

import revAmped.components.GamepadDevices.RevAmpedGamepad;

public class TeleOpDrivetrainHandler extends TeleOpMecanumDrive {
    public static TeleOpDrivetrainHandler INSTANCE;
    private RevAmpedGamepad gamepad_1;
    public static float SLOW_MULT = 0.5f;
    public static float MAGNITUDE_ZERO = 0.15f;
    public static float ANGLE_ZERO = (float) Math.tan(Math.toRadians(15));
    public static float MAX_POWER = 1.0f;
    private boolean slowMode = false;
    private Localizer localizer;

    public TeleOpDrivetrainHandler(OpMode op, List<DcMotorSimple.Direction> directions) {
        super(op, directions);
        INSTANCE = this;
    }

    public void arcadeDrive() {
        arcadeDrive(gamepad_1, slowMode, SLOW_MULT, MAGNITUDE_ZERO, ANGLE_ZERO, MAX_POWER);
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

    public static class TeleOpDrivetrainBuilder {
        private OpMode op;
        private List<DcMotorSimple.Direction> directions;
        private RevAmpedGamepad gamepad_1;
        private Localizer localizer;

        public TeleOpDrivetrainBuilder setDirections(List<DcMotorSimple.Direction> directions) {
            this.directions = directions;
            return this;
        }

        public TeleOpDrivetrainBuilder setOpMode(OpMode op) {
            this.op = op;
            return this;
        }

        public TeleOpDrivetrainBuilder setGamepad_1(RevAmpedGamepad gamepad_1) {
            this.gamepad_1 = gamepad_1;
            return this;
        }

        public TeleOpDrivetrainBuilder setLocalizer(Localizer localizer) {
            this.localizer = localizer;
            return this;
        }

        public TeleOpDrivetrainHandler build() {
            TeleOpDrivetrainHandler dt = new TeleOpDrivetrainHandler(op, directions);
            dt.setGamepad_1(gamepad_1);
            dt.setLocalizer(localizer);
            return dt;
        }
    }
}
