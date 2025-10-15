package org.firstinspires.ftc.teamcode.revAmped.mechanisms.sorter;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.revAmped.components.drive.TeleOpMecanumDrive;
import org.firstinspires.ftc.teamcode.revAmped.components.gamepad.RevAmpedGamepad;
import org.firstinspires.ftc.teamcode.revAmped.mechanisms.Robot;
import org.firstinspires.ftc.teamcode.revAmped.opmodes.DecodeRevAmpedOpMode;
import org.firstinspires.ftc.teamcode.revAmped.util.Types;

import java.util.Arrays;

public class PrototypeMGNTeleOp extends DecodeRevAmpedOpMode {
    private RevAmpedGamepad gamepad_1;
    private HorizontalMGNServo horizontalMGNServo;
    private VerticalMGNServo verticalMGNServo;
    private GateServo gateServo;
    private PopperServo popperServo;
    private TeleOpMecanumDrive drivetrain;
    @Override
    public void RevAmpedInit() {
        gamepad_1 = new RevAmpedGamepad(gamepad1);
        drivetrain = new TeleOpMecanumDrive(this, Arrays.asList(DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.REVERSE, DcMotorSimple.Direction.FORWARD, DcMotorSimple.Direction.FORWARD));
        horizontalMGNServo = new HorizontalMGNServo(hardwareMap, true);
        verticalMGNServo = new VerticalMGNServo(hardwareMap, true);
        gateServo = new GateServo(hardwareMap, true);
        popperServo = new PopperServo(hardwareMap, true);
    }

    @Override
    public void RevAmpedLoop() {
        drivetrain.arcadeDrive(gamepad_1, false, 0.5f, 0.15f, (float) Math.tan(Math.toRadians(15)), 1f);
        if(gamepad_1.a.isTrue()){
            popperServo.setCurrentState(PopperServo.PopperState.POP);
        } else{
            popperServo.setCurrentState(PopperServo.PopperState.DOWN);
        }
        if(gamepad_1.left_bumper.isPress()){
            if(horizontalMGNServo.getCurrentState() == Types.HorizontalMGNState.OUT){
                horizontalMGNServo.setCurrentState(Types.HorizontalMGNState.LOADED);
            } else{
                horizontalMGNServo.setCurrentState(Types.HorizontalMGNState.OUT);
            }
        }
        if(gamepad_1.right_bumper.isPress()){
            if(verticalMGNServo.getCurrentState() == Types.VerticalMGNState.OUT){
                verticalMGNServo.setCurrentState(Types.VerticalMGNState.LOADED);
            } else{
                verticalMGNServo.setCurrentState(Types.VerticalMGNState.OUT);
            }
        }
    }

    @Override
    public void update() {
        gamepad_1.update();
    }

    @Override
    public Robot.OpModeType getOpModeType() {
        return Robot.OpModeType.TELEOP;
    }
}
