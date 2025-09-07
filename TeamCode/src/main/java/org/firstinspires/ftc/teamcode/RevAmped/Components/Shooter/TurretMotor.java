package org.firstinspires.ftc.teamcode.RevAmped.Components.Shooter;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import revAmped.components.Hardware.HwSlide2;
import revAmped.components.Hardware.Turret;
import revAmped.components.SlideController.PIDF.PIDFController;

public class TurretMotor extends Turret<TurretMotor.TurretState> {
    public TurretMotor(HardwareMap hardwareMap, TurretState motorState, HwSlide2.SlideRetractionDetector detector) throws IllegalArgumentException {
        super(hardwareMap, "turret", DcMotorSimple.Direction.FORWARD, motorState, detector);
    }

    public static PIDFCoefficients TURRET_PIDF_COEFFICIENTS = new PIDFCoefficients(0, 0, 0, 0);
    public final PIDFController controller = new PIDFController(TURRET_PIDF_COEFFICIENTS);
    public static int TURRET_TICKS_PER_ROTATION = 0;
    private int targetPosition;

    @Override
    public int ticksPerRotation() {
        return TURRET_TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TurretState.IDLE, MotorPower.constant(0f));
        statePowerMap.put(TurretState.HOLD, () -> (float) controller.runWithError(getError(targetPosition)));
        statePowerMap.put(TurretState.TRACKING, () -> (float) controller.runWithError(getError(targetPosition)));
    }

    public enum TurretState implements MotorState {
        IDLE (0),
        HOLD(0),
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
    }

    public void setMotorState(TurretState motorState, int targetPosition) {
        super.setMotorState(motorState);
        this.targetPosition = targetPosition;
        controller.reset();
    }
    
    public void finetuneAngle(double rad) {
        setMotorState(TurretState.TRACKING, (int) (targetPosition + rad * ticksPerRotation() / (2 * Math.PI)));
    }
}
