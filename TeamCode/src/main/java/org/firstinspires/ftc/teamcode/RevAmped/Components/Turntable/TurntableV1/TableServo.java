package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable.TurntableV1;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RevAmped.Util.Types.TableState;
import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TableServo extends HwCRServo2 <TableState> {
    public static PIDFCoefficients TABLE_PIDF_COEFFICIENTS;
    private final PIDFController controller = new PIDFController(TABLE_PIDF_COEFFICIENTS);
    public static int BALL1 = 0;
    public static int BALL2 = 0;
    public static int BALL3 = 0;
    public static int TICKS_PER_ROTATION = 0;

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

    public TableServo(HardwareMap hardwareMap, TableState state, Encoder encoder) throws IllegalArgumentException{
        super(hardwareMap, "table", DcMotorSimple.Direction.FORWARD, state, encoder);
    }

    @Override
    public void setCurrentState(TableState servoState) {
        super.setCurrentState(servoState);
        controller.reset();
        controller.setTargetPosition(servoState.getPosition());
    }
}