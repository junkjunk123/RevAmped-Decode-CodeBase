package org.firstinspires.ftc.teamcode.revAmped.opmodes.auto;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.commands.Follow;
import com.pedropathing.ivy.commands.Infinite;
import com.pedropathing.ivy.commands.Instant;
import com.pedropathing.ivy.commands.Wait;
import com.pedropathing.ivy.commands.WaitUntil;
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

import java.util.List;

@Autonomous
public class RevAmpedRevAmpedCommandAuto extends OpModeCommand {
    private Robot robot;
    private Follower follower;
    private PathManager pathManager;
    private DecodeLimelight limelight;

    @Override
    public void RevAmpedInit() {
        Globals.init(telemetry);
        Logger.reset();
        robot = new Robot(this, Globals.AllianceColor.Red);
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(pathManager.getStartingPose());
        pathManager = new PathManager(new CloseAutoPaths());
        limelight = new DecodeLimelight(hardwareMap);

        schedule(
             new Infinite(() -> {
                 follower.update();
                 robot.update();
             }),
             new Follow(follower, pathManager.next()),
             new Sequential(
                     new Command()
                             .setStart(() -> {
                                 robot.flywheel().run(Flywheel.FLYWHEEL_NEAR_VELOCITY);
                                 robot.hoodServo().setCurrentState(Types.HoodState.NEAR_PRESET);
                                 if (Globals.allianceColor == Globals.AllianceColor.Red) {
                                     TurretMotor.AUTO_PRELOADS *= -1;
                                     TurretMotor.AUTO_SET_1 *= -1;
                                 }
                                 limelight.setCurrentPipeline(DecodeLimelight.Pipeline.OBELISK);
                             })
                             .setExecute(
                                     () -> limelight.update()
                             )
                             .setDone(() -> Globals.randomizationState != null),
                     new Instant(() -> {
                         robot.tableServo().setCurrentState(computeTableRotation());
                         robot.turretMotor().setCurrentState(Types.TurretState.HOLD, TurretMotor.AUTO_PRELOADS);
                     }),
                     new WaitUntil(robot.tableServo()::reached),
                     new Wait(150),
                     new Instant(() -> robot.popper().setCurrentState(Types.PopperState.POP)),
                     new Wait(650),
                     robot.turnTable.shootAllAuto()
             )
        );
    }

    private static Types.TableState2 computeTableRotation() {
        List<Types.TableState2> tableStates = List.of(Types.TableState2.BALL1, Types.TableState2.BALL2, Types.TableState2.BALL3);
        int greenIndex = Globals.randomizationState.getGreenIndex();
        int curGreenIndex = 1;
        int index = curGreenIndex - greenIndex;
        index = (index + 3) % 3;
        return tableStates.get(index);
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.AUTO;
    }
}
