package org.firstinspires.ftc.teamcode.revAmped.opmodes.test;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.DecodeConstants;
import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpDrivetrainHandler;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;

@Config
@TeleOp
public class HoldPoseTest extends DecodeRevAmpedOpMode {
    private TeleOpDrivetrainHandler drive;
    private RevAmpedGamepad gamepad_1;
    public static boolean test1 = false;
    public static boolean test2 = false;
    public static boolean test3 = false;

    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        drive = new TeleOpDrivetrainHandler.TeleOpDrivetrainBuilder()
                .setOpMode(this)
                .setGamepad(gamepad_1)
                .setDirections(DecodeConstants.DRIVE_DIRECTIONS)
                .setLocalizer(new PinpointLocalizer(hardwareMap, Constants.localizerConstants))
                .build();
        drive.startHoldPose();
    }

    @Override
    public void RevAmpedLoop() {
        drive.arcadeDrive();
    }

    @Override
    public void update() {
        gamepad_1.update();
        drive.updateLocalization();
        telemetry.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TEST;
    }
}
