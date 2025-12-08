package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import static org.firstinspires.ftc.teamcode.revAmped.mechanisms.turntable.turntableV1.TableServoV2.computeTableRotation;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.follow.Follow;
import com.pedropathing.ivy.commands.Infinite;
import com.pedropathing.ivy.commands.Instant;
import com.pedropathing.ivy.commands.Wait;
import com.pedropathing.ivy.commands.WaitUntil;
import com.pedropathing.ivy.groups.Parallel;
import com.pedropathing.ivy.groups.Sequential;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Logger;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.OpModeCommand;
import org.firstinspires.ftc.teamcode.revAmped.components.pedro.PathManager;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.DecodeLimelight;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.Flywheel;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter.TurretMotor;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.auto.paths.CloseAutoPaths;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

@Autonomous
public class CommandAuto extends OpModeCommand {
    private Robot robot;
    private Follower follower;
    private PathManager pathManager;
    private DecodeLimelight limelight;

    @Override
    public void RevAmpedInit() {
        Globals.init(telemetry);
        DecodeLimelight.usingLimelight = true;
        Logger.reset();
        robot = new Robot(this, Globals.AllianceColor.Red, Robot.OpModeType.AUTO);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(pathManager.getStartingPose());
        pathManager = new PathManager(new CloseAutoPaths());
        limelight = new DecodeLimelight(hardwareMap);

        schedule (
             new Infinite(() -> {
                 follower.update();
                 robot.update();
             }),
             new Sequential(
                     new Parallel(
                             new Follow(follower, pathManager.next())
                                     .timeoutAfter(5000),
                             new Command()
                                     .setStart(() -> {
                                         robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
                                         robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
                                         limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                                     })
                                     .setExecute(() -> limelight.update())
                                     .setDone(() -> Globals.randomizationState != null)
                                     .setEnd(b -> new Instant(() -> {
                                         if (!b) {
                                             robot.tableServo().setCurrentState(computeTableRotation());
                                             robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                         } else {
                                             robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                                         }
                                     }))
                                     .timeoutAfter(5000)
                     ),
                     new WaitUntil(robot.tableServo()::reached)
                             .timeoutAfter(650),
                     new Wait(150),
                     new Instant(() -> robot.popper().setCurrentState(Types.PopperState.POP)),
                     new Wait(650),
                     robot.turnTable.shootAllAuto()
             )
        );
    }
}
