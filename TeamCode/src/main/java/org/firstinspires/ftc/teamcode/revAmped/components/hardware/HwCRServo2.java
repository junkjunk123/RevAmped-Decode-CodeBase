package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class HwCRServo2<T extends Enum<T> & CRServoInternal.ServoState> extends CRServoInternal<T> {


    protected HwCRServo2(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction, T motorState, Encoder encoder) throws IllegalArgumentException {
        super(hardwareMap, id, direction, motorState, encoder);
    }

    public abstract int ticksPerRotation();

    public double getError(int targetPosition) {
        return (((targetPosition - getCurrentPosition()) % ticksPerRotation()) + ticksPerRotation() * 1.5) % ticksPerRotation() - ticksPerRotation() / 2.0;
    }

    public static double getErrorInfiniteRotation(int targetPosition, int currentPos, int ticksPerRotation) {
        return (((targetPosition - currentPos) % ticksPerRotation) + ticksPerRotation * 1.5) % ticksPerRotation - ticksPerRotation / 2.0;
    }

    public abstract double getLastError();

    public abstract double getVelocity();

    public abstract void reverse();

    public abstract void finetune(int ticks);
}
