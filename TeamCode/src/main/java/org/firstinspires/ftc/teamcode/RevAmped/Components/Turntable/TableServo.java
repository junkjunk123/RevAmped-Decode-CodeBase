package org.firstinspires.ftc.teamcode.RevAmped.Components.Turntable;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwCRServo2;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TableServo extends HwCRServo2 <TableServo.TableState> {
    private final PIDFController controller = new PIDFController(new PIDFCoefficients(0f,0f,0f,0f));
    public static  float BALL1 = 0f;
    public static  float BALL2 = 0f;
    public static  float BALL3 = 0f;
    public static final DcMotorSimple.Direction TABLE_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public enum TableState implements HwCRServo2.ServoState {
        BALL1(0f),
        BALL2(0f),
        BALL3(0f);

        float position;

        TableState(float position) {
            this.position = position;
        }

        @Override
        public int position() {
            return 0;
        }
    }

    @Override
    public Class<TableState> getStateClass() {
        return TableState.class;
    }

    @Override
    public void buildPowerMap() {
        powerMap.put(TableState.BALL1, () -> controller.run(getCurrentPosition()));
        powerMap.put(TableState.BALL2, () -> controller.run(getCurrentPosition()));
        powerMap.put(TableState.BALL3, () -> controller.run(getCurrentPosition()));
    }

    @Override
    public void buildInitMap() {
        initMap.put(TableState.BALL1, new InitActions());
        initMap.put(TableState.BALL2, new InitActions());
        initMap.put(TableState.BALL3, new InitActions());
    }

    @Override
    public void updateController(int targetPosition) {
        controller.setTargetPosition(targetPosition);
    }

    @Override
    protected void retraction() {
        setCurrentState(TableState.BALL1);
    }

    @Override
    public void init() {
        setTargetPosition(0);
        super.init();
    }

    public boolean retracted(){ //placeholder values
        return getVelocity() < 0 && getCurrentPosition() < 0;
    }

    @Override
    public TableState getTestState() {
        return null;
    }

    public TableServo(HardwareMap hardwareMap) throws IllegalArgumentException{
        super(hardwareMap, "", TABLE_DIRECTION, null , TableState.BALL1,null);
        resetDetector = this::retracted;
        init();
    }
}