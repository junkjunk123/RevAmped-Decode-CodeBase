package org.firstinspires.ftc.teamcode.UG.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.UG.UGRobot;

import revAmped.components.Architecture.TeleOpStateHandler;
import revAmped.components.GamepadDevices.RevAmpedGamepad;
import revAmped.components.Misc.Globals;
import revAmped.components.Misc.OpModeType;
import revAmped.components.Misc.RevAmpedOpMode;

@TeleOp
public class OutreachTeleOp extends RevAmpedOpMode {
    private UGRobot robot;
    private RevAmpedGamepad admin;
    private RevAmpedGamepad guest;
    public enum CycleState implements TeleOpStateHandler.State{
        SHOOT,
        INTAKE,
        NEUTRAL;

    }
    public void RevAmpedInit(){
        robot = new UGRobot(this, Globals.AllianceColor.None);
        admin = new RevAmpedGamepad(gamepad1);
        guest = new RevAmpedGamepad(gamepad2);
    }
    public void RevAmpedLoop(){
    }
    public void update(){
        telemetry.update();
        admin.update();
        guest.update();
    }

    @Override
    public OpModeType getOpModeType() {
        return UGRobot.OpModeType.TELEOP;
    }
}
