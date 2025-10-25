package org.firstinspires.ftc.teamcode.revAmped.opmodes.teleop.PrototypeTeleOp.Mechanisms;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.CRServoInternal;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.HwCRServo2;
import org.firstinspires.ftc.teamcode.revAmped.components.math.Differentiator;

public class CRTableServo extends HwCRServo2<CRTableServo.TableState> {
    //Variable Declarations
    private final PIDFController controller;
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    public final Differentiator velocityComputer;
    public static int TICKS_PER_ROTATION;
    public static int REST;

    //Constructors
    public CRTableServo(HardwareMap hardwareMap, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, "table", DcMotorSimple.Direction.REVERSE, TableState.REST, encoder);
        controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
        resetPosition();
        velocityComputer = new Differentiator(encoder);
    }

    @Override
    public void setCurrentState(TableState servoState){
        if (getCurrentState() != servoState){
            controller.reset();
            velocityComputer.reset();
        }
        super.setCurrentState(servoState);
    }

    //Mandatory Methods
    @Override
    public int ticksPerRotation() {
        return TICKS_PER_ROTATION;
    }
    @Override
    public int getCurrentPosition(){return super.getCurrentPosition() % TICKS_PER_ROTATION;}
    @Override
    public double getLastError() {
        return controller.getError();
    }

    @Override
    public double getVelocity() {
        return velocityComputer.calculate();
    }

    @Override
    public void reverse() {

    }

    @Override
    public void finetune(int ticks) {

    }

    @Override
    public void buildStatePowerMap() {
        //statePowerMap.put(TableState.REST, () -> (float) MathFunctions.clamp(controller.runWithError(getError(REST)),-1,1));
        statePowerMap.put(TableState.REST, ServoPower.constant(0f));
    }

    public enum TableState implements CRServoInternal.ServoState{
        REST;
    }
}
