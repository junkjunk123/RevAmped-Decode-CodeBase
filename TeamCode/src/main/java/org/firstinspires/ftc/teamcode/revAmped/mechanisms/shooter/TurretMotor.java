package org.firstinspires.ftc.teamcode.revAmped.mechanisms.shooter;

import com.pedropathing.control.PIDFCoefficients;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.revAmped.components.controller.PIDF.PIDFController;
import org.firstinspires.ftc.teamcode.revAmped.components.hardware.Turret;
import org.firstinspires.ftc.teamcode.revAmped.components.misc.Globals;
import org.firstinspires.ftc.teamcode.revAmped.util.Range;
import org.firstinspires.ftc.teamcode.revAmped.util.Types.TurretState;

public class TurretMotor extends Turret<TurretState> {
    public TurretMotor(HardwareMap hardwareMap, TurretState motorState) throws IllegalArgumentException {
        super(hardwareMap, "turret", DcMotorSimple.Direction.REVERSE, motorState);
        controller = new PIDFController(TURRET_PIDF_COEFFICIENTS);
    }

    public static PIDFCoefficients TURRET_PIDF_COEFFICIENTS;
    public final PIDFController controller;
    public static int TURRET_TICKS_PER_ROTATION;
    private int targetPosition;
    public static float AUTO_PRELOADS;
    public static float AUTO_SET_1;
    public static float AUTO_SET_2;
    public static float AUTO_SET_3;
    public static float LL_DETECTION_RED;
    public static float LL_DETECTION_BLUE;
    public static boolean TURRET_LIMITS_ENABLED;
    public static int TURRET_POSITIVE_LIMIT_TICKS;
    public static int TURRET_NEGATIVE_LIMIT_TICKS;

    private double prevFineTuneAngle = 0;

    @Override
    public int ticksPerRotation() {
        return TURRET_TICKS_PER_ROTATION;
    }

    @Override
    public void buildStatePowerMap() {
        statePowerMap.put(TurretState.IDLE, MotorPower.constant(0f));
        statePowerMap.put(TurretState.HOLD, () -> (float) controller.runWithError(!TURRET_LIMITS_ENABLED ? getError(targetPosition) : targetPosition - getCurrentPosition()));
        statePowerMap.put(TurretState.TRACKING, () -> (float) controller.runWithError(!TURRET_LIMITS_ENABLED ? getError(targetPosition) : targetPosition - getCurrentPosition()));
    }

    @Override
    public TurretState getRest() {
        return TurretState.IDLE;
    }

    @Override
    public void setCurrentState(TurretState motorState) {
        super.setCurrentState(motorState);
        targetPosition = motorState.getPos();
        controller.reset();
        prevFineTuneAngle = 0;
    }

    public void setCurrentState(TurretState motorState, int targetPosition) {
        super.setCurrentState(motorState);
        this.targetPosition = targetPosition;
        controller.reset();
        prevFineTuneAngle = 0;
    }
    
    public void finetuneAngle(double rad) {
        prevFineTuneAngle = rad;
        targetPosition = Range.clip((int) (rad * ticksPerRotation() / (2 * Math.PI)), TURRET_NEGATIVE_LIMIT_TICKS, TURRET_POSITIVE_LIMIT_TICKS);
        Globals.telemetry.addData("angle",targetPosition);
        motorState = TurretState.TRACKING;
    }

    public void limelightDetection() {
        switch (Globals.allianceColor) {
            case Blue -> setCurrentState(TurretState.HOLD, LL_DETECTION_BLUE);
            case Red -> setCurrentState(TurretState.HOLD, LL_DETECTION_RED);
        }
    }

    public double getPrevFineTuneAngle() {
        return prevFineTuneAngle;
    }

    @Override
    public int getTargetPosition() {
        return targetPosition;
    }
}
