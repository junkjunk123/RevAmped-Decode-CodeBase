package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.CRServoInternal;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TableServo extends HwCRServo2 <TableServo.TableState> {
    private final PIDFController controller = new PIDFController(new PIDFCoefficients(0f,0f,0f,0f));
    public static int BALL1 = 0;
    public static int BALL2 = 0;
    public static int BALL3 = 0;
    public static int TICKS_PER_ROTATION = 0;
    public static final DcMotorSimple.Direction TABLE_DIRECTION = DcMotorSimple.Direction.FORWARD;

    @Override
    public int ticksPerRotation() {
        return TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TableState.REST, ServoPower.constant(0f));
        statePowerMap.put(TableState.BALL1, () -> (float) controller.runWithError(getError(BALL1)));
        statePowerMap.put(TableState.BALL2, () -> (float) controller.runWithError(getError(BALL2)));
        statePowerMap.put(TableState.BALL3, () -> (float) controller.runWithError(getError(BALL3)));
    }

    public enum TableState implements CRServoInternal.ServoState {
        REST(0),
        BALL1(0),
        BALL2(0),
        BALL3(0);

        final int position;

        TableState(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public TableServo(HardwareMap hardwareMap, TableState state) throws IllegalArgumentException{
        super(hardwareMap, "", TABLE_DIRECTION, state , null,null);
    }

    @Override
    public void setCurrentState(TableState servoState) {
        super.setCurrentState(servoState);
        controller.reset();
        controller.setTargetPosition(servoState.position);
    }
}