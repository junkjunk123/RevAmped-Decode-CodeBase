package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Octocanum{
    private OctocanumFront front;
    private OctocanumBack back;
    private OctoState octoState;
    public Octocanum(HardwareMap hardwareMap){
        front = new OctocanumFront(hardwareMap,true);
        back = new OctocanumBack(hardwareMap, true);
        octoState = OctoState.UP;
    }
    public enum OctoState{
        UP,
        DOWN;
    }
    public void engage(){
        front.setCurrentState(OctocanumFront.servoState.DOWN);
        back.setCurrentState(OctocanumBack.servoState.DOWN);
        octoState = OctoState.DOWN;
    }
    public void raise(){
        front.setCurrentState(OctocanumFront.servoState.UP);
        back.setCurrentState(OctocanumBack.servoState.UP);
        octoState = OctoState.UP;
    }
    public void toggle(){
        switch (octoState){
            case UP -> {engage();}
            case DOWN -> {raise();}
        }
    }
    public OctoState getState(){
        return octoState;
    }
}
