package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.CRServoInternal;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TableServo extends HwCRServo2 <TableServo.TableState> {
    private final PIDFController controller = new PIDFController(new PIDFCoefficients(0f,0f,0f,0f));
    public static  float BALL1 = 0f;
    public static  float BALL2 = 0f;
    public static  float BALL3 = 0f;
    public static int ticks = 0;
    public static final DcMotorSimple.Direction TABLE_DIRECTION = DcMotorSimple.Direction.FORWARD;

    @Override
    public int ticksPerRotation() {
        return ticks;
    }

    public enum TableState implements CRServoInternal.MotorState {
        BALL1(0f),
        BALL2(0f),
        BALL3(0f);

        float position;

        TableState(float position) {
            this.position = position;
        }


        @Override
        public float power() {
            return 0;
        }
    }

    public Class<TableState> getStateClass() {
        return TableState.class;
    }

    public void updateController(int targetPosition) {
        controller.setTargetPosition(targetPosition);
    }



    public TableState getTestState() {
        return null;
    }

    public TableServo(HardwareMap hardwareMap) throws IllegalArgumentException{
        super(hardwareMap, "", TABLE_DIRECTION, TableState.BALL1 , null,null);
    }

}