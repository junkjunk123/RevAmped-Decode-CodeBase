package org.firstinspires.ftc.teamcode.RevAmped.Components;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwSlide2;
import revAmped.components.Hardware.Turret;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TurretServo extends Turret<TurretServo.TurretState> {
    public TurretServo(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, TurretState motorState, HwSlide2.SlideRetractionDetector detector) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState, detector);
    }

    public static PIDFCoefficients TURRET_PIDF_COEFFICIENTS = new PIDFCoefficients(0, 0, 0, 0);
    public final PIDFController controller = new PIDFController(TURRET_PIDF_COEFFICIENTS);
    public static int TURRET_TICKS_PER_ROTATION = 0;
    public static int TURRET_MAX_RAD = 0;
    public static int TURRET_MIN_RAD = 0;
    public static int TURRET_MAX_POS = 0;
    private int targetPosition;

    @Override
    public int ticksPerRotation() {
        return TURRET_TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TurretState.IDLE, MotorPower.constant(0f));
        statePowerMap.put(TurretState.TRACKING, () -> (float) controller.runPIDF());
    }

    public enum TurretState implements MotorState {
        IDLE (0),
        TRACKING (0);

        final int pos;

        TurretState(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }
    }

    @Override
    public void setMotorState(TurretState motorState) {
        super.setMotorState(motorState);
        targetPosition = motorState.getPos();
        controller.reset();
        controller.setTargetPosition(targetPosition);
    }

    public void setMotorState(TurretState motorState, int targetPosition) {
        super.setMotorState(motorState);
        this.targetPosition = targetPosition;
        controller.reset();
        controller.setTargetPosition(targetPosition);
    }

    @Override
    public void update() {
        super.update();
        controller.updateError(getError(targetPosition));
    }
}
