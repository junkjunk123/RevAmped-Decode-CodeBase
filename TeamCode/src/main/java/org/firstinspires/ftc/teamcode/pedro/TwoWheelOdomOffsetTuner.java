package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class TwoWheelOdomOffsetTuner extends OpMode {
    private Follower follower;
    private ElapsedTime timer;

    @Override
    public void init() {
        timer = new ElapsedTime();
        follower = Constants.createFollower(hardwareMap);
    }

    @Override
    public void start() {
        timer.reset();
        follower.startTeleOpDrive(true);
        follower.update();
        follower.setTeleOpDrive(0, 0, -1);
    }

    @Override
    public void loop() {

    }
}
